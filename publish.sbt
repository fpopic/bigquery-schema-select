import sbt.url

ThisBuild / description := "Generates SQL query that selects all fields (recursively for nested fields) from the provided BigQuery schema file."
ThisBuild / licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/fpopic/bigquery-schema-select"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/fpopic/bigquery-schema-select"),
    "scm:git@github.com:fpopic/bigquery-schema-select.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "fpopic",
    name = "Filip Popic",
    email = "filip.popic@gmail.com",
    url = url("https://github.com/fpopic")
  )
)

ThisBuild / publishMavenStyle := true
ThisBuild / resolvers += Opts.resolver.sonatypeSnapshots
ThisBuild / pomIncludeRepository := { _ =>
  false
}

ThisBuild / publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

ThisBuild / useGpg := false
ThisBuild / pgpPublicRing := Path.userHome / ".gnupg" / "pubring.gpg"
ThisBuild / pgpSecretRing := Path.userHome / ".gnupg" / "secring.gpg"
ThisBuild / pgpSigningKey := Some(0xD4EC12FAAE369F81L) // Just ID of the key used, not the key itself.
