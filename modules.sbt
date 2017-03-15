
/**
* Addon Modules
*/
lazy val core = LocalProject("coreJVM")
lazy val app = LocalProject("gdx_app_desktop")
lazy val graphics = LocalProject("gdx_graphics")

lazy val openni = project.in(file("modules/openni")).
  dependsOn(core).
  settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.openniD)

lazy val opencv = project.in(file("modules/opencv")).
  dependsOn(core).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.opencvD)

lazy val video = project.in(file("modules/video")).
  dependsOn(core, app).
  settings(Common.settings: _*)

lazy val portaudio = project.in(file("modules/portaudio")).
  dependsOn(core).
  settings(Common.settings: _*)

lazy val script = project.in(file("modules/script")).
  dependsOn(core, graphics, openni).
  settings(Common.settings: _*)

lazy val openvr = project.in(file("modules/openvr")).
  dependsOn(core, graphics, app).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)

lazy val gdx_vr = project.in(file("modules/gdx-vr")).
  dependsOn(coreJVM, gdx_graphics, gdx_app_desktop).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)

lazy val rift = project.in(file("modules/rift")).
  dependsOn(core, graphics, app).
  settings(Common.settings: _*)

lazy val hid = project.in(file("modules/hid")).
  dependsOn(core).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.hidD)
