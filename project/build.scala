import sbt._

import Keys._

import com.typesafe.sbt.SbtNativePackager._

import seer.unmanaged._

// import org.scalasbt.androidplugin._
// import org.scalasbt.androidplugin.AndroidKeys._

object Settings {

  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
    scalaVersion := "2.10.2",
    exportJars := true,
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
      "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
      "Sonatypes OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
      "maven.org" at "http://repo1.maven.org/maven2",
      "java portaudio" at "http://maven.renejeschke.de/snapshots",
      "java portaudio natives" at "http://maven.renejeschke.de/native-snapshots"
    ),
    autoCompilerPlugins := true,
    scalacOptions += "-Xexperimental"
   )

  lazy val core = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.1.4", //"2.2.1",
      "org.scala-lang" % "scala-actors" % "2.10.2",
      "de.sciss" %% "scalaosc" % "1.1.+",
      "de.sciss" %% "scalaaudiofile" % "1.2.0", //"1.4.+",
      "net.java.dev.jna" % "jna" % "3.5.2",
      "com.scalarx" % "scalarx_2.10" % "0.2.3"
      //"org.scalala" % "scalala_2.9.0" % "1.0.0.RC2-SNAPSHOT",
    ),
    SeerLibs.updateGdxTask,
    SeerLibs.downloadTask
  )

  lazy val xuggle = Settings.common ++ Seq(
    libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.4"
  )
  lazy val jruby = Settings.common ++ Seq(
    libraryDependencies += "org.jruby" % "jruby" % "1.7.3"
  )
  lazy val twitter_eval = Settings.common ++ Seq(
    libraryDependencies += "com.twitter" %% "util-eval" % "6.12.1"
  )
  lazy val scala_repl = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "org.scala-lang" % "scala-compiler" % "2.10.2",
      "org.scala-lang" % "jline" % "2.10.2"
    )
  )

  lazy val portaudio = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "com.github.rjeschke" % "jpa" % "0.1-SNAPSHOT",
      "com.github.rjeschke" % "jpa-macos" % "0.1-SNAPSHOT"
    )
  )

  lazy val openni = Settings.common ++ Seq(
    // javaOptions in run += "-Djava.library.path=.;./lib;/usr/local/lib/ni2;/usr/local/lib;"
    javaOptions in run += "-Djava.library.path=/usr/local/lib/ni2:/Users/fishuyo/lib/nite2"
  )

  lazy val desktop = packageArchetype.java_application ++ Settings.common ++ Seq (
    fork in run := true,
    // javaOptions in run += "-Djava.library.path=.;./lib;/usr/local/lib", 
    javaOptions in run <<= (fullClasspath in Compile) map { (cp) => 
       val cpString = cp.map(_.data).mkString(System.getProperty("path.separator"))
       Seq("-cp",cpString)
    },
    connectInput in run := true,
    cancelable := true,
    outputStrategy := Some(StdoutOutput)
  )


}

object SeerBuild extends Build {

  // core
  lazy val seer_core = Project (
    "seer-core",
    file("seer-core"),
    settings = Settings.core
  )


  // modules
  lazy val seer_desktop = Project (
    "seer-desktop",
    file("seer-desktop"),
    settings = Settings.common
  ) dependsOn ( seer_core, seer_multitouch, seer_repl, seer_jruby, seer_eval )

  lazy val seer_allosphere = Project (
    "seer-allosphere",
    file("seer-allosphere"),
    settings = Settings.common
  ) dependsOn ( seer_core, seer_desktop )

  lazy val seer_sensors = Project (
    "seer-sensors",
    file("seer-sensors"),
    settings = Settings.common
  ) aggregate( seer_kinect, seer_leap, seer_multitouch )

    lazy val seer_kinect = Project (
      "seer-kinect",
      file("seer-sensors/seer-kinect"),
      settings = Settings.common ++ Settings.openni
    ) dependsOn( seer_core, seer_opencv )

    lazy val seer_leap = Project (
      "seer-leap",
      file("seer-sensors/seer-leap"),
      settings = Settings.common
    ) dependsOn seer_core

    lazy val seer_multitouch = Project (
      "seer-multitouch",
      file("seer-sensors/seer-multitouch"),
      settings = Settings.common
    ) dependsOn seer_core

  lazy val seer_opencv = Project (
    "seer-opencv",
    file("seer-opencv"),
    settings = Settings.common
  ) dependsOn( seer_core, seer_video )

  lazy val seer_video = Project (
    "seer-video",
    file("seer-video"),
    settings = Settings.common ++ Settings.xuggle
  ) dependsOn seer_core 

  lazy val seer_portaudio = Project (
    "seer-portaudio",
    file("seer-portaudio"),
    settings = Settings.common ++ Settings.portaudio
  ) dependsOn seer_core

  lazy val seer_jruby = Project (
    "seer-jruby",
    file("seer-dynamic/seer-jruby"),
    settings = Settings.common ++ Settings.jruby
  ) dependsOn seer_core
  lazy val seer_eval = Project (
    "seer-eval",
    file("seer-dynamic/seer-eval"),
    settings = Settings.common ++ Settings.twitter_eval
  ) dependsOn seer_core
  lazy val seer_repl = Project (
    "seer-repl",
    file("seer-dynamic/seer-repl"),
    settings = Settings.common ++ Settings.scala_repl
  ) dependsOn seer_core 



  // examples
  lazy val examples = Project (
    "examples",
    file("examples"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_opencv )

  lazy val experiments_fieldViewer = Project (
    "experiments-fieldViewer",
    file("experiments/fieldViewer"),
    settings = Settings.desktop
  ) dependsOn seer_desktop

  lazy val experiments_particleSystems = Project (
    "experiments-particleSystems",
    file("experiments/particleSystems"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_opencv )

  lazy val experiments_kinect = Project (
    "experiments-kinect",
    file("experiments/kinect"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_kinect )

  lazy val experiments_opencv = Project (
    "experiments-opencv",
    file("experiments/opencv"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_opencv )

  lazy val experiments_video = Project (
    "experiments-video",
    file("experiments/video"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_video )


  // experiments
  lazy val experiments = Project (
    "experiments",
    file("experiments"),
    settings = Settings.desktop ++ Settings.openni
  ) dependsOn( seer_desktop, seer_opencv, seer_allosphere, seer_portaudio, seer_kinect )


  // apps
  lazy val loop = Project (
    "loop",
    file("apps/desktop/loop"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop )

  lazy val trees = Project (
    "trees",
    file("apps/desktop/trees"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_kinect )

  // // android apps
  // lazy val loop_android = Project (
  //   "loop-android",
  //   file("apps/android/loop"),
  //   settings = Settings.android
  // ) dependsOn seer_core
}
