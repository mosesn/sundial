import sbt._
import Keys._

object Sundial extends Build {
  val sharedSettings = Seq(
    organization := "com.mosesn",
    name := "sundial",
    scalaVersion := "2.10.2",
    crossScalaVersions := Seq("2.9.3", "2.10.2")
  )

  val testDeps = Seq(
    "org.scalatest" %% "scalatest" % "1.9.1" % "test"
  )

  lazy val sundial = Project(
    id = "sundial",
    base = file(".")
  )
    .aggregate(core)
    .settings(sharedSettings: _*)

  def project(name: String) = {
    val string = "sundial-%s" format name
    Project(
      id = string,
      base = file(string)
    )
      .settings(sharedSettings: _*)
      .settings(libraryDependencies ++= testDeps)
  }

  lazy val core = project("core")
    .settings(libraryDependencies += "com.twitter" %% "util-core" % "6.3.7")
}
