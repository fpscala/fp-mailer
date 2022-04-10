package mailer

import mailer.types.Subtype
import mailer.types.Subtype.{HTML, PLAIN}

import java.nio.charset.{Charset, StandardCharsets}

sealed trait Body {
  def value: String
  def charset: Charset
  def subtype: Subtype
}

object Body {

  case class Text(
    value: String,
    charset: Charset = StandardCharsets.UTF_8,
    subtype: Subtype = PLAIN,
    headers: List[Header] = Nil
  ) extends Body

  case class Html(
    value: String,
    charset: Charset = StandardCharsets.UTF_8,
    subtype: Subtype = HTML,
    headers: List[Header] = Nil
  ) extends Body

}
