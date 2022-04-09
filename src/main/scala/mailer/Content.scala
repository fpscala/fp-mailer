package mailer

import java.io.File
import javax.activation.{DataHandler, FileDataSource}
import javax.mail.MessagingException
import javax.mail.internet.{MimeBodyPart, MimeMultipart, PreencodedMimeBodyPart}
import javax.mail.util.ByteArrayDataSource


/**
	* Represents the content of the e-mail message, composed of individual `MimeBodyPart` instances.
	* For easier use, helper methods to add specific content are available, such as `html()` for
	* adding ''HTML'' or `attachFile()` to add file attachment.
	*
	* @param parts parts of the message content (represented by `MimeBodyPart` instances)
	*/
case class Content(parts: MimeBodyPart*) {

	/**
		* Appends the given part (represented by `MimeBodyPart` instance) to the existing content parts.
		*
		* @param parts content part to append
		*/
	def append(parts: MimeBodyPart*): Content = Content(this.parts ++ parts: _*)

	/**
		* Appends the given string as the text content part (defaults to ''text/plain'').
		*
		* @param text    text to append
		* @param charset charset of the given text (defaults to ''UTF-8'')
		* @param subtype defines subtype of the ''MIME type'' (the part after the slash), defaults
		*                to ''UTF-8''
		* @param headers content part headers (''RFC 822'')
		*/
	def text(text: String, charset: String = "UTF-8", subtype: String = "plain",
					 headers: Seq[MessageHeader] = Seq.empty[MessageHeader]): Content = {

		val part = new MimeBodyPart()
		part.setText(text, charset, subtype)
		headers.foreach(header => part.setHeader(header.name, header.value))
		append(part)
	}

	/**
		* Appends the given ''HTML'' string as the new ''HTML'' content part.
		*
		* @param html    ''HTML'' string to append
		* @param charset charset of the given ''HTML'' string (defaults to ''UTF-8'')
		* @param headers content part headers (''RFC 822'')
		*/
	def html(html: String, charset: String = "UTF-8",
					 headers: Seq[MessageHeader] = Seq.empty[MessageHeader]): Content = {

		val part = new MimeBodyPart()
		part.setText(html, charset, "html")
		headers.foreach(header => part.setHeader(header.name, header.value))
		append(part)
	}

	/**
		* Appends the given file as the e-mail message attachment.
		*
		* @param file      file to attach
		* @param name      name of the attachment (optional, defaults to the given file name)
		* @param contentId the "Content-ID" header field of this body part
		* @param headers   content part headers (''RFC 822'')
		*/

	def attachFile(file: File, name: Option[String] = None,
								 contentId: Option[String] = None,
								 headers: Seq[MessageHeader] = Seq.empty[MessageHeader]): Content = {

		val part = new MimeBodyPart()
		contentId.foreach(part.setContentID)
		part.setDataHandler(new DataHandler(new FileDataSource(file)))
		part.setFileName(name.getOrElse(file.getName))
		headers.foreach(header => part.setHeader(header.name, header.value))
		append(part)
	}

	/**
		* Appends the given array of bytes as the e-mail message attachment. Useful especially when the
		* original file object is not available, only its array of bytes.
		*
		* @param bytes     array of bytes representing the attachment
		* @param mimeType  ''MIME type'' of the attachment
		* @param name      name of the attachment (optional)
		* @param contentId the "Content-ID" header field of this body part (optional)
		* @param headers   content part headers (''RFC 822'')
		*/
	def attachBytes(bytes: Array[Byte], mimeType: String, name: Option[String] = None,
									contentId: Option[String] = None,
									headers: Seq[MessageHeader] = Seq.empty[MessageHeader]): Content = {

		val part = new MimeBodyPart()
		contentId.foreach(part.setContentID)
		part.setDataHandler(new DataHandler(new ByteArrayDataSource(bytes, mimeType)))
		name.foreach(part.setFileName)
		headers.foreach(header => part.setHeader(header.name, header.value))
		append(part)
	}


	/**
		* Appends the given string of ''Base64-encoded'' data as the e-mail message attachment. Use
		* instead of the `#attachBytes` method if you have already ''Base64-encoded'' data and you
		* want to avoid ''JavaMail'' encoding it again.
		*
		* @param data      ''Base64-encoded'' data
		* @param mimeType  ''MIME type'' of the attachment
		* @param name      name of the attachment (optional)
		* @param contentId the `Content-ID` header field of this body part (optional)
		* @param headers   content part headers (''RFC 822'')
		* @see http://www.oracle.com/technetwork/java/faq-135477.html#preencoded
		*/
	def attachBase64(data: String, mimeType: String, name: Option[String] = None,
									 contentId: Option[String] = None,
									 headers: Seq[MessageHeader] = Seq.empty[MessageHeader]): Content = {

		val part = new PreencodedMimeBodyPart("base64")
		contentId.foreach(part.setContentID)
		part.setDataHandler(new DataHandler(new ByteArrayDataSource(data, mimeType)))
		name.foreach(part.setFileName)
		headers.foreach(header => part.setHeader(header.name, header.value))
		append(part)
	}


	@throws[MessagingException]
	def apply(): MimeMultipart = {
		val prts = parts
		new MimeMultipart() {
			prts.foreach(addBodyPart(_))
		}
	}
}


