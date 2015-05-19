import sbt._

import Keys._

import com.typesafe.sbt.SbtNativePackager._

// import seer.unmanaged._

object SeerBuildSettings {
  lazy val common = Defaults.defaultSettings ++ Seq(
    organization := "fttttm",
    version := "0.1",
    scalaVersion := "2.11.6",
    exportJars := true,
    autoCompilerPlugins := true,
    scalacOptions += "-Xexperimental",

    SeerUnmanagedLibs.updateGdxTask,
    SeerUnmanagedLibs.downloadTask

    // scalaSource in Compile := baseDirectory.value / "src",
    // resourceDirectory in Compile := baseDirectory.value / "resources"
  )

  lazy val app = packageArchetype.java_application ++ common ++ Seq (
    fork in run := true,
    // javaOptions in run += "-Djava.library.path=.;./lib;/usr/local/lib", 
    javaOptions in run <<= (fullClasspath in Compile) map { (cp) => 
       val cpString = cp.map(_.data).mkString(System.getProperty("path.separator"))
       Seq("-cp",cpString)
    },
    connectInput in run := true,
    cancelable in Global := true,
    outputStrategy := Some(StdoutOutput)
  )
}
  
object SeerBuild extends Build {

  import SeerBuildSettings._
  import SeerModulesBuild._

  // core
  lazy val seer_core = project.in(file("seer-core")).settings(common: _*)

  // libgdx specific code
  lazy val seer_gdx = project.in(file("seer-gdx")).
    settings(common: _*).dependsOn(seer_core)

  // libgdx desktop specific code
  lazy val seer_gdx_desktop_app = project.in(file("seer-gdx/seer-gdx-desktop-app")).
    settings(common: _*).dependsOn(seer_gdx, seer_script)

  // examples
  lazy val examples = project.settings(app: _*).
    dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_graphics = project.in(file("examples/graphics")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_actor = project.in(file("examples/actor")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app)

  lazy val examples_audio = project.in(file("examples/audio")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_portaudio)

  lazy val examples_particle = project.in(file("examples/particle")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app)

  lazy val examples_trackpad = project.in(file("examples/trackpad")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_osx_multitouch)

  lazy val examples_video = project.in(file("examples/video")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_video)

  lazy val examples_opencv = project.in(file("examples/opencv")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_opencv)

  lazy val examples_openni = project.in(file("examples/openni")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_openni)

  lazy val examples_bullet = project.in(file("examples/bullet")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_bullet)
}

