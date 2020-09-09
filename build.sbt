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

lazy val script = project
  .in(file("modules/script"))
  .dependsOn(core.jvm, gdx_graphics)
  .settings(Settings.common: _*)

  
/**
  * Backend
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

lazy val backend_lwjgl = project
  .in(file("modules/backends/lwjgl/lwjgl-graphics"))
  .dependsOn(gdx_graphics)
  .settings(Settings.app: _*)
  .settings(libraryDependencies ++= Dependencies.gdxAppDesktop)



/**
  * Audio backends
  */
lazy val portaudio = project
  .in(file("modules/backends/audio/portaudio"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)

lazy val jackaudio = project
  .in(file("modules/backends/audio/jackaudio"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)

/**
 * Addon Modules
 */

lazy val trackpad = project
  .in(file("modules/addons/apple-trackpad"))
  .dependsOn(core.jvm)
  .settings(Settings.common: _*)

/**
  * Examples
  */
lazy val examples = project
  .in(file("examples"))
  .dependsOn(gdx_app_desktop, portaudio, jackaudio, script, trackpad)
  .settings(Settings.app: _*)
