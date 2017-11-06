/**
* Core Modules
*/
lazy val core = crossProject.in(file("modules/core")).
  settings(Common.settings: _*).
  jvmSettings(libraryDependencies ++= Dependencies.coreD)
lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val gdx_graphics = project.in(file("modules/backends/gdx/gdx-graphics")).
  dependsOn(coreJVM).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxD)

lazy val gdx_app_desktop = project.in(file("modules/backends/gdx/gdx-app-desktop")).
  dependsOn(gdx_graphics).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxAppDesktopD)


/**
* Examples
*/
lazy val examples = project.in(file("examples")).
  dependsOn(gdx_app_desktop).
  settings(Common.appSettings :_*)
