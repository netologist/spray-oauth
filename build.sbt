import AssemblyKeys._

lazy val commonSettings = Seq(
  organization := "com.hasanozgan",
  version := "1.0-SNAPSHOT",
  scalaVersion  := "2.10.3",
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:existentials"))

lazy val settings = (
  commonSettings
  ++ scalariformSettings
  ++ org.scalastyle.sbt.ScalastylePlugin.Settings
  ++ assemblySettings)

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  publishTo := {
    val nexus = "https://oss.sonatype.org/"
    if (isSnapshot.value)
      Some("snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("releases"  at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <url>http://github.com/hasanozgan/spray-oauth2</url>
      <licenses>
        <license>
          <name>Apache 2</name>
          <url>http://www.apache.org/licenses/LICENSE-2.0</url>
          <distribution>repo</distribution>
        </license>
      </licenses>
      <scm>
        <url>git@github.com:hasanozgan/spray-oauth2.git</url>
        <connection>scm:git@github.com:hasanozgan/spray-oauth2.git</connection>
      </scm>
      <developers>
        <developer>
          <id>hasanozgan</id>
          <name>Hasan Ozgan</name>
          <url>http://github.com/hasanozgan</url>
        </developer>
      </developers>))

lazy val root = project.in( file(".") ).aggregate(core, salatAdapter, slickAdapter)

lazy val core = project.in(file("core"))
  .settings(settings: _*)
  .settings(publishSettings: _*)
  .settings(test in assembly := {})
  .settings(testOptions in Test += Tests.Argument("showtimes", "true"))

lazy val salatAdapter = project.in(file("adapters/salat-adapter"))
  .dependsOn(core)
  .settings(settings: _*)
  .settings(test in assembly := {})
  .settings(testOptions in Test += Tests.Argument("showtimes", "true"))

lazy val slickAdapter = project.in(file("adapters/slick-adapter"))
  .dependsOn(core)
  .settings(settings: _*)
  .settings(test in assembly := {})
  .settings(testOptions in Test += Tests.Argument("showtimes", "true"))

