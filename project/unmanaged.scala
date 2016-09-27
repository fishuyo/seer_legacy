
import sbt._

import Keys._


object SeerUnmanagedLibs {

  lazy val downloadLibs = taskKey[Unit]("Downloads unmanaged libs and puts them where needed.")
  val downloadTask = downloadLibs <<= streams map { (s: TaskStreams) => doDownloadLibs(s) }

  def doDownloadLibs(s:TaskStreams) = {
  // downloadLibs := {
    import Process._
    import java.io._
    import java.net.URL
    
    // Declare names
    val baseUrl = "http://fishuyo.com/stuff"
    val zipName = "seerLibs.zip"
    val zipFile = new java.io.File(zipName)

    // Fetch the file.
    println(s"Pulling $zipName")
    val url = new URL("%s/%s" format(baseUrl, zipName))
    IO.download(url, zipFile)

    // Extract jars into their respective lib folders.
    // val coreDest = file("seer-core/lib")
    // val deskDest = file("seer-desktop/lib")
    val opencvDest = file("seer-modules/seer-opencv/lib")
    val openniDest = file("seer-modules/seer-openni/lib")
    val leapDest = file("seer-modules/seer-leap/lib")
    val touchDest = file("seer-modules/seer-osx-multitouch/lib")
    val vrpnDest = file("seer-modules/seer-vrpn/lib")
    val nativeDest = file("lib")
    
    val nativeFilter =  new ExactFilter("libGlulogicMT.dylib") | new ExactFilter("libLeap.dylib") | 
                        new ExactFilter("libLeapJava.dylib") | new ExactFilter("libopencv_java245.dylib") |
                        new ExactFilter("libjava_vrpn.dylib") | new ExactFilter("libOpenNI.dylib") | new ExactFilter("libOpenNI.jni.dylib")

    // val coreFilter =  new ExactFilter("monido-core_2.10-0.1.2.jar") //| new ExactFilter("gdx.jar")
    //val deskFilter =  new ExactFilter("gdx-natives.jar") | new ExactFilter("gdx-backend-lwjgl.jar") | new ExactFilter("gdx-backend-lwjgl-natives.jar")
    val opencvFilter = new ExactFilter("opencv-245.jar")
    val openniFilter = new ExactFilter("org.openni.jar")
    val leapFilter = new ExactFilter("LeapJava.jar")
    val touchFilter = new ExactFilter("GlulogicMT.jar")
    val vrpnFilter = new ExactFilter("vrpn.jar")
    
    // IO.unzip(zipFile, coreDest, coreFilter)
    //IO.unzip(zipFile, deskDest, deskFilter)
    IO.unzip(zipFile, opencvDest, opencvFilter)
    IO.unzip(zipFile, openniDest, openniFilter)
    IO.unzip(zipFile, leapDest, leapFilter)
    IO.unzip(zipFile, touchDest, touchFilter)
    IO.unzip(zipFile, vrpnDest, vrpnFilter)
    IO.unzip(zipFile, nativeDest, nativeFilter)

    // Destroy the file.
    // zipFile.delete
    println("downloadLibs Complete.")
  }
  
}