/*
 * Seer Modules - Project Refs
 */
val core = ProjectRef(file("../../.."), "core")
val app = ProjectRef(file("../../.."), "gdx_app_desktop")

val openni2 = project.in(file("."))
  .dependsOn(core)
  .settings(Settings.common: _*)
  .settings(libraryDependencies += "org.scodec" %% "scodec-core" % "1.10.3")
  // .settings(compile in Compile := ((compile in Compile).dependsOn(getLibs)).value)

val examples = project.in(file("examples"))
  .dependsOn(openni2, app)
  .settings(Settings.app: _*)


// lazy val getLibs:TaskKey[Unit] = TaskKey[Unit]("download unmanaged libs.")

// getLibs := {
//   val dir = baseDirectory.value / "lib"
//   Utilities.getOpenNI2(dir)
// }

// compile in Compile <<= (compile in Compile).dependsOn(getLibs)
// compile in Compile := ((compile in Compile).dependsOn(getLibs)).value
// a := (a dependsOn b).value