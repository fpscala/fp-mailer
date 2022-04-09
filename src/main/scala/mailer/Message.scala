package mailer
import javax.mail.internet.InternetAddress

/** Represents the e-mail message itself.
  *
  * @param from
  *   e-mail sender address
  * @param subject
  *   e-mail subject text
  * @param content
  *   e-mail content,
  * @param to
  *   set of e-mail receiver addresses
  * @param cc
  *   set of e-mail ''carbon copy'' receiver addresses
  * @param bcc
  *   set of e-mail ''blind carbon copy'' receiver addresses
  * @param replyTo
  *   address used to reply this message (optional)
  * @param replyToAll
  *   whether the new message will be addressed to all recipients of this message
  * @param headers
  *   message headers (''RFC 822'')
  */
case class Message(
  from: InternetAddress,
  subject: String,
  content: Content,
  to: Seq[InternetAddress] = Seq.empty[InternetAddress],
  cc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  bcc: Seq[InternetAddress] = Seq.empty[InternetAddress],
  replyTo: Option[InternetAddress] = None,
  replyToAll: Option[Boolean] = None,
  headers: Seq[MessageHeader] = Seq.empty[MessageHeader]
)
