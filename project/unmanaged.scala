package seer.unmanaged

import sbt._

import Keys._


object SeerLibs {

  val downloadLibs = TaskKey[Unit]("download-libs", "Downloads/Updates required libs")
  val updateLibgdx = TaskKey[Unit]("update-gdx", "Updates libgdx")

  val downloadTask = downloadLibs <<= streams map { (s: TaskStreams) =>
    doDownloadLibs(s)
  }

  val updateGdxTask = updateLibgdx <<= streams map { (s: TaskStreams) =>
    doUpdateLibgdx(s)
  }

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
    val coreDest = file("seer-core/lib")
    val deskDest = file("seer-desktop/lib")
    val opencvDest = file("seer-modules/seer-opencv/lib")
    val kinectDest = file("seer-modules/seer-kinect/lib")
    val leapDest = file("seer-modules/seer-leap/lib")
    val touchDest = file("seer-modules/seer-multitouch/lib")
    val nativeDest = file("lib")
    val nativeFilter =  new ExactFilter("libGlulogicMT.dylib") | new ExactFilter("libLeap.dylib") | 
                        new ExactFilter("libLeapJava.dylib") | new ExactFilter("libopencv_java245.dylib")
                      
                
    val coreFilter =  new ExactFilter("monido-core_2.10-0.1.2.jar") | new ExactFilter("gdx.jar")
    val deskFilter =  new ExactFilter("gdx-natives.jar") | new ExactFilter("gdx-backend-lwjgl.jar") | new ExactFilter("gdx-backend-lwjgl-natives.jar")
    val opencvFilter = new ExactFilter("opencv-245.jar")
    val kinectFilter = new ExactFilter("freenect-0.0.1.jar")
    val leapFilter = new ExactFilter("LeapJava.jar")
    val touchFilter = new ExactFilter("GlulogicMT.jar")
    
    IO.unzip(zipFile, coreDest, coreFilter)
    IO.unzip(zipFile, deskDest, deskFilter)
    IO.unzip(zipFile, opencvDest, opencvFilter)
    IO.unzip(zipFile, kinectDest, kinectFilter)
    IO.unzip(zipFile, leapDest, leapFilter)
    IO.unzip(zipFile, touchDest, touchFilter)
    IO.unzip(zipFile, nativeDest, nativeFilter)

    // Destroy the file.
    zipFile.delete
    s.log.info("Complete")
  }
  
  def doUpdateLibgdx(s:TaskStreams) = {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://libgdx.badlogicgames.com/releases"
    val gdxName = "libgdx-1.0.0"
    // val baseUrl = "http://libgdx.badlogicgames.com/nightlies"
    // val gdxName = "libgdx-nightly-latest"

    // Fetch the file.
    s.log.info("Pulling %s" format(gdxName))
    s.log.warn("This may take a few minutes...")
    val zipName = "%s.zip" format(gdxName)
    val zipFile = new java.io.File(zipName)
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    s.log.info("Extracting..")

    // Extract jars into their respective lib folders.
    val commonDest = file("seer-core/lib")
    val commonFilter = new ExactFilter("gdx.jar")
    IO.unzip(zipFile, commonDest, commonFilter)

    val desktopDest = file("seer-desktop/lib")
    val desktopFilter = new ExactFilter("gdx-natives.jar") |
    new ExactFilter("gdx-backend-lwjgl.jar") |
    new ExactFilter("gdx-backend-lwjgl-natives.jar") |
    new ExactFilter("gdx-tools.jar")
    IO.unzip(zipFile, desktopDest, desktopFilter)

    val androidDest = file("apps/android/loop/src/main/libs")
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