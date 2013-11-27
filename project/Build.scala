import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "sbscala"
    val appVersion      = "0.1-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "com.typesafe.akka" % "akka-actor"      % "2.0.3",
      "com.typesafe.akka" % "akka-testkit"    % "2.0.3"  % "test",
      "org.scala-lang" % "scala-swing" % "2.9.1",
      //"com.googlecode.blacken" % "blacken-core" % "1.1.1"
      "junit"             % "junit"           % "4.5"             % "test",
      "org.scalatest"     % "scalatest_2.9.0" % "1.6.1"           % "test",
      "org.specs2" %% "specs2" % "1.12.3" % "test"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
      // Add your own project settings here 
      testOptions in Test := Nil, 
      organization := "org.mixolyde",
      scalaVersion := "2.9.1"
    )

}
