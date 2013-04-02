import sbt._

import Keys._
import AndroidKeys._

object Settings {
  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
    scalaVersion := "2.9.2",
    resolvers ++= Seq(
      "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
      "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
      "Sonatypes OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo"
    ),
    libraryDependencies ++= Seq(
      //"org.scala-lang" % "scala-compiler" % "2.9.1",
      "com.nativelibs4java" % "scalacl" % "0.2",
      "de.sciss" %% "scalaosc" % "1.1.+"
      //"com.nativelibs4java" % "javacl" % "1.0.0-RC2",
      //"xuggle" % "xuggle-xuggler" % "5.4"
      //"org.scalala" % "scalala_2.9.0" % "1.0.0.RC2-SNAPSHOT",
      //"net.sf.bluecove" % "bluecove" % "2.1.0",
      //"net.sf.bluecove" % "bluecove-gpl" % "2.1.0"
    ),
    autoCompilerPlugins := true,
    addCompilerPlugin("com.nativelibs4java" % "scalacl-compiler-plugin" % "0.2"),
    scalacOptions += "-Xexperimental",
    //sourceDirectories in Compile += new File("common/src"),
    updateLibgdxTask
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
      mainAssetsPath in Android := file("android/src/main/assets") //file("common/src/main/resources")
      //proguardOption in Android := proguard_options,
      //unmanagedBase <<= baseDirectory( _ /"src/main/libs" ),
      //unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "src/main/libs") }
    )

  val updateLibgdx = TaskKey[Unit]("update-gdx", "Updates libgdx")

  val updateLibgdxTask = updateLibgdx <<= streams map { (s: TaskStreams) =>
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
}

object LibgdxBuild extends Build {
  val all_common = Project (
    "common",
    file("common"),
    settings = Settings.common
  )

  lazy val desktop = Project (
    "desktop",
    file("desktop"),
    settings = Settings.desktop
  ) dependsOn all_common

  lazy val android = Project (
    "android",
    file("android"),
    settings = Settings.android
  ) dependsOn all_common
}
