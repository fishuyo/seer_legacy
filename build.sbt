import sbtcrossproject.CrossPlugin.autoImport.{crossProject, CrossType}

/**
  * Core Modules
  */

lazy val util = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/util"))
  .settings(Settings.common: _*)

lazy val spatial = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/spatial"))
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.spatial.value)

lazy val audio = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/audio"))
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.audio.value)
  .dependsOn(spatial)

lazy val graphics = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/graphics"))
  .settings(Settings.common: _*)
  .dependsOn(spatial)

lazy val runtime = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/runtime"))
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.runtime.value)
  .dependsOn(spatial, audio, graphics)

lazy val ui = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/ui"))
  .settings(Settings.common: _*)
  .dependsOn(spatial, runtime)

lazy val worldmaking = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core/worldmaking"))
  .settings(Settings.common: _*)
  .dependsOn(spatial, graphics)

lazy val core = crossProject(JVMPlatform, JSPlatform)
  .withoutSuffixFor(JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("modules/core"))
  .settings(Settings.common: _*)
  .aggregate(util, spatial, audio, graphics, runtime, ui, worldmaking)
  // .jvmSettings(libraryDependencies ++= Dependencies.coreJvm.value)
  // .jsSettings(libraryDependencies ++= Dependencies.coreJs.value)

lazy val flow = project
  .in(file("modules/flow"))
  .dependsOn(runtime.jvm)
  .settings(Settings.common: _*)

lazy val script = project
  .in(file("modules/script"))
  .dependsOn(runtime.jvm, gdx_graphics, flow) // TODO remove gdx_graphics dependency
  .settings(Settings.common: _*)


  
/**
  * Backend
  */
lazy val gdx_graphics = project
  .in(file("modules/backends/gdx/gdx-graphics"))
  .dependsOn(spatial.jvm, audio.jvm, graphics.jvm, ui.jvm, worldmaking.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.gdx)
  .settings(libraryDependencies ++= Dependencies.rx.value)

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

lazy val backend_webgl = project.enablePlugins(ScalaJSPlugin)
  .in(file("modules/backends/web/webgl-graphics"))
  .settings(Settings.common: _*)
  .settings(scalaJSUseMainModuleInitializer := true)
  .dependsOn(core.js)

/**
  * Audio backends
  */
lazy val portaudio = project
  .in(file("modules/backends/audio/portaudio"))
  .dependsOn(audio.jvm)
  .settings(Settings.common: _*)

lazy val jackaudio = project
  .in(file("modules/backends/audio/jackaudio"))
  .dependsOn(audio.jvm)
  .settings(Settings.common: _*)

/**
 * Addon Modules
 */

lazy val trackpad = project
  .in(file("modules/addons/apple-trackpad"))
  .dependsOn(spatial.jvm)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Dependencies.rx.value)

lazy val openni2 =  RootProject(file("modules/addons/openni2"))
lazy val video =  RootProject(file("modules/addons/video"))
lazy val javacv =  RootProject(file("modules/addons/javacv"))

/**
  * Examples
  */
lazy val examples = project
  .in(file("examples"))
  .dependsOn(gdx_app_desktop, portaudio, jackaudio, script, trackpad, openni2, video, javacv)
  .settings(Settings.app: _*)

