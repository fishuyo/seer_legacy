import sbt._

import Keys._

import com.typesafe.sbt.SbtNativePackager._

import seer.unmanaged._


object BuildSettings {
  val defaults = Defaults.defaultSettings ++ Seq(
    organization := "fttttm",
    version := "0.1",
    scalaVersion := "2.10.2",
    exportJars := true,
    autoCompilerPlugins := true,
    scalacOptions += "-Xexperimental",

    SeerLibs.updateGdxTask,
    SeerLibs.downloadTask

    // scalaSource in Compile := baseDirectory.value / "src",
    // resourceDirectory in Compile := baseDirectory.value / "resources"
  )

  lazy val app = packageArchetype.java_application ++ BuildSettings.defaults ++ Seq (
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
  
object SeerProject {
  def apply(id:String, base:File, settings:Seq[Project.Setting[_]] = BuildSettings.defaults) = Project(id = id, base = base, settings = settings)
}

object SeerBuild extends Build {

  // core
  lazy val seer_core = SeerProject (
    id = "seer-core",
    base = file("seer-core")
  )

  lazy val seer_desktop = SeerProject (
    id = "seer-desktop",
    base = file("seer-desktop")
  ) dependsOn ( seer_core, seer_repl )


  // modules
  lazy val seer_allosphere = SeerProject (
    id = "seer-allosphere",
    base = file("seer-modules/seer-allosphere"),
    settings = BuildSettings.app
  ) dependsOn ( seer_desktop, seer_luaj, seer_eval )

  lazy val seer_kinect = SeerProject (
    "seer-kinect",
    file("seer-modules/seer-kinect")
  ) dependsOn( seer_core, seer_opencv )

  lazy val seer_leap = SeerProject (
    "seer-leap",
    file("seer-modules/seer-leap")
  ) dependsOn seer_core

  lazy val seer_multitouch = SeerProject (
    "seer-multitouch",
    file("seer-modules/seer-multitouch")
  ) dependsOn seer_core

  lazy val seer_opencv = SeerProject (
    "seer-opencv",
    file("seer-modules/seer-opencv")
  ) dependsOn( seer_core, seer_video )

  lazy val seer_video = SeerProject (
    "seer-video",
    file("seer-modules/seer-video")
  ) dependsOn seer_core 

  lazy val seer_portaudio = SeerProject (
    "seer-portaudio",
    file("seer-modules/seer-portaudio")
  ) dependsOn seer_core

  lazy val seer_vrpn = SeerProject ( // TODO get vrpn dependency..
    "seer-vrpn",
    file("seer-modules/seer-vrpn")
  ) dependsOn seer_core

  lazy val seer_jruby = SeerProject (
    "seer-jruby",
    file("seer-modules/seer-dynamic/seer-jruby")
  ) dependsOn seer_core
  lazy val seer_luaj = SeerProject (
    "seer-luaj",
    file("seer-modules/seer-dynamic/seer-luaj")
  ) dependsOn seer_core
  lazy val seer_eval = SeerProject (
    "seer-eval",
    file("seer-modules/seer-dynamic/seer-eval")
  ) dependsOn seer_core
  lazy val seer_repl = SeerProject (
    "seer-repl",
    file("seer-modules/seer-dynamic/seer-repl")
  ) dependsOn seer_core 


  // examples
  lazy val examples = Project (
    "examples",
    file("examples"),
    settings = BuildSettings.app
  ) dependsOn( seer_desktop, seer_opencv, seer_jruby, seer_multitouch )

  // lazy val experiments_fieldViewer = Project (
  //   "experiments-fieldViewer",
  //   file("experiments/fieldViewer"),
  //   settings = Settings.desktop
  // ) dependsOn seer_desktop

  // lazy val experiments_particleSystems = Project (
  //   "experiments-particleSystems",
  //   file("experiments/particleSystems"),
  //   settings = Settings.desktop
  // ) dependsOn( seer_desktop, seer_opencv )

  // lazy val experiments_kinect = Project (
  //   "experiments-kinect",
  //   file("experiments/kinect"),
  //   settings = Settings.desktop
  // ) dependsOn( seer_desktop, seer_kinect )

  // lazy val experiments_opencv = Project (
  //   "experiments-opencv",
  //   file("experiments/opencv"),
  //   settings = Settings.desktop
  // ) dependsOn( seer_desktop, seer_opencv )

  // lazy val experiments_video = Project (
  //   "experiments-video",
  //   file("experiments/video"),
  //   settings = Settings.desktop
  // ) dependsOn( seer_desktop, seer_video )


  // experiments
  lazy val experiments = SeerProject(
    id = "experiments",
    base = file("experiments"),
    settings = BuildSettings.app
  ) dependsOn( seer_desktop, seer_opencv, seer_allosphere, seer_portaudio, seer_kinect, seer_luaj, seer_vrpn, seer_eval, seer_jruby, seer_multitouch )


  // apps
  lazy val loop = Project (
    "loop",
    file("apps/desktop/loop"),
    settings = BuildSettings.app
  ) dependsOn( seer_desktop )

  lazy val trees = Project (
    "trees",
    file("apps/desktop/trees"),
    settings = BuildSettings.app
  ) dependsOn( seer_desktop, seer_kinect, seer_jruby, seer_multitouch )

  // // android apps
  // lazy val loop_android = Project (
  //   "loop-android",
  //   file("apps/android/loop"),
  //   settings = Settings.android
  // ) dependsOn seer_core
}

