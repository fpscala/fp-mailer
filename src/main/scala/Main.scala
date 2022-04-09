import mailer._

import javax.mail.Session
import javax.mail.internet.InternetAddress
import scala.util.{Failure, Success, Try}

object Main extends App {
  val to = "monitoring@scala.uz"
  val from = "kim799186@gmail.com"
  val password="Maftunbek1998"
  val session: Session = (SmtpAddress("smtp.gmail.com", 587) :: SmtpStartTls() :: Debug(true) :: SmtpTimeout(30000) :: SessionFactory()).session(Some(from -> password))

  val content: Content = Content().html("<b>HTML</b> content part")
  val msg: Message = Message(
    from = new InternetAddress("Medical Monitoring <monitoring@scala.uz>"),
    subject = "my subject",
    content = content,
    to = Seq(new InternetAddress(to)))
  val mailer: Mailer = Mailer(session)
  Try{
    mailer.send(msg)
  } match {
    case Failure(exception) =>
      mailer.close()
      println(exception)
    case Success(value) =>
      mailer.close()
      println(value)
  }

}
