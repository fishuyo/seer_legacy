import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

/**
  * Core Modules
  */
lazy val core = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(Settings.common: _*)
  .jvmSettings(libraryDependencies ++= Dependencies.core)

/**
  * Gdx backend
  */
lazy val gdx_graphics = project
  .in(file("modules/backends/gdx/gdx-graphics"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.gdx)

lazy val gdx_app_desktop = project
  .in(file("modules/backends/gdx/gdx-app-desktop"))
  .dependsOn(gdx_graphics)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.gdxAppDesktop)

/**
  * Audio backends
  */
lazy val portaudio = project
  .in(file("modules/backends/portaudio"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)

lazy val jackaudio = project
  .in(file("modules/backends/jackaudio"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)

/**
  * Examples
  */
lazy val examples = project
  .in(file("examples"))
  .dependsOn(gdx_app_desktop, portaudio, jackaudio)
  .settings(Settings.app: _*)
