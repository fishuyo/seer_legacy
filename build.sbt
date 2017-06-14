
/**
* Core Modules
*/
lazy val core = crossProject.in(file("modules/core")).
  settings(Common.settings: _*).
  jvmSettings(libraryDependencies ++= Dependencies.coreD)
lazy val coreJVM = core.jvm
lazy val coreJS = core.js

lazy val graphics = project.in(file("modules/graphics"))

lazy val gdx_graphics = project.in(file("modules/gdx-backend/gdx-graphics")).
  dependsOn(coreJVM).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxD)

lazy val gdx_app_desktop = project.in(file("modules/gdx-backend/gdx-app-desktop")).
  dependsOn(gdx_graphics).
  settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.gdxAppDesktopD)





