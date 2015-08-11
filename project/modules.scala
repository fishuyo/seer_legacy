import sbt._

import Keys._

object SeerModulesBuild extends Build {

  import SeerBuildSettings._
	import SeerBuild.seer_core
  import SeerBuild.seer_gdx
  import SeerBuild.seer_gdx_desktop_app

  lazy val seer_leap = project.in(file("seer-modules/seer-leap")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_osx_multitouch = project.in(file("seer-modules/seer-osx-multitouch")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_vrpn = project.in(file("seer-modules/seer-vrpn")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_openni = project.in(file("seer-modules/seer-openni")).
    settings(common: _*).dependsOn(seer_gdx)

  lazy val seer_opencv = project.in(file("seer-modules/seer-opencv")).
    settings(common: _*).dependsOn(seer_video)

  lazy val seer_video = project.in(file("seer-modules/seer-video")).
    settings(common: _*).dependsOn(seer_gdx_desktop_app)


  lazy val seer_portaudio = project.in(file("seer-modules/seer-portaudio")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_rtaudio = project.in(file("seer-modules/seer-rtaudio")).
    settings(common: _*).dependsOn(seer_core)


  lazy val seer_jruby = project.in(file("seer-modules/seer-jruby")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_script = project.in(file("seer-modules/seer-script")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_repl = project.in(file("seer-modules/seer-repl")).
    settings(common: _*).dependsOn(seer_core)

  lazy val seer_luaj = project.in(file("seer-modules/seer-luaj")).
    settings(common: _*).dependsOn(seer_core)


  lazy val seer_bullet = project.in(file("seer-modules/seer-bullet")).
    settings(common: _*).dependsOn(seer_gdx)

  lazy val seer_box2d = project.in(file("seer-modules/seer-box2d")).
    settings(common: _*).dependsOn(seer_gdx)

  lazy val seer_allosphere = project.in(file("seer-modules/seer-allosphere")).
    settings(app: _*).dependsOn(seer_gdx_desktop_app, seer_luaj, seer_script)

}

