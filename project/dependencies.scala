
import sbt._
// import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._

object Dependencies {
  // versions
  val akkaV = "2.3.4" 
  val libgdxV = "1.9.2"

  // libs
  val akkaActor = "com.typesafe.akka" %% "akka-actor" % akkaV
  val akkaRemote = "com.typesafe.akka" %% "akka-remote" % akkaV
  val spire = "org.spire-math" %% "spire" % "0.13.0"
  val parsers = "org.scala-lang.modules" %% "scala-parser-combinators" % "1.0.2"


  // projects
  val coreD = Seq(
    //"com.lihaoyi" % "ammonite-repl" % "0.4.8" cross CrossVersion.full,
    //"com.lihaoyi" % "ammonite-sshd" % "0.4.8" cross CrossVersion.full,
    akkaActor, akkaRemote,
    spire,
    "com.twitter" %% "chill" % "0.5.2",
    "com.twitter" %% "chill-bijection" % "0.5.2",
    "com.twitter" %% "chill-akka" % "0.5.2",
    "de.sciss" %% "scalaaudiofile" % "1.4.3+", //"1.4.+",
    "de.sciss" %% "scalaosc" % "1.1.+",
    "net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
    "com.lihaoyi" %% "scalarx" % "0.3.1",
    "com.beachape.filemanagement" %% "schwatcher" % "0.1.5"
  )

  val gdxD = Seq(
    "com.badlogicgames.gdx" % "gdx" % libgdxV,
    parsers
  )

  val gdxAppDesktopD = Seq(
    "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxV,
    "com.badlogicgames.gdx" % "gdx-platform" % libgdxV classifier "natives-desktop"
  )

  val opencvD = Seq(
    "org.openpnp" % "opencv" % "3.2.0-1",
    // "org.openpnp" % "opencv" % "2.4.13-0",
    parsers
  )
}