import sbt._

import Keys._

import org.scalasbt.androidplugin._
import org.scalasbt.androidplugin.AndroidKeys._

object Settings {
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
    scalaVersion := "2.10.2",
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
      "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
      "Sonatypes OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
      "maven.org" at "http://repo1.maven.org/maven2"
    ),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC2",
      "org.scala-lang" % "scala-actors" % "2.10.2",
      // "com.nativelibs4java" % "scalacl" % "0.2",
      "de.sciss" %% "scalaosc" % "1.1.+",
      //"com.github.philcali" % "monido-core_2.9.1" % "0.1.2",
      "org.jruby" % "jruby" % "1.7.3",
      "net.java.dev.jna" % "jna" % "3.5.2",
      "de.sciss" %% "scalaaudiofile" % "1.2.0", //"1.4.+",
      //"com.nativelibs4java" % "javacl" % "1.0.0-RC2",
      "xuggle" % "xuggle-xuggler" % "5.4"
      //"org.scalala" % "scalala_2.9.0" % "1.0.0.RC2-SNAPSHOT",
      //"net.sf.bluecove" % "bluecove" % "2.1.0",
      //"net.sf.bluecove" % "bluecove-gpl" % "2.1.0"
    ),
    autoCompilerPlugins := true,
    //addCompilerPlugin("com.nativelibs4java" % "scalacl-compiler-plugin" % "0.2"),
    scalacOptions += "-Xexperimental",
    //sourceDirectories in Compile += new File("common/src"),
    updateLibgdxTask,
    downloadLibsTask
    //fork in Compile := true
   )

  lazy val desktop = Settings.common ++ Seq (
    fork in Compile := true
  )

  lazy val android = Settings.common ++
    AndroidProject.androidSettings ++
    AndroidMarketPublish.settings ++ Seq (
      platformName in Android := "android-10",
      keyalias in Android := "change-me",
      mainAssetsPath in Android := file("android/src/main/assets"), //file("common/src/main/resources")
      proguardOption in Android := "-keep class com.badlogic.gdx.backends.android.** { *; }"//proguard_options,
      //unmanagedBase <<= baseDirectory( _ /"src/main/libs" ),
      //unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "src/main/libs") }
    )

  val downloadLibs = TaskKey[Unit]("download-libs", "Downloads/Updates required libs")
  val updateLibgdx = TaskKey[Unit]("update-gdx", "Updates libgdx")

  def doDownloadLibs(s:TaskStreams) = {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://fishuyo.com/stuff"
    val zipName = "seerLibs.zip"
    val zipFile = new java.io.File(zipName)

    // Fetch the file.
    s.log.info("Pulling %s" format(zipName))
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    val commonDest = file("common/lib")
    val desktopDest = file("desktop/lib")
    val commonFilter = new ExactFilter("freenect-0.0.1.jar") | new ExactFilter("opencv-245.jar") | new ExactFilter("monido-core_2.10-0.1.2.jar")
    val deskFilter = new ExactFilter("GlulogicMT.jar") | new ExactFilter("libGlulogicMT.dylib") | new ExactFilter("flibopencv_java245.dylib")
    IO.unzip(zipFile, commonDest, commonFilter)
    IO.unzip(zipFile, desktopDest, deskFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Complete")
  }
  
  def doUpdateLibgdx(s:TaskStreams) = {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://libgdx.badlogicgames.com/nightlies"
    val gdxName = "libgdx-nightly-latest"

    // Fetch the file.
    s.log.info("Pulling %s" format(gdxName))
    s.log.warn("This may take a few minutes...")
    val zipName = "%s.zip" format(gdxName)
    val zipFile = new java.io.File(zipName)
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    s.log.info("Extracting..")

    // Extract jars into their respective lib folders.
    val commonDest = file("common/lib")
    val commonFilter = new ExactFilter("gdx.jar")
    IO.unzip(zipFile, commonDest, commonFilter)

    val desktopDest = file("desktop/lib")
    val desktopFilter = new ExactFilter("gdx-natives.jar") |
    new ExactFilter("gdx-backend-lwjgl.jar") |
    new ExactFilter("gdx-backend-lwjgl-natives.jar") |
    new ExactFilter("gdx-tools.jar")
    IO.unzip(zipFile, desktopDest, desktopFilter)

    val androidDest = file("android/src/main/libs")
    val androidFilter = new ExactFilter("gdx-backend-android.jar") |
    new ExactFilter("armeabi/libgdx.so") |
    new ExactFilter("armeabi/libandroidgl20.so") |
    new ExactFilter("armeabi-v7a/libgdx.so") |
    new ExactFilter("armeabi-v7a/libandroidgl20.so") |
    commonFilter
    IO.unzip(zipFile, androidDest, androidFilter)
    //check this copy?
    //IO.copyFile( (androidDest+"gdx-backend-android.jar").asFile , "android/lib/".asFile )

    // Destroy the file.
    zipFile.delete
    s.log.info("Complete")
  }

  val downloadLibsTask = downloadLibs <<= streams map { (s: TaskStreams) =>
    doDownloadLibs(s)
    doUpdateLibgdx(s)
  }

  val updateLibgdxTask = updateLibgdx <<= streams map { (s: TaskStreams) =>
    doUpdateLibgdx(s)
  }
}

object LibgdxBuild extends Build {
  val common = Project (
    "common",
    file("common"),
    settings = Settings.common
  )

  lazy val a_desktop = Project (
    "desktop",
    file("desktop"),
    settings = Settings.desktop
  ) dependsOn common

  lazy val android = Project (
    "android",
    file("android"),
    settings = Settings.android
  ) dependsOn common
}
