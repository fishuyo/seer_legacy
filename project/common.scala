
import sbt._
import Keys._

object Common {

  // lazy val settings = plugins.JvmPlugin.projectSettings ++ Seq(
  lazy val settings = Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    updateOptions := updateOptions.value.withCachedResolution(true),
    javacOptions ++= Seq(
      "-Xlint",
      "-encoding", "UTF-8"
      // "-source", "1.6",
      // "-target", "1.6"
    ),
    scalacOptions ++= Seq(
      "-Xlint",
      // "-Ywarn-dead-code",
      // "-Ywarn-value-discard",
      // "-Ywarn-numeric-widen",
      // "-Ywarn-unused",
      // "-Ywarn-unused-import",
      // "-unchecked",
      // "-deprecation",
      "-feature",
      "-encoding", "UTF-8"
      // "-target:jvm-1.6"
    ),
    cancelable in Global := true,
    exportJars := true//,
    // libraryDependencies ++= Dependencies.coreD
  )

  // lazy val desktop = commonSettings ++ Seq(
  //   libraryDependencies ++= Seq(
  //     // "com.badlogicgames.gdx" % "gdx-backend-lwjgl" % libgdxVersion.value,
  //     "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxVersion.value,
  //     "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion.value classifier "natives-desktop"
  //   )
  //   // unmanagedResourceDirectories in Compile += file("android/assets"),
  // )

  lazy val appSettings = settings ++ Seq(
    // libraryDependencies ++= Seq(
    //   "net.sf.proguard" % "proguard-base" % "4.11" % "provided"
    // ),
    fork in Compile := true,
    fork in run := true
    // javaOptions in run += "-XstartOnFirstThread"
    // unmanagedResourceDirectories in Compile += file("android/assets"),
    // desktopJarName := "seer-app",
    // Tasks.assembly
  )

  lazy val jsSettings = Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := "2.11.8",
    updateOptions := updateOptions.value.withCachedResolution(true)
  )

}


