sbtPlugin := true

name := "sbt-mongoseed"

organization := "com.thomaschoo.sbt"

version := "1.0.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation", "-feature")

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "org.mongodb" %% "casbah" % "2.7.4",
  "com.typesafe" % "config" % "1.2.1"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  Some(if (isSnapshot.value) {
    "snapshots" at nexus + "content/repositories/snapshots"
  } else {
    "releases" at nexus + "service/local/staging/deploy/maven2"
  })
}

pomExtra :=
  <url>https://github.com/thomaschoo/sbt-mongoseed</url>
  <licenses>
    <license>
      <name>MIT</name>
      <url>http://opensource.org/licenses/MIT</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:thomaschoo/sbt-mongoseed.git</url>
    <connection>scm:git:git@github.com:thomaschoo/sbt-mongoseed.git</connection>
  </scm>
  <developers>
    <developer>
      <id>thomaschoo</id>
      <name>Thomas Choo</name>
      <url>https://github.com/thomaschoo</url>
    </developer>
  </developers>
