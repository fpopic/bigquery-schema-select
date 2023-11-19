Global / onChangedBuildSource := ReloadOnSourceChanges

ThisBuild / organization := "com.github.fpopic"
ThisBuild / version := "0.4"
ThisBuild / scalaVersion := "2.13.9"

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.10.0",
      "org.scalatest" %% "scalatest" % "3.1.0" % Test
    ),
    // Assembly settings
    assembly / mainClass := Some("com.github.fpopic.bigqueryschemaselect.Main"),
    assembly / test := {},
    assembly / assemblyMergeStrategy := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    },
    assembly / assemblyJarName :=
      s"${name.value}_${CrossVersion.binaryScalaVersion(scalaVersion.value)}-${version.value}.jar",
    // Publish assembly
    //   https://github.com/sbt/sbt-assembly#publishing-not-recommended
    publish / skip := true,
    Compile / assembly / artifact := {
      val art = (Compile / assembly / artifact).value
      art.withClassifier(Some("assembly"))
    },
    addArtifact(Compile / assembly / artifact, assembly)
  )

lazy val wrapper = project
  .settings(
    name := "bigquery-schema-select",
    Compile / packageBin := (root / Compile / assembly).value
  )
