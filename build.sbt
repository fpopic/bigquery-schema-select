name := "bigquery-schema-select"
version := "0.1"
scalaVersion := "2.13.2"

Global / onChangedBuildSource := IgnoreSourceChanges

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.8.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.1.0" % Test

mainClass in assembly := Some("com.github.fpopic.bigqueryschemaselect.Main")
test in assembly := {}

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}
