
import sbt._
// import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {
  // versions
  val akkaV = "2.4.17" //"2.3.4" 
  val libgdxV = "1.9.2"

  val chillV = "0.5.2" //"0.8.0" //"0.5.2"

  // libs
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaV
  val akkaStream = "com.typesafe.akka" %% "akka-stream" % akkaV
  val spire = "org.spire-math" %% "spire" % "0.13.0"
  val parsers = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"

  val ficus = "com.iheart" %% "ficus" % "1.4.0"
  val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.7.2"

  // projects
  val coreD = Seq(
    //"com.lihaoyi" % "ammonite-repl" % "0.4.8" cross CrossVersion.full,
    //"com.lihaoyi" % "ammonite-sshd" % "0.4.8" cross CrossVersion.full,
    akkaActor, akkaRemote, akkaStream,
    spire,
    "com.twitter" %% "chill" % chillV,
    "com.twitter" %% "chill-bijection" % chillV,
    "com.twitter" %% "chill-akka" % chillV,
    "de.sciss" %% "scalaaudiofile" % "1.4.3+", //"1.4.+",
    "de.sciss" %% "scalaosc" % "1.1.+",
    "net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
    "com.lihaoyi" %% "scalarx" % "0.3.1",
    "com.beachape.filemanagement" %% "schwatcher" % "0.3.2" //"0.1.5"
  )

  val gdxD = Seq(
    //akkaActor, akkaRemote, akkaStream,
    "com.badlogicgames.gdx" % "gdx" % libgdxV,
    parsers
  )

  val gdxAppDesktopD = Seq(
    //akkaActor, akkaRemote, akkaStream,
    "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxV,
    "com.badlogicgames.gdx" % "gdx-platform" % libgdxV classifier "natives-desktop"
  )

  val opencvD = Seq(
    "org.openpnp" % "opencv" % "3.2.0-1",
    // "org.openpnp" % "opencv" % "2.4.13-0",
    parsers
  )

  val openvrD = Seq(
    "net.java.dev.jna" % "jna" % "3.5.0",
    "org.joml" % "joml" % "1.8.1"
  )

  val hidD = Seq(
    "org.hid4java" % "hid4java" % "0.5.0"
  )
}