Global / onChangedBuildSource := ReloadOnSourceChanges

val commonSettings = Seq(
  name := "bigquery-schema-select",
  organization := "com.github.fpopic",
  version := "0.2-SNAPSHOT",
  scalaVersion := "2.13.2"
)

lazy val root = (project in file("."))
  .enablePlugins(AssemblyPlugin)
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.play" %% "play-json" % "2.8.1",
      "org.scalatest" %% "scalatest" % "3.1.0" % Test
    ),
    // Assembly settings
    mainClass in assembly := Some("com.github.fpopic.bigqueryschemaselect.Main"),
    test in assembly := {},
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", _*) => MergeStrategy.discard
      case _                        => MergeStrategy.first
    },
    assemblyJarName in assembly :=
      s"${name.value}_${CrossVersion.binaryScalaVersion(scalaVersion.value)}-${version.value}.jar",
    // Publish assembly
    //   https://github.com/sbt/sbt-assembly#publishing-not-recommended
    skip in publish := true,
    artifact in (Compile, assembly) := {
      val art = (artifact in (Compile, assembly)).value
      art.withClassifier(Some("assembly"))
    },
    addArtifact(artifact in (Compile, assembly), assembly)
  )

lazy val wrapper = project
  .settings(
    commonSettings,
    packageBin in Compile := (assembly in (root, Compile)).value
  )
