/* 
 * Seer build.scala
 * Based on project template https://github.com/ajhager/libgdx-sbt-project.g8
 */ 

import sbt._
import Keys._

object SeerSettings {
  import SeerBuild.libgdxVersion

  lazy val nativeExtractions = SettingKey[Seq[(String, NameFilter, File)]](
    "native-extractions", "(jar name partial, sbt.NameFilter of files to extract, destination directory)"
  )

  lazy val desktopJarName = SettingKey[String]("desktop-jar-name", "name of JAR file for desktop")

  lazy val core = plugins.JvmPlugin.projectSettings ++ Seq(
    version := (version in LocalProject("seer")).value,
    libgdxVersion := (libgdxVersion in LocalProject("seer")).value,
    scalaVersion := (scalaVersion in LocalProject("seer")).value,
    libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx" % libgdxVersion.value
    ),
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions ++= Seq(
      "-Xlint",
      "-encoding", "UTF-8",
      "-source", "1.6",
      "-target", "1.6"
    ),
    scalacOptions ++= Seq(
      "-Xlint",
      "-Ywarn-dead-code",
      // "-Ywarn-value-discard",
      // "-Ywarn-numeric-widen",
      // "-Ywarn-unused",
      // "-Ywarn-unused-import",
      "-unchecked",
      "-deprecation",
      "-feature",
      "-encoding", "UTF-8",
      "-target:jvm-1.6"
    ),
    cancelable in Global := true,
    exportJars := true
  )

  lazy val desktop = core ++ Seq(
    libraryDependencies ++= Seq(
      // "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % libgdxVersion.value,
      "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxVersion.value,
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value classifier "natives-desktop"
    )
    // unmanagedResourceDirectories in Compile += file("android/assets"),
  )

  lazy val app = core ++ Seq(
    libraryDependencies ++= Seq(
      "net.sf.proguard" % "proguard-base" % "4.11" % "provided"
    ),
    fork in Compile := true,
    fork in run := true,
    javaOptions in run += "-XstartOnFirstThread",
    // unmanagedResourceDirectories in Compile += file("android/assets"),
    desktopJarName := "seer-app",
    Tasks.assembly
  )

}

object SeerBuild extends Build {

  import SeerSettings._
  import SeerModulesBuild._

  lazy val libgdxVersion = settingKey[String]("version of Libgdx library")

  // aggregate all projects
  lazy val seer = project.in(file(".")).settings(SeerUnmanagedLibs.downloadTask).
    aggregate(seer_core, seer_gdx, seer_gdx_desktop_app)

  // core
  lazy val seer_core = project.in(file("seer-core")).settings(core: _*)

  // libgdx specific code
  lazy val seer_gdx = project.in(file("seer-gdx")).
    settings(core: _*).dependsOn(seer_core)

  // libgdx desktop specific code
  lazy val seer_gdx_desktop_app = project.in(file("seer-gdx/seer-gdx-desktop-app")).
    settings(desktop: _*).dependsOn(seer_gdx)

  // examples
  lazy val examples = project.in(file("examples")).settings(app: _*).
    aggregate(examples_graphics, examples_audio, examples_live, examples_particle,
      examples_trackpad, examples_video, examples_opencv, examples_openni)

  lazy val examples_graphics = project.in(file("examples/graphics")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_audio = project.in(file("examples/audio")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_portaudio)

  lazy val examples_live = project.in(file("examples/live")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_script, seer_portaudio)

  lazy val examples_particle = project.in(file("examples/particle")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app)

  lazy val examples_trackpad = project.in(file("examples/trackpad")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_video = project.in(file("examples/video")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_video)

  lazy val examples_opencv = project.in(file("examples/opencv")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_opencv)

  lazy val examples_openni = project.in(file("examples/openni")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_openni)

  lazy val examples_iclc = project.in(file("examples/iclc")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_script, seer_portaudio, seer_openni)

}


