
import sbt._
import Keys._

import Process._
import java.io.File
import java.net.URL

object UnmanagedLibs {

  val baseUrl = "http://fishuyo.com/seer"
  
  def downloadZip(name:String) = {
    val zipFile = new File(name)
    println(s"Pulling $name")
    val url = new URL("%s/%s" format(baseUrl, name))
    IO.download(url, zipFile)
    println(s"Downloaded $name")
    zipFile
  }

  def getGlulogic(dest:File){
    if(dest.exists) return
    val zipFile = downloadZip("glulogic.zip")
    unzip(zipFile, dest, matchfile("GlulogicMT.jar"))
    unzip(zipFile, file("lib"), matchfile("libGlulogicMT.dylib"))
    zipFile.delete
  }

  def getOpenNI2(dest:File){
    if(dest.exists) return
    val zipFile = downloadZip("openni2.zip")
    unzip(zipFile, dest, matchfile("org.openni.jar"))
    unzip(zipFile, dest, matchfile("com.primesense.nite.jar"))
    zipFile.delete
  }

  def getJPA(dest:File){
    if(dest.exists) return
    val zipFile = downloadZip("jpa.zip")
    unzip(zipFile, dest, matchfile("jpa-0.1-SNAPSHOT.jar"))
    val arch = sys.props.get("os.arch")
    sys.props.get("os.name") match {
      case Some(s) if s.contains("Mac") => unzip(zipFile, dest, matchfile("jpa-macos-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Win") && arch.get.contains("64") => unzip(zipFile, dest, matchfile("jpa-win64-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Win") => unzip(zipFile, dest, matchfile("jpa-win32-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("nix") && arch.get.contains("64") => unzip(zipFile, dest, matchfile("jpa-linux64-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("nix") => unzip(zipFile, dest, matchfile("jpa-linux32-0.1-SNAPSHOT.jar"))
      case _ => () 
    }
    zipFile.delete
  }

  def getVRPN(dest:File){
    if(dest.exists) return
    val zipFile = downloadZip("vrpn.zip")
    unzip(zipFile, dest, matchfile("vrpn-07.33.jar"))
    unzip(zipFile, file("lib"), matchfile("libjava_vrpn.dylib"))
    zipFile.delete
  }

  // def doDownloadLibs() = {
  //   val zipFile = new File(zipName)

  //   // Extract jars into their respective lib folders.
  //   // val coreDest = file("seer-core/lib")
  //   // val deskDest = file("seer-desktop/lib")
  //   val opencvDest = file("seer-modules/seer-opencv/lib")
  //   val openniDest = file("seer-modules/seer-openni/lib")
  //   val leapDest = file("seer-modules/seer-leap/lib")
  //   val touchDest = file("seer-modules/seer-osx-multitouch/lib")
  //   val vrpnDest = file("seer-modules/seer-vrpn/lib")
  //   val nativeDest = file("lib")
    
  //   val nativeFilter =  new ExactFilter("libGlulogicMT.dylib") | new ExactFilter("libLeap.dylib") | 
  //                       new ExactFilter("libLeapJava.dylib") | new ExactFilter("libopencv_java245.dylib") |
  //                       new ExactFilter("libjava_vrpn.dylib") | new ExactFilter("libOpenNI.dylib") | new ExactFilter("libOpenNI.jni.dylib")

  //   // val coreFilter =  new ExactFilter("monido-core_2.10-0.1.2.jar") //| new ExactFilter("gdx.jar")
  //   //val deskFilter =  new ExactFilter("gdx-natives.jar") | new ExactFilter("gdx-backend-lwjgl.jar") | new ExactFilter("gdx-backend-lwjgl-natives.jar")
  //   val opencvFilter = new ExactFilter("opencv-245.jar")
  //   val openniFilter = new ExactFilter("org.openni.jar")
  //   val leapFilter = new ExactFilter("LeapJava.jar")
  //   val touchFilter = new ExactFilter("GlulogicMT.jar")
  //   val vrpnFilter = new ExactFilter("vrpn.jar")
    
  //   // IO.unzip(zipFile, coreDest, coreFilter)
  //   //IO.unzip(zipFile, deskDest, deskFilter)
  //   IO.unzip(zipFile, opencvDest, opencvFilter)
  //   IO.unzip(zipFile, openniDest, openniFilter)
  //   IO.unzip(zipFile, leapDest, leapFilter)
  //   IO.unzip(zipFile, touchDest, touchFilter)
  //   IO.unzip(zipFile, vrpnDest, vrpnFilter)
  //   IO.unzip(zipFile, nativeDest, nativeFilter)

  //   // Destroy the file.
  //   // zipFile.delete
  //   println("downloadLibs Complete.")
  // }



  import java.io.{FileOutputStream, FileInputStream}
  import java.nio.file.Path
  import java.util.zip.ZipInputStream
  import java.util.zip.ZipEntry

  def matchfile(name:String):PartialFunction[ZipEntry,(ZipEntry,String)] = { case f if f.getName == name || f.getName.endsWith('/'+name) => println(s"hi $name"); (f,name) }
  def matchdir(name:String):PartialFunction[ZipEntry,(ZipEntry,String)] = { case f if f.getName.startsWith(name) => (f,f.getName.replace(name,"")) }

  def unzip(zipFile:File, dest:File, filter:PartialFunction[ZipEntry,(ZipEntry,String)]): Unit = {
    val fis = new FileInputStream(zipFile)
    val zis = new ZipInputStream(fis)

    Stream.continually(zis.getNextEntry).takeWhile(_ != null).collect(filter).foreach {  case (file,path) =>
      if (!file.isDirectory) {
        val outPath = dest.toPath.resolve(path)
        // println(s"${file.getName} $path")
        val outPathParent = outPath.getParent
        if (!outPathParent.toFile.exists()) {
          outPathParent.toFile.mkdirs()
        }

        val outFile = outPath.toFile
        val out = new FileOutputStream(outFile)
        val buffer = new Array[Byte](4096)
        Stream.continually(zis.read(buffer)).takeWhile(_ != -1).foreach(out.write(buffer, 0, _))
        // out.close
      }
    }
    // zis.close
    // fis.close
  }
  
}