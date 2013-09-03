import sbt._

import Keys._

import seer.unmanaged._

// import org.scalasbt.androidplugin._
// import org.scalasbt.androidplugin.AndroidKeys._

object Settings {

  lazy val common = Defaults.defaultSettings ++ Seq (
    version := "0.1",
    scalaVersion := "2.10.2",
    resolvers ++= Seq(
      "typesafe" at "http://repo.typesafe.com/typesafe/releases/",
      "NativeLibs4Java Repository" at "http://nativelibs4java.sourceforge.net/maven/",
      "xuggle repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/",
      "Sonatypes OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "ScalaNLP Maven2" at "http://repo.scalanlp.org/repo",
      "maven.org" at "http://repo1.maven.org/maven2"
    ),
    autoCompilerPlugins := true,
    scalacOptions += "-Xexperimental"
   )

  lazy val core = Settings.common ++ Seq(
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.2.0-RC2",
      "org.scala-lang" % "scala-actors" % "2.10.2",
      "de.sciss" %% "scalaosc" % "1.1.+",
      "de.sciss" %% "scalaaudiofile" % "1.2.0", //"1.4.+",
      "org.jruby" % "jruby" % "1.7.3",
      "net.java.dev.jna" % "jna" % "3.5.2",
      "xuggle" % "xuggle-xuggler" % "5.4"
      //"org.scalala" % "scalala_2.9.0" % "1.0.0.RC2-SNAPSHOT",
    ),
    SeerLibs.updateGdxTask,
    SeerLibs.downloadTask
  )

  lazy val desktop = Settings.common ++ Seq (
    fork in Compile := true
  )

  // lazy val android = Settings.common ++
  //   AndroidProject.androidSettings ++
  //   AndroidMarketPublish.settings ++ Seq (
  //     platformName in Android := "android-10",
  //     keyalias in Android := "change-me",
  //     mainAssetsPath in Android := file("android/src/main/assets"), //file("common/src/main/resources")
  //     proguardOption in Android := "-keep class com.badlogic.gdx.backends.android.** { *; }"//proguard_options,
  //     //unmanagedBase <<= baseDirectory( _ /"src/main/libs" ),
  //     //unmanagedClasspath in Runtime <+= (baseDirectory) map { bd => Attributed.blank(bd / "src/main/libs") }
  //   )


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
    settings = Settings.desktop
  ) dependsOn ( seer_core, seer_multitouch )

  lazy val seer_allosphere = Project (
    "seer-allosphere",
    file("seer-allosphere"),
    settings = Settings.desktop
  ) dependsOn seer_core

  lazy val seer_sensors = Project (
    "seer-sensors",
    file("seer-sensors"),
    settings = Settings.desktop
  ) aggregate( seer_kinect, seer_leap, seer_multitouch )

    lazy val seer_kinect = Project (
      "seer-kinect",
      file("seer-sensors/seer-kinect"),
      settings = Settings.desktop
    ) dependsOn( seer_core, seer_opencv )

    lazy val seer_leap = Project (
      "seer-leap",
      file("seer-sensors/seer-leap"),
      settings = Settings.desktop
    ) dependsOn seer_core

    lazy val seer_multitouch = Project (
      "seer-multitouch",
      file("seer-sensors/seer-multitouch"),
      settings = Settings.desktop
    ) dependsOn seer_core

  lazy val seer_opencv = Project (
    "seer-opencv",
    file("seer-opencv"),
    settings = Settings.desktop
  ) dependsOn seer_core 


  // examples
  lazy val examples = Project (
    "examples",
    file("examples"),
    settings = Settings.desktop
  ) dependsOn seer_desktop

  lazy val examples_fieldViewer = Project (
    "examples-fieldViewer",
    file("examples/fieldViewer"),
    settings = Settings.desktop
  ) dependsOn seer_desktop

  lazy val examples_particleSystems = Project (
    "examples-particleSystems",
    file("examples/particleSystems"),
    settings = Settings.desktop
  ) dependsOn seer_desktop

  lazy val examples_kinect = Project (
    "examples-kinect",
    file("examples/kinect"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_kinect )

  lazy val examples_opencv = Project (
    "examples-opencv",
    file("examples/opencv"),
    settings = Settings.desktop
  ) dependsOn( seer_desktop, seer_opencv )




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
  ) dependsOn seer_desktop

  // lazy val loop-android = Project (
  //   "loop-android",
  //   file("apps/android/loop"),
  //   settings = Settings.android
  // ) dependsOn common
}
