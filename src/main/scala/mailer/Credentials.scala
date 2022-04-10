package mailer

import mailer.types.{EmailAddress, Password}

case class Credentials (user: EmailAddress, password: Password)
