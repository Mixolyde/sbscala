//sbscala plugin setup for sbt

scalaVersion := "2.9.1"

// Comment to get more information during initialization
logLevel := Level.Warn

// The Typesafe repository 
resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

// Use the Play sbt plugin for Play projects
addSbtPlugin("play" % "sbt-plugin" % "2.0.2")

// generates a start-script for calling the main app from command line
//addSbtPlugin("com.typesafe.sbt" % "sbt-start-script" % "0.7.0")