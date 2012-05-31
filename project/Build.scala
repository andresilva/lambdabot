import sbt._
import Keys._
import sbtassembly.Plugin._
import AssemblyKeys._

object Build extends Build {
  val botDirectory = SettingKey[File]("bot-directory")
  val play = TaskKey[Unit]("play")

  val bot = Project(
    id = "lambdabot",
    base = file("."),
    settings = Project.defaultSettings ++ botSettings ++ assemblySettings)

  val botSettings = Seq[Setting[_]](
      organization := "lambdabot",
      name := "lambdabot",
      version := "1.0.0-SNAPSHOT",

      scalaVersion := "2.9.1",
      scalacOptions ++= Seq("-deprecation", "-unchecked"),

      javaOptions += "-Xmx1g",

      libraryDependencies ++= Seq(
        "org.scalaz"   %% "scalaz-core" % "7.0-SNAPSHOT"
      ),

      resolvers += "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",

      botDirectory := file("bots"),

      play <<= (botDirectory, name, javaOptions, unmanagedClasspath in Compile, Keys.`package` in Compile) map { (bots, name, javaOptions, ucp, botJar) =>

        IO createDirectory (bots / name)
        IO copyFile (botJar, bots / name / "ScalatronBot.jar")

        val cmd = "java %s -cp %s:%s scalatron.main.Main -plugins %s" format (
            javaOptions mkString " ",
            Seq(ucp.files(0), botJar).absString,
            Seq(ucp.files(1), botJar).absString,
            bots.absolutePath)
        cmd run
      }
    )
}
