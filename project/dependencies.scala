import sbt._

object Dependencies {
  object versions {
    val akka = "2.6.5" //"2.5.12" //"2.4.17"
    val gdx = "1.9.10" //-SNAPSHOT"
    val lwjgl = "3.1.3"
    val chill = "0.9.5" //"0.9.2" //"0.5.2" //"0.8.0"
  }

  object akka {
    val actor = "com.typesafe.akka" %% "akka-actor" % versions.akka
    val remote = "com.typesafe.akka" %% "akka-remote" % versions.akka
    val stream = "com.typesafe.akka" %% "akka-stream" % versions.akka
  }

  object twitter {
    val chill = "com.twitter" %% "chill" % versions.chill
    val chillBijection = "com.twitter" %% "chill-bijection" % versions.chill
    val chillAkka = "com.twitter" %% "chill-akka" % versions.chill
  }

  object breeze {
    val breeze = "org.nlp" %% "breeze" % "1.0"
    val natives = "org.nlp" %% "breeze-natives" % "1.0"
    val viz = "org.nlp" %% "breeze-viz" % "1.0"
  }

  val spire = "org.typelevel" %% "spire" % "0.17.0-M1" //"org.spire-math" %% "spire" % "0.17.0"

  val pureconfig = "com.github.pureconfig" %% "pureconfig" % "0.7.2"

  // val scodec = "org.scodec" %% "scodec-core" % "1.10.3"

  /** Core Dependencies */
  val core = Seq(
    // "org.scalameta" %% "scalameta" % "4.1.0",
    akka.actor,
    akka.remote,
    // akka.stream,
    // "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.1",
    "io.altoo" %% "akka-kryo-serialization" % "1.1.5",
    spire,
    twitter.chill,
    twitter.chillBijection,
    twitter.chillAkka,
    "de.sciss" %% "audiofile" % "1.5.4",
    "de.sciss" %% "scalaosc" % "1.2.1",
    "net.sourceforge.jtransforms" % "jtransforms" % "2.4.0",
    "com.lihaoyi" %% "scalarx" % "0.4.2",
    "com.beachape.filemanagement" %% "schwatcher" % "0.3.5",
    "javax.vecmath" % "vecmath" % "1.5.2"
  )

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
