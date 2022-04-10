package retries

import cats.Applicative
import cats.effect.Temporal
import exception.TimeOutError
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry._

import java.net.UnknownHostException
import javax.mail.MessagingException

trait Retry[F[_]] {
  def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A]
}

object Retry {
  def apply[F[_]: Retry]: Retry[F] = implicitly

  implicit def forLoggerTemporal[F[_]: Logger: Temporal]: Retry[F] =
    new Retry[F] {
      def retry[A](policy: RetryPolicy[F])(fa: F[A]): F[A] = {
        def onError(e: Throwable, details: RetryDetails): F[Unit] =
          details match {
            case WillDelayAndRetry(_, retriesSoFar, _) =>
              Logger[F].error(
                s"Failed to process send email with ${e.getMessage}. So far we have retried $retriesSoFar times."
              )
            case GivingUp(totalRetries, _) =>
              Logger[F].error(s"Giving up on send email after $totalRetries retries.")
          }

        def isWorthRetrying: Throwable => F[Boolean] = {
          case _: TimeOutError => Applicative[F].pure(true)
          case exception: MessagingException =>
            exception.getCause match {
              case _: UnknownHostException => Applicative[F].pure(true)
              case _                       => Applicative[F].pure(false)
            }

          case _ => Applicative[F].pure(false)
        }

        retryingOnSomeErrors[A](policy, isWorthRetrying, onError)(fa)
      }
    }
}
