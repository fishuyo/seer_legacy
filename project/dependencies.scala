import sbt._
import org.portablescala.sbtplatformdeps.PlatformDepsPlugin.autoImport._

object Dependencies {
  object versions {
    val akka = "2.6.5" //"2.5.12" //"2.4.17"
    val gdx = "1.9.10" //-SNAPSHOT"
    val lwjgl = "3.1.3"
    val chill = "0.9.5" 
  }

  object breeze {
    val breeze = "org.nlp" %% "breeze" % "1.0"
    val natives = "org.nlp" %% "breeze-natives" % "1.0"
    val viz = "org.nlp" %% "breeze-viz" % "1.0"
  }

  /** Core Dependencies */
  val spatial = Def.setting(Seq(
    "org.typelevel" %%% "spire" % "0.17.0-RC1",
    "javax.vecmath" % "vecmath" % "1.5.2"
  ))

  val audio = Def.setting(Seq(
    "net.sourceforge.jtransforms" % "jtransforms" % "2.4.0",
    "de.sciss" %%% "audiofile" % "2.3.3"
  ))

  val runtime = Def.setting(Seq(
    "com.typesafe.akka" %% "akka-actor" % versions.akka,
    "com.typesafe.akka" %% "akka-remote" % versions.akka,
    // "com.typesafe.akka" %% "akka-stream" % versions.akka,

    "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
    "com.twitter" %% "chill" % versions.chill,
    "com.twitter" %% "chill-bijection" % versions.chill,
    "com.twitter" %% "chill-akka" % versions.chill,

    "com.github.pathikrit" %% "better-files" % "3.9.1",
    "com.github.pathikrit" %% "better-files-akka" % "3.9.1",
    "io.methvin" % "directory-watcher" % "0.10.1",
    "io.methvin" %% "directory-watcher-better-files" % "0.10.1",

    "de.sciss" %% "scalaosc" % "1.2.1",
    "com.lihaoyi" %% "os-lib" % "0.7.2"

  ))


  val core = Def.setting(Seq(
    "com.chuusai" %%% "shapeless" % "2.3.3",
    // "org.typelevel" %%% "spire" % "0.17.0-RC1",
    // "net.sourceforge.jtransforms" % "jtransforms" % "2.4.0",
    "com.lihaoyi" %%% "scalarx" % "0.4.3",
    // "javax.vecmath" % "vecmath" % "1.5.2"
  ))

  val coreJvm = Def.setting(core.value ++ Seq(
    "com.typesafe.akka" %% "akka-actor" % versions.akka,
    "com.typesafe.akka" %% "akka-remote" % versions.akka,
    // "com.typesafe.akka" %% "akka-stream" % versions.akka,
    "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
    "com.twitter" %% "chill" % versions.chill,
    "com.twitter" %% "chill-bijection" % versions.chill,
    "com.twitter" %% "chill-akka" % versions.chill,
    "de.sciss" %% "scalaosc" % "1.2.1",
    // "com.beachape.filemanagement" %% "schwatcher" % "0.3.5",
    "com.github.pathikrit" %% "better-files" % "3.9.1",
    "com.github.pathikrit" %% "better-files-akka" % "3.9.1",
    "io.methvin" % "directory-watcher" % "0.10.1",
    "io.methvin" %% "directory-watcher-better-files" % "0.10.1"
  ))

  val coreJs = Def.setting(core.value ++ Seq(
    "org.akka-js" %%% "akkajsactor" % "2.2.6.9",
    // "org.akka-js" %%% "akkajsactorstream" % "2.2.6.9",
    "org.scala-js" %%% "scalajs-dom" % "1.1.0"
  ))

  val rx = Def.setting(Seq(
    "com.lihaoyi" %%% "scalarx" % "0.4.3"
  ))


  /** libGDX backend dependencies */
  val gdx = Seq(
    "com.badlogicgames.gdx" % "gdx" % versions.gdx,
    "com.badlogicgames.gdx" % "gdx-freetype" % versions.gdx
    // parsers
  )
  val gdxAppDesktop = Seq(
    "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % versions.gdx,
    "com.badlogicgames.gdx" % "gdx-platform" % versions.gdx classifier "natives-desktop",
    "com.badlogicgames.gdx" % "gdx-freetype-platform" % versions.gdx classifier "natives-desktop"
  )

  /** Addon Dependencies */
  val openvr = Seq(
    "org.lwjgl" % "lwjgl-openvr" % versions.lwjgl,
    "org.lwjgl" % "lwjgl-openvr" % versions.lwjgl classifier "natives-windows",
    "net.java.dev.jna" % "jna" % "3.5.0",
    "org.joml" % "joml" % "1.8.1"
  )

  val hid = Seq(
    "org.hid4java" % "hid4java" % "0.5.0"
  )

  val javacv = Seq(
    "org.bytedeco" % "javacv" % "1.4.4",
    "org.bytedeco" % "javacv-platform" % "1.4.4"
  )
}
