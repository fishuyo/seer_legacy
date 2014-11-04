import sbt._

import Keys._

import com.typesafe.sbt.SbtNativePackager._

import seer.unmanaged._


object BuildSettings {
  val defaults = Defaults.defaultSettings ++ Seq(
    organization := "fttttm",
    version := "0.1",
    scalaVersion := "2.11.2",
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

  import SeerModulesBuild._

  // core
  lazy val seer_core = SeerProject (
    id = "seer-core",
    base = file("seer-core")
  )

  lazy val seer_gdx = SeerProject (
    id = "seer-gdx",
    base = file("seer-gdx")
  ) dependsOn seer_core

  lazy val seer_gdx_desktop_app = SeerProject (
    id = "seer-gdx-desktop-app",
    base = file("seer-gdx/seer-gdx-desktop-app")
  ) dependsOn (seer_gdx, seer_script)



  // examples
  lazy val examples = SeerProject (
    "examples",
    file("examples"),
    settings = BuildSettings.app
  ) dependsOn( seer_gdx_desktop_app, seer_multitouch )

  lazy val examples_audio = SeerProject (
    "examples-audio",
    file("examples/audio"),
    settings = BuildSettings.app
  ) dependsOn( seer_gdx_desktop_app, seer_portaudio, seer_rtaudio, seer_multitouch )

  lazy val examples_bullet = SeerProject (
    "examples-bullet",
    file("examples/bullet"),
    settings = BuildSettings.app
  ) dependsOn( seer_gdx_desktop_app, seer_multitouch, seer_bullet )

}

