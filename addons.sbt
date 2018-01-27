
lazy val core = LocalProject("coreJVM")
lazy val app = LocalProject("gdx_app_desktop")
lazy val graphics = LocalProject("gdx_graphics")

/**
* Addon Modules
*/

/** Allows for livecoding via scala scripts */
lazy val script = project.in(file("modules/addons/script")).
  dependsOn(core, graphics).
  settings(Common.settings: _*)


lazy val openni = project.in(file("modules/addons/openni")).
  dependsOn(core).
  settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.openniD)

lazy val openni2 = project.in(file("modules/addons/openni2")).
  dependsOn(core).
  settings(Common.settings: _*)

lazy val opencv = project.in(file("modules/addons/opencv")).
  dependsOn(core).
  settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.opencvD)

lazy val video = project.in(file("modules/addons/video")).
  dependsOn(core, app).
  settings(Common.settings: _*)

lazy val openvr = project.in(file("modules/addons/openvr")).
  dependsOn(core, graphics, app).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)

lazy val rift = project.in(file("modules/addons/rift")).
  dependsOn(core, graphics, app).
  settings(Common.settings: _*)

lazy val hid = project.in(file("modules/addons/hid")).
  dependsOn(core, graphics).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.hidD)


lazy val gdx_box2d = project.in(file("modules/backends/gdx/gdx-box2d")).
  dependsOn(core).
  settings(Common.settings: _*)