Global / onChangedBuildSource := IgnoreSourceChanges

ThisBuild / name := "bigquery-schema-select"
ThisBuild / version := "0.1"
ThisBuild / scalaVersion := "2.13.2"
ThisBuild / libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)

ThisBuild / mainClass in assembly := Some("com.github.fpopic.bigqueryschemaselect.Main")
ThisBuild / test in assembly := {}
assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _                        => MergeStrategy.first
}
