

lazy val seer = project.in(file(".")). //SeerUnmanagedLibs.downloadTask).
  aggregate(coreJVM)


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



/**
* EXAMPLES
*/
lazy val examples = project.in(file("examples/jvm")).
  dependsOn(gdx_app_desktop).
  settings(Common.appSettings: _*)

lazy val examples_graphics = project.in(file("examples/jvm/graphics")).
  dependsOn(gdx_app_desktop).
  settings(Common.appSettings: _*)

lazy val examples_openni = project.in(file("examples/jvm/openni")).
  dependsOn(gdx_app_desktop, openni).
  settings(Common.appSettings: _*)

lazy val examples_opencv = project.in(file("examples/jvm/opencv")).
  dependsOn(gdx_app_desktop, opencv).
  settings(Common.appSettings: _*)

lazy val examples_video = project.in(file("examples/jvm/video")).
  dependsOn(gdx_app_desktop, video).
  settings(Common.appSettings: _*)

/**
* EXAMPLES scalaJS
*/
lazy val examples_js_particle = project.in(file("examples/js/particle")).
  dependsOn(coreJS).
  settings(Common.jsSettings: _*)

/**
* EXAMPLE crossProject both JVM and JS versions
*/
lazy val test = crossProject.in(file("examples/test")).
  settings(Common.settings: _*)
lazy val testJVM = test.jvm.dependsOn(coreJVM)
lazy val testJS = test.js.dependsOn(coreJS)


//
lazy val soma = project.in(file("works/soma")).
  dependsOn(gdx_app_desktop, openni, portaudio, script).
  settings(Common.appSettings: _*)

lazy val becominglightVR = project.in(file("works/becominglightVR")).
  settings(Common.appSettings: _*).
  dependsOn(gdx_app_desktop, portaudio, openni, script, rift)



lazy val dailyworlds = project.in(file("dailyworlds")).
  dependsOn(gdx_app_desktop, gdx_graphics, openni, portaudio, script).
  settings(Common.appSettings: _*)

