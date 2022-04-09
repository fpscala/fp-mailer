ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"
val log4catsV      = "2.2.0"
val logbackV       = "1.2.11"
val log4cats    = "org.typelevel" %% "log4cats-slf4j"  % log4catsV
val logback     = "ch.qos.logback" % "logback-classic" % logbackV

lazy val root = (project in file("."))
  .settings(
    name := "Mailer",
    libraryDependencies ++= Seq(log4cats, logback, "javax.mail" % "mail" % "1.4.7")
  )
