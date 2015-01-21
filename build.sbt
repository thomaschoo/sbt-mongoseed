sbtPlugin := true

name := "sbt-mongoseed"

organization := "com.thomaschoo.sbt"

version := "0.1.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.7.4",
  "com.typesafe" % "config" % "1.2.1"
)

publishMavenStyle := false

initialCommands in console := "import com.thomaschoo.sbt.mongoseed._"
