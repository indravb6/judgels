import Versions._

import de.johoop.testngplugin.TestNGPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

lazy val jophielcommons = (project in file("."))
    .enablePlugins(PlayJava, SbtWeb)
    .disablePlugins(plugins.JUnitXmlReportPlugin)
    .dependsOn(playcommons, api, jophielApi)
    .aggregate(playcommons, api, jophielApi)
    .settings(
        name := "jophielcommons",
        scalaVersion := sbtScalaVersion,
        libraryDependencies ++= Seq(
            "com.nimbusds" % "c2id-server-sdk" % "2.0"
        ),
        routesGenerator := InjectedRoutesGenerator
    )
    .settings(TestNGPlugin.testNGSettings: _*)
    .settings(
        aggregate in test := false,
        aggregate in jacoco.cover := false,
        TestNGPlugin.testNGSuites := Seq("test/resources/testng.xml")
    )
    .settings(jacoco.settings: _*)
    .settings(
        parallelExecution in jacoco.Config := false
    )
    .settings(
        publishArtifact in (Compile, packageDoc) := false,
        publishArtifact in packageDoc := false,
        sources in (Compile,doc) := Seq.empty
    )

lazy val playcommons = RootProject(file("../play-commons"))
lazy val api = RootProject(file("../api"))

lazy val jophielApi = RootProject(file("../../judgels-backends/jophiel/jophiel-api"))
