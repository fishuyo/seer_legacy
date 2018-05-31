
import sbt._

object Dependencies {
  // versions
  val akkaVersion = "2.5.12" //"2.4.17"
  val libgdxVersion = "1.9.9-SNAPSHOT"
  val lwjglVersion = "3.1.3"
  val chillVersion = "0.9.2" //"0.5.2" //"0.8.0"

  // libs
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaVersion
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaVersion
  
  val spire = "org.spire-math" %% "spire" % "0.13.0"
  // val parsers = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.4"

  val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.7.2"

  val scodec = "org.scodec" %% "scodec-core" % "1.10.3"

  /** Core Dependencies */
  val corejvmD = Seq(
    //"com.lihaoyi" % "ammonite-repl" % "0.4.8" cross CrossVersion.full,
    //"com.lihaoyi" % "ammonite-sshd" % "0.4.8" cross CrossVersion.full,
    akkaActor, akkaRemote, akkaStream,
    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1",
    spire,
    "com.twitter" %% "chill" % chillVersion,
    "com.twitter" %% "chill-bijection" % chillVersion,
    "com.twitter" %% "chill-akka" % chillVersion,
    "de.sciss" %% "scalaaudiofile" % "1.4.7",
    "de.sciss" %% "scalaosc" % "1.1.6",
    "net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
    "com.lihaoyi" %% "scalarx" % "0.3.2",
    "com.beachape.filemanagement" %% "schwatcher" % "0.3.2",
    "javax.vecmath" % "vecmath" % "1.5.2"
  )
  val corejsD = Seq()

  /** libGDX backend dependencies */
  val gdxD = Seq(
    "com.badlogicgames.gdx" % "gdx" % libgdxVersion
    // parsers
  )
  val gdxAppDesktopD = Seq(
    "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxVersion,
    "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion classifier "natives-desktop"
  )


 /** Addon Dependencies */
  val openvrD = Seq(
    "org.lwjgl" % "lwjgl-openvr" % lwjglVersion,
    "org.lwjgl" % "lwjgl-openvr" % lwjglVersion classifier "natives-windows",
    "net.java.dev.jna" % "jna" % "3.5.0",
    "org.joml" % "joml" % "1.8.1"
  )

  val hidD = Seq(
    "org.hid4java" % "hid4java" % "0.5.0"
  )
}