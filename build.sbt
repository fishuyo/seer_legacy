
/**
* Core Modules
*/
lazy val core = crossProject.in(file("modules/core")).
  settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.coreD)
lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val gdx_graphics = project.in(file("modules/gdx-graphics")).
  dependsOn(coreJVM).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxD)

lazy val gdx_app_desktop = project.in(file("modules/gdx-app-desktop")).
  dependsOn(gdx_graphics).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxAppDesktopD)


/**
* Addon Modules
*/
lazy val openni = project.in(file("modules/openni")).
  dependsOn(coreJVM).
  settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.openniD)

lazy val opencv = project.in(file("modules/opencv")).
  dependsOn(coreJVM).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.opencvD)

lazy val video = project.in(file("modules/video")).
  dependsOn(coreJVM, gdx_app_desktop).
  settings(Common.settings: _*)

lazy val portaudio = project.in(file("modules/portaudio")).
  dependsOn(coreJVM).
  settings(Common.settings: _*)

lazy val script = project.in(file("modules/script")).
  dependsOn(coreJVM, gdx_graphics, openni).
  settings(Common.settings: _*)

lazy val openvr = project.in(file("modules/openvr")).
  dependsOn(coreJVM, gdx_graphics, gdx_app_desktop).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)

lazy val rift = project.in(file("modules/rift")).
  dependsOn(coreJVM, gdx_graphics, gdx_app_desktop).
  settings(Common.settings: _*)

lazy val hid = project.in(file("modules/hid")).
  dependsOn(coreJVM).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.hidD)



