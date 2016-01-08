/* 
 * Seer build.scala
 * Based on project template https://github.com/ajhager/libgdx-sbt-project.g8
 */ 

import sbt._
import Keys._

import android.Keys._
import android.Plugin.androidBuild
import sbtrobovm.RobovmPlugin._

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
    exportJars := true,
    SeerUnmanagedLibs.downloadTask

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
    // unmanagedResourceDirectories in Compile += file("android/assets"),
    desktopJarName := "seer-app",
    Tasks.assembly
  )

  lazy val android = core ++ Tasks.natives ++ androidBuild ++ Seq(
    libraryDependencies ++= Seq(
      "com.badlogicgames.gdx" % "gdx-backend-android" % libgdxVersion.value,
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value % "natives" classifier "natives-armeabi",
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value % "natives" classifier "natives-armeabi-v7a",
      "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value % "natives" classifier "natives-x86"
    ),
    nativeExtractions <<= (baseDirectory) { base => Seq(
      ("natives-armeabi.jar", new ExactFilter("libgdx.so"), base / "libs" / "armeabi"),
      ("natives-armeabi-v7a.jar", new ExactFilter("libgdx.so"), base / "libs" / "armeabi-v7a"),
      ("natives-x86.jar", new ExactFilter("libgdx.so"), base / "libs" / "x86")
    )},
    platformTarget in Android := "android-21",
    proguardOptions in Android ++= scala.io.Source.fromFile(file("core/proguard-project.txt")).getLines.toList ++
                                   scala.io.Source.fromFile(file("android/proguard-project.txt")).getLines.toList
  )

  // lazy val ios = core ++ Tasks.natives ++ Seq(
  //   unmanagedResources in Compile <++= (baseDirectory) map { _ =>
  //     (file("android/assets") ** "*").get
  //   },
  //   forceLinkClasses := Seq("com.badlogic.gdx.scenes.scene2d.ui.*"),
  //   skipPngCrush := true,
  //   iosInfoPlist <<= (sourceDirectory in Compile){ sd => Some(sd / "Info.plist") },
  //   frameworks := Seq("UIKit", "OpenGLES", "QuartzCore", "CoreGraphics", "OpenAL", "AudioToolbox", "AVFoundation"),
  //   nativePath <<= (baseDirectory){ bd => Seq(bd / "lib") },
  //   libraryDependencies ++= Seq(
  //     "com.badlogicgames.gdx" % "gdx-backend-robovm" % libgdxVersion.value,
  //     "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value % "natives" classifier "natives-ios"
  //   ),
  //   nativeExtractions <<= (baseDirectory) { base => Seq(
  //     ("natives-ios.jar", new ExactFilter("libgdx.a") | new ExactFilter("libObjectAL.a"), base / "lib")
  //   )}
  // )
}


// object LibgdxBuild extends Build {
//   lazy val libgdxVersion = settingKey[String]("version of Libgdx library")

//   lazy val core = Project(
//     id       = "core",
//     base     = file("core"),
//     settings = SeerSettings.core
//   )

//   lazy val desktop = Project(
//     id       = "desktop",
//     base     = file("desktop"),
//     settings = SeerSettings.desktop
//   ).dependsOn(core)

//   lazy val android = Project(
//     id       = "android",
//     base     = file("android"),
//     settings = SeerSettings.android
//   ).dependsOn(core)

//   lazy val ios = RobovmProject(
//     id       = "ios",
//     base     = file("ios"),
//     settings = SeerSettings.ios
//   ).dependsOn(core)

//   lazy val all = Project(
//     id       = "all-platforms",
//     base     = file("."),
//     settings = SeerSettings.core
//   ).aggregate(core, desktop, android, ios)
// }

object SeerBuild extends Build {

  import SeerSettings._
  import SeerModulesBuild._

  lazy val libgdxVersion = settingKey[String]("version of Libgdx library")

  // core
  lazy val seer_core = project.in(file("seer-core")).settings(core: _*)

  // libgdx specific code
  lazy val seer_gdx = project.in(file("seer-gdx")).
    settings(core: _*).dependsOn(seer_core)

  // libgdx desktop specific code
  lazy val seer_gdx_desktop_app = project.in(file("seer-gdx/seer-gdx-desktop-app")).
    settings(desktop: _*).dependsOn(seer_gdx)

  // examples
  lazy val examples = project.settings(app: _*).
    dependsOn(seer_gdx_desktop_app, seer_osx_multitouch, seer_script)

  lazy val examples_graphics = project.in(file("examples/graphics")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_actor = project.in(file("examples/actor")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app)

  lazy val examples_audio = project.in(file("examples/audio")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_portaudio)

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

  lazy val examples_bullet = project.in(file("examples/bullet")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_bullet)

  lazy val seer = Project(
    id       = "seer",
    base     = file("."),
    settings = core
  ).aggregate(seer_core, seer_gdx, seer_gdx_desktop_app, examples)

}


