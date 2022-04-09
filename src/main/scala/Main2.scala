import javax.mail.{Message, MessagingException, PasswordAuthentication, Session, Transport}
import javax.mail.internet._
object Main2 extends App {

  val to = "kim799186@gmail.com"
  val to2 = "isurojiddin@gmail.com"
//  val to = "monitoring@scala.uz"
//  val from = "kim799186@gmail.com"
  val from = "monitoring@scala.uz"
//  val password="Maftunbek1998"
  val password="SPyXhM7dmPD5803qdJC5"
  val port="587"
  val host="smtp.mail.ru"
  val properties = System.getProperties
  properties.put("mail.smtp.host", host)
  properties.put("mail.smtp.starttls.enable", "true")
  properties.put("mail.smtp.ssl.protocols", "TLSv1.2")
  properties.put("mail.smtp.starttls.required", "true")
  properties.put("mail.transport.protocol", "smtp")
  properties.put("mail.debug", "true")
  properties.put("mail.smtp.port", port)
  properties.put("mail.smtp.auth", "true")
  val session = Session.getDefaultInstance(properties,
    new javax.mail.Authenticator() {
      protected override def getPasswordAuthentication: PasswordAuthentication  = {
        new PasswordAuthentication(from, password)
      }
    })

  try{
    val message = new MimeMessage(session)
    message.setFrom(new InternetAddress(from))
    message.addRecipient(Message.RecipientType.TO,new InternetAddress(to2))
    message.setSubject("Ping")
    message.setText("Hello, this is example of sending email  ")

    // Send message
    Transport.send(message)
    System.out.println("message sent successfully....")

  } catch  {
    case error: MessagingException =>
      println(error)
  }
}
