package mailer
import javax.mail.internet.MimeMultipart
import javax.mail.{MessagingException, Session, Transport}

/**
	* Represents the ''Mailer'' itself, with methods for opening/closing the connection and sending
	*/
trait Mailer {

	/**
		* Creates new transport connection.
		*/
	@throws[MessagingException]
	def connect(): Mailer

	/**
		* Sends the given message.
		*
		* @param msg message to send
		*/
	@throws[MessagingException]
	def send(msg: Message): Mailer

	/**
		* Returns the instance of `javax.mail.Transport`, used by this instance of 'Mailer'
		*
		* @return instance of `javax.mail.Transport`
		*/
	def transport: Transport

	/**
		* Closes the previously opened transport connection.
		*
		* @return
		*/
	@throws[MessagingException]
	def close(): Mailer
}


/**
 * ''Mailer'' object providing default operations to handle the transport connection and send the
 * e-mail message.
 *
 * @author jubu
 */
object Mailer {

  import MailKeys._

  /**
   * Sets the ''JavaMail'' session to the ''Mailer'' and returns the instance ready to send e-mail
   * messages. Optionally, transport method can be explicitly specified.
   *
   * @param session      ''JavaMail'' session
   * @param transportOpt transport method (optional)
   * @return ''Mailer'' instance
   */
  def apply(session: Session, transportOpt: Option[Transport] = None): Mailer = new Mailer {


    val trt: Transport = transportOpt match {
      case None => if (session.getProperty(TransportProtocolKey) == null) {
        println("::::")
        session.getTransport("smtp")
      } else {
        println(":::::")
        session.getTransport
      }
      case Some(t) =>
        println("::::::")
        t
    }

    @throws[MessagingException]
    override def connect(): Mailer = {
      if (!trt.isConnected) {
        println(":")
        trt.connect()
      }
      this
    }

    @throws[MessagingException]
    override def send(msg: Message): Mailer = {
      import javax.mail.{Message => M}
      connect()
      val message = new MimeMessage(session)
      println("::")
      msg.to.foreach(message.addRecipient(M.RecipientType.TO, _))
      msg.cc.foreach(message.addRecipient(M.RecipientType.CC, _))
      msg.bcc.foreach(message.addRecipient(M.RecipientType.BCC, _))
      msg.headers.foreach(header => message.setHeader(header.name, header.value))
      message.setSubject(msg.subject)
      message.setFrom(msg.from)
      message.setContent(new MimeMultipart() {
        msg.content.parts.foreach(addBodyPart(_))
      })
      println(":::")
      trt.sendMessage(message, message.getAllRecipients)
      this
    }

    override def transport: Transport = trt

    @throws[MessagingException]
    override def close(): Mailer = {
      if (trt.isConnected) {
        trt.close()
      }
      this
    }
  }
}