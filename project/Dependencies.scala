import Dependencies.Libraries._
import sbt._

object Dependencies {
  object Versions {
    val cats       = "2.7.0"
    val catsEffect = "3.3.8"
    val logback    = "1.2.11"
    val log4cats   = "2.2.0"
    val newType    = "0.4.4"
    val mailer     = "1.4.7"
    val catsRetry  = "3.1.0"
  }

  object Libraries {
    val mailer     = "javax.mail"        % "mail"        % Versions.mailer
    val cats       = "org.typelevel"    %% "cats-core"   % Versions.cats
    val catsEffect = "org.typelevel"    %% "cats-effect" % Versions.catsEffect
    val catsRetry  = "com.github.cb372" %% "cats-retry"  % Versions.catsRetry
    val newType    = "io.estatico"      %% "newtype"     % Versions.newType

    val logback = "ch.qos.logback" % "logback-classic" % Versions.logback

    val log4cats = "org.typelevel" %% "log4cats-slf4j" % Versions.log4cats
  }
  val libraries: Seq[ModuleID] = Seq(cats, catsEffect, catsRetry, log4cats, logback, newType, mailer)
}
