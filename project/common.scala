
import sbt._
import Keys._

object Common {

  val scalaV = "2.12.5" //2.11.8"

  // lazy val settings = plugins.JvmPlugin.projectSettings ++ Seq(
  lazy val settings = Seq(
    organization := "com.fishuyo.seer",
    version := "0.1-SNAPSHOT",
    scalaVersion := scalaV,
    updateOptions := updateOptions.value.withCachedResolution(true),
    // updateOptions := updateOptions.value.withLatestSnapshots(true),
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
    resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
    cancelable in Global := true,
    exportJars := true,
    pomIncludeRepository := { _ => false },
    licenses := Seq("BSD-style" -> url("http://www.opensource.org/licenses/bsd-license.php")),
    homepage := Some(url("http://fishuyo.com/projects/seer")),
    scmInfo := Some(
      ScmInfo(
        url("https://github.com/fishuyo/seer"),
        "scm:git@github.com:fishuyo/seer.git"
      )
    ),
    developers := List(
      Developer(
        id    = "fishuyo",
        name  = "Tim Wood",
        email = "fishuyo@gmail.com",
        url   = url("http://fishuyo.com")
      )
    ),
    publishMavenStyle := true,
    publishTo := {
      val nexus = "https://oss.sonatype.org/"
      if (isSnapshot.value)
        Some("snapshots" at nexus + "content/repositories/snapshots")
      else
        Some("releases"  at nexus + "service/local/staging/deploy/maven2")
    }
  )

  lazy val appSettings = settings ++ Seq(
    // libraryDependencies ++= Seq(
    //   "net.sf.proguard" % "proguard-base" % "4.11" % "provided"
    // ),
    fork in Compile := true,
    fork in run := true //,
    // javaOptions in run += "-Xcheck:jni"
    // javaOptions in run += "-Dorg.lwjgl.util.Debug=true"
    // unmanagedResourceDirectories in Compile += file("android/assets"),
    // desktopJarName := "seer-app",
    // Tasks.assembly
  ) ++ {
    if(System.getProperty("os.name").contains("Mac")) Seq(
      javaOptions in run += "-XstartOnFirstThread",
      envVars := Map("DYLD_LIBRARY_PATH" -> file("lib").getAbsolutePath)
    ) else Seq()
  }

  lazy val jsSettings = Seq(
    version := "0.1-SNAPSHOT",
    scalaVersion := scalaV,
    updateOptions := updateOptions.value.withCachedResolution(true)
  )

}


