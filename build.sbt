name := "AkkaServices"

version := "1.0"

lazy val root = (project in file(".")).configs(Test)

scalaVersion := "2.11.7"

val AkkaVersion = "2.5.11"

resolvers += "Akka Snapshot Repository" at "http://repo.akka.io/snapshots/"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % AkkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % AkkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)

ideaExcludeFolders += ".idea"

ideaExcludeFolders += ".idea_modules"
