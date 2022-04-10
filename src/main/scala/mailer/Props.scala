package mailer

import mailer.Props._
import mailer.types.Protocol.Smtp
import mailer.types._

import scala.concurrent.duration.{DurationInt, FiniteDuration}

final case class Props(values: Map[String, String] = defaultProps) {
  def withSmtpAddress(host: Host, port: Port): Props =
    copy(values = values ++ Map(SmtpHostKey -> host.value, SmtpPortKey -> port.value.toString))

  def setConnectionTimeout(timeout: FiniteDuration): Props =
    copy(values = values ++ Map(SmtpConnectionTimeoutKey -> timeout.toMillis.toString))

  def setSmtpTimeout(timeout: FiniteDuration): Props =
    copy(values = values ++ Map(SmtpTimeoutKey -> timeout.toMillis.toString))

  def withTls(enable: Boolean = true, required: Boolean = false): Props =
    copy(values =
      values ++ Map(
        SmtpStartTlsEnableKey   -> enable.toString,
        SmtpStartTlsRequiredKey -> required.toString
      )
    )

  def setProtocol(protocol: Protocol): Props =
    copy(values = values ++ Map(TransportProtocolKey -> protocol.value))

  def withDebug(debug: Boolean = false): Props =
    copy(values = values ++ Map(DebugKey -> debug.toString))

  def withAuth(enable: Boolean = true): Props =
    copy(values = values ++ Map(SmtpAuthKey -> enable.toString))

  def set(key: String, value: String): Props =
    copy(values = values ++ Map(key -> value))
}

object Props {
  private[mailer] val DebugKey                 = "mail.debug"
  private[mailer] val SmtpConnectionTimeoutKey = "mail.smtp.connectiontimeout"
  private[mailer] val SmtpHostKey              = "mail.smtp.host"
  private[mailer] val SmtpPortKey              = "mail.smtp.port"
  private[mailer] val SmtpStartTlsEnableKey    = "mail.smtp.starttls.enable"
  private[mailer] val SmtpSslProtocolKey       = "mail.smtp.ssl.protocols"
  private[mailer] val SmtpStartTlsRequiredKey  = "mail.smtp.starttls.required"
  private[mailer] val SmtpTimeoutKey           = "mail.smtp.timeout"
  private[mailer] val TransportProtocolKey     = "mail.transport.protocol"
  private[mailer] val SmtpAuthKey              = "mail.smtp.auth"
  private[mailer] val defaultProps =
    Map(
      SmtpHostKey              -> "localhost",
      SmtpPortKey              -> "25",
      DebugKey                 -> "false",
      SmtpConnectionTimeoutKey -> 3.seconds.toMillis.toString,
      SmtpTimeoutKey           -> 30.seconds.toMillis.toString,
      SmtpStartTlsEnableKey    -> "true",
      SmtpSslProtocolKey       -> "TLSv1.2",
      SmtpStartTlsRequiredKey  -> "true",
      SmtpStartTlsRequiredKey  -> "true",
      TransportProtocolKey     -> Smtp.value,
      SmtpAuthKey              -> "true"
    )
}
