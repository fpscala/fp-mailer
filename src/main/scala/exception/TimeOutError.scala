package exception

sealed abstract class TimeOutError extends Throwable {
  def cause: String
  override def getMessage: String = cause
}

object TimeOutError {
  case class ConnectionTimeOut(cause: String) extends TimeOutError
  case class ReadTimeOut(cause: String) extends TimeOutError
}
