
import sbt._
import Keys._

import Process._
import java.io.File
import java.net.URL
import scala.sys.process._

object Utilities {

  val baseUrl = "http://fishuyo.com/seer"
  
  def download(name:String) = {
    val zipFile = new File(name)
    println(s"Pulling $name")
    val url = new URL("%s/%s" format(baseUrl, name)).toString
    // IO.download(url, zipFile)
    Process(s"curl $url -o ${zipFile.getAbsolutePath}").!
    println(s"Downloaded $name")
    zipFile
  }

  def getHid(dest:File){
    if(dest.exists) return
    val file = download("purejavahidapi.jar")
  }


  def getGlulogic(dest:File){
    if(dest.exists) return
    val zipFile = download("glulogic.zip")
    unzip(zipFile, dest, matchfile("GlulogicMT.jar"))
    unzip(zipFile, file("lib"), matchfile("libGlulogicMT.dylib"))
    zipFile.delete
  }

  def getLeap(dest:File){
    if(dest.exists) return
    val zipFile = download("leap.zip")
    unzip(zipFile, dest, matchfile("LeapJava.jar"))
    unzip(zipFile, file("lib"), matchfile("libLeap.dylib"))
    unzip(zipFile, file("lib"), matchfile("libLeapJava.dylib"))
    zipFile.delete
  }

  def getOpenNI2(dest:File){
    if(dest.exists) return
    val zipFile = download("openni2.zip")
    val lib = file("lib")
    unzip(zipFile, dest, matchfile("org.openni.jar"))
    unzip(zipFile, dest, matchfile("com.primesense.nite.jar"))
    unzip(zipFile, lib, matchfile("OpenNI.ini"))
    unzip(zipFile, new File(lib,"NiTE2"), matchdir("openni2/NiTE2/"))
    IO.write(new File(lib,"NiTE.ini"), s"""
[General]
DataDir=${new File(lib,"NiTE2").getAbsolutePath}

[Log]
; 0 - Verbose; 1 - Info; 2 - Warning; 3 - Error. Default - None
Verbosity=3
LogToConsole=0
LogToFile=0
    """)
    val arch = sys.props.get("os.arch")
    sys.props.get("os.name") match {
      case Some(s) if s.contains("Mac") => unzip(zipFile, lib, matchdir("openni2/macos/"))
      case Some(s) if s.contains("Win") && arch.get.contains("64") => unzip(zipFile, lib, matchdir("openni2/win64/"))
      case _ => () 
    }
    zipFile.delete
  }

  def getJPA(dest:File){
    if(dest.exists) return
    val zipFile = download("jpa.zip")
    unzip(zipFile, dest, matchfile("jpa-0.1-SNAPSHOT.jar"))
    val arch = sys.props.get("os.arch")
    sys.props.get("os.name") match {
      case Some(s) if s.contains("Mac") => unzip(zipFile, dest, matchfile("jpa-macos-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Win") && arch.get.contains("64") => unzip(zipFile, dest, matchfile("jpa-win64-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Win") => unzip(zipFile, dest, matchfile("jpa-win32-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Linux") && arch.get.contains("64") => unzip(zipFile, dest, matchfile("jpa-linux64-0.1-SNAPSHOT.jar"))
      case Some(s) if s.contains("Linux") => unzip(zipFile, dest, matchfile("jpa-linux32-0.1-SNAPSHOT.jar"))
      case _ => () 
    }
    zipFile.delete
  }

  def getVRPN(dest:File){
    if(dest.exists) return
    val zipFile = download("vrpn.zip")
    unzip(zipFile, dest, matchfile("vrpn-07.33.jar"))
    unzip(zipFile, file("lib"), matchfile("libjava_vrpn.dylib"))
    zipFile.delete
  }


  import java.io.{FileOutputStream, FileInputStream}
  import java.nio.file.Path
  import java.util.zip.ZipInputStream
  import java.util.zip.ZipEntry

  def matchfile(name:String):PartialFunction[ZipEntry,(ZipEntry,String)] = { case f if f.getName == name || f.getName.endsWith('/'+name) => (f,name) }
  def matchdir(name:String):PartialFunction[ZipEntry,(ZipEntry,String)] = { case f if f.getName.startsWith(name) => (f,f.getName.replace(name,"")) }

  def unzip(zipFile:File, dest:File, filter:PartialFunction[ZipEntry,(ZipEntry,String)]): Unit = {
    val fis = new FileInputStream(zipFile)
    val zis = new ZipInputStream(fis)

    Stream.continually(zis.getNextEntry).takeWhile(_ != null).collect(filter).foreach {  case (file,path) =>
      if (!file.isDirectory) {
        val outPath = dest.toPath.resolve(path)
        println(s"${file.getName} $path")
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