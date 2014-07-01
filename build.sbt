

import com.typesafe.sbt.SbtStartScript

name := "pada1"

organization := "com.madmode"

version := "0.1.2"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.2.0" % "test"

libraryDependencies += "org.jsoup" % "jsoup" % "1.7.3"

// per spray.io

scalaVersion := "2.10.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.1.4"
  val sprayV = "1.1.1"
  Seq(
    "io.spray" % "spray-can" % sprayV,
    "io.spray" % "spray-routing" % sprayV,
    "io.spray" % "spray-testkit" % sprayV % "test",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-testkit" % akkaV % "test",
    "org.specs2" %% "specs2" % "2.2.3" % "test"
  )
}

Revolver.settings


// This SbtStartScript incantation supports deployment via heroku[7].
// It requires an import, which has to go at the top of the file.
//
// [7]: https://devcenter.heroku.com/articles/getting-started-with-scala

seq(SbtStartScript.startScriptForClassesSettings: _*)

