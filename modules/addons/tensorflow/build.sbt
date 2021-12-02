/*
 * Seer Modules - Project Refs
 */
val core = ProjectRef(file("../../.."), "core")
val app = ProjectRef(file("../../.."), "gdx_app_desktop")

val tensorflow = project.in(file("."))
  .dependsOn(core)
  .settings(Settings.common: _*)
  .settings(libraryDependencies += "org.platanios" %% "tensorflow" % "0.4.1" classifier "darwin-cpu-x86_64")
  // .settings(libraryDependencies += "org.platanios" %% "tensorflow" % "0.4.1")

val examples = project.in(file("examples"))
  .dependsOn(tensorflow, app)
  .settings(Settings.app: _*)


