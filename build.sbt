sbtPlugin := true

name := "sbt-mongoseed"

organization := "com.thomaschoo.sbt"

version := "0.1"

scalaVersion := "2.11.5"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "reactivemongo" % "0.10.5.0.akka23"
)

publishMavenStyle := false

initialCommands in console := "import com.thomaschoo.sbt.mongoseed._"
