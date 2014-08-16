import sbt._

import Keys._

object SeerModulesBuild extends Build {

	import SeerBuild.seer_core
  import SeerBuild.seer_gdx
  import SeerBuild.seer_gdx_desktop_app


	// interaction
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
  ) dependsOn (seer_gdx )

  lazy val seer_vrpn = SeerProject ( // TODO get vrpn dependency..
    "seer-vrpn",
    file("seer-modules/seer-vrpn")
  ) dependsOn seer_core


  // image video computer vision
  lazy val seer_opencv = SeerProject (
    "seer-opencv",
    file("seer-modules/seer-opencv")
  ) dependsOn( seer_core, seer_video )

  lazy val seer_video = SeerProject (
    "seer-video",
    file("seer-modules/seer-video")
  ) dependsOn seer_gdx 


  // audio
  lazy val seer_portaudio = SeerProject (
    "seer-portaudio",
    file("seer-modules/seer-portaudio")
  ) dependsOn seer_core


  // dynamic and livecoding related
  lazy val seer_jruby = SeerProject (
    "seer-jruby",
    file("seer-modules/seer-dynamic/seer-jruby")
  ) dependsOn seer_core

  lazy val seer_luaj = SeerProject (
    "seer-luaj",
    file("seer-modules/seer-dynamic/seer-luaj")
  ) dependsOn seer_core

  lazy val seer_script = SeerProject (
    "seer-eval",
    file("seer-modules/seer-dynamic/seer-eval")
  ) dependsOn( seer_core )

  lazy val seer_repl = SeerProject (
    "seer-repl",
    file("seer-modules/seer-dynamic/seer-repl")
  ) dependsOn seer_core 


  // allosphere related
  lazy val seer_allosphere = SeerProject (
    id = "seer-allosphere",
    base = file("seer-modules/seer-allosphere"),
    settings = BuildSettings.app
  ) dependsOn ( seer_gdx_desktop_app, seer_luaj, seer_script )
}

