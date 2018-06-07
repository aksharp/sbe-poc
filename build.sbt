name := "sbe-poc"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "uk.co.real-logic" % "sbe-all" % "1.8.1",
  "uk.co.real-logic" % "Agrona" % "0.4.12",
  "com.typesafe" % "config" % "1.3.3",
  "io.circe" %% "circe-core" % "0.9.3",
  "io.circe" %% "circe-parser" % "0.9.3",
  "io.circe" %% "circe-generic" % "0.9.3",
  "io.circe" %% "circe-generic-extras" % "0.9.3",
  "joda-time" % "joda-time" % "2.10",
  "org.scalatest" %% "scalatest" % "3.0.5" % Test
)