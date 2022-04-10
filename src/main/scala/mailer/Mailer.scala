package mailer

import cats.effect.kernel.Temporal
import cats.effect.{Async, Sync}
import cats.implicits._
import exception.DeliverFailure.AuthenticationFailed
import exception.InvalidAddress
import mailer.Props.SmtpConnectionTimeoutKey
import org.typelevel.log4cats.Logger
import retries.Retry
import retry.RetryPolicies.{exponentialBackoff, limitRetries}
import retry.RetryPolicy

import java.util.Properties
import javax.mail.Message.RecipientType._
import javax.mail._
import javax.mail.internet.{InternetAddress, MimeMessage}
import scala.concurrent.duration.DurationInt
import scala.jdk.CollectionConverters.MapHasAsJava

trait Mailer[F[_]] {
  def send(email: Email): F[Unit]

}
object Mailer {
  def apply[F[_]: Async: Logger: Temporal](props: Props, credentials: Credentials): Mailer[F] =
    new MailerImpl[F](props, credentials)

  class MailerImpl[F[_]: Async: Logger: Temporal](props: Props, credentials: Credentials) extends Mailer[F] {
    private[mailer] val retryPolicy: RetryPolicy[F] = {
      val delay = props.values.get(SmtpConnectionTimeoutKey).fold(1.second)(_.toInt.millis)
      limitRetries[F](3) |+| exponentialBackoff[F](delay)
    }

    private[mailer] val properties: Properties = {
      val properties = System.getProperties
      properties.putAll(props.values.asJava)
      properties
    }

    private[mailer] val authenticator: F[Authenticator] =
      Sync[F].delay(
        new Authenticator {
          override def getPasswordAuthentication: PasswordAuthentication = {
            new PasswordAuthentication(credentials.user.value, credentials.password.value)
          }
        }
      )

    private[mailer] def session(properties: Properties, auth: Authenticator): F[Session] =
      Sync[F].delay(Session.getDefaultInstance(properties, auth))

    private[mailer] def prepareMessage(session: Session, email: Email): MimeMessage = {
      val message = new MimeMessage(session)
      message.setFrom(new InternetAddress(email.from.value))
      email.to.map(ads => message.addRecipient(TO, new InternetAddress(ads.value)))
      email.cc.foreach(ads => message.addRecipient(CC, new InternetAddress(ads.value)))
      email.bcc.foreach(ads => message.addRecipient(BCC, new InternetAddress(ads.value)))
      message.setSubject(email.subject)
      message.setText(email.content.value, email.content.charset.toString, email.content.subtype.value)
      message
    }

    override def send(email: Email): F[Unit] =
      for {
        auth    <- authenticator
        session <- session(properties, auth)
        message = prepareMessage(session, email)
        task    = Sync[F].delay(Transport.send(message))
        result <- Retry[F]
          .retry(retryPolicy)(task)
          .adaptError {
            case exception: AuthenticationFailedException =>
              AuthenticationFailed(exception.getMessage)
            case exception: SendFailedException =>
              InvalidAddress(exception.getMessage)
          }
      } yield result

  }
}
