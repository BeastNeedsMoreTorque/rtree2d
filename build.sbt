import com.typesafe.sbt.pgp.PgpKeys._
import sbt.Keys.scalacOptions
import sbt.url

lazy val commonSettings = Seq(
  organization := "com.sizmek.rtree2d",
  organizationHomepage := Some(url("https://sizmek.com")),
  homepage := Some(url("https://github.com/Sizmek/rtree2d")),
  licenses := Seq(("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))),
  startYear := Some(2018),
  developers := List(
    Developer(
      id = "loony-bean",
      name = "Alexey Suslov",
      email = "alexey.suslov@sizmek.com",
      url = url("https://github.com/loony-bean")
    ),
    Developer(
      id = "AnderEnder",
      name = "Andrii Radyk",
      email = "andrii.radyk@sizmek.com",
      url = url("https://github.com/AnderEnder")
    ),
    Developer(
      id = "plokhotnyuk",
      name = "Andriy Plokhotnyuk",
      email = "andriy.plokhotnyuk@sizmek.com",
      url = url("https://twitter.com/aplokhotnyuk")
    ),
  ),
  resolvers += Resolver.jcenterRepo,
  scalaVersion := "2.12.6",
  crossScalaVersions := Seq("2.13.0-M3", "2.12.6", "2.11.12"),
  scalacOptions ++= Seq(
    "-deprecation",
    "-encoding", "UTF-8",
    "-feature",
    "-unchecked",
    "-Yno-adapted-args",
    "-Ywarn-dead-code",
    "-Xfuture",
    "-Xlint"
  ),
  testOptions in Test += Tests.Argument("-oDF"),
  parallelExecution in ThisBuild := false
)

lazy val noPublishSettings = Seq(
  skip in publish := true,
  publishArtifact := false,
  // Replace tasks to work around https://github.com/sbt/sbt-bintray/issues/93
  bintrayRelease := ((): Unit),
  bintrayEnsureBintrayPackageExists := ((): Unit),
  bintrayEnsureLicenses := ((): Unit),
)

lazy val publishSettings = Seq(
  bintrayOrganization := Some("sizmek"),
  bintrayRepository := "sizmek-maven",
  scmInfo := Some(
    ScmInfo(
      url("https://github.com/Sizmek/rtree2d"),
      "scm:git@github.com:Sizmek/rtree2d.git"
    )
  ),
  publishMavenStyle := true,
  pomIncludeRepository := { _ => false }
)

lazy val rtree2d = project.in(file("."))
  .aggregate(core, benchmark)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)

lazy val core = project
  .settings(commonSettings: _*)
  .settings(publishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-reflect" % scalaVersion.value,
      "org.scalacheck" %% "scalacheck" % "1.14.0" % Test,
      "org.scalatest" %% "scalatest" % "3.0.5-M1" % Test
    )
  )

lazy val benchmark = project
  .enablePlugins(JmhPlugin)
  .dependsOn(core)
  .settings(commonSettings: _*)
  .settings(noPublishSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "pl.project13.scala" % "sbt-jmh-extras" % "0.3.4",
      "org.scalatest" %% "scalatest" % "3.0.5-M1" % Test
    )
  )