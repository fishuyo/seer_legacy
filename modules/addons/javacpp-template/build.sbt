/*
 * Seer Modules - Project Refs
 */
val core = ProjectRef(file("../../.."), "core")
val app = ProjectRef(file("../../.."), "gdx_app_desktop")

val javacppVersion = "1.5.4"

// Platform classifier for native library dependencies
val platform = org.bytedeco.javacpp.Loader.getPlatform

// Libraries with native dependencies
val bytedecoPresetLibs = Seq(
  "javacpp" -> javacppVersion,
  "opencv" -> s"4.4.0-$javacppVersion",
  "ffmpeg" -> s"4.3.1-$javacppVersion",
  "openblas" -> s"0.3.10-$javacppVersion",
  "openpose" -> s"1.6.0-$javacppVersion"
).flatMap {
  case (lib, ver) => Seq(
    // Add both: dependency and its native binaries for the current `platform`
    "org.bytedeco" % lib % ver withSources() withJavadoc(),
    "org.bytedeco" % lib % ver classifier platform
  )
}

val javacpp = project.in(file("."))
  .dependsOn(core)
  .settings(Settings.common: _*)
  .settings(libraryDependencies ++= Seq(
    "org.bytedeco" % "javacv" % javacppVersion withSources() withJavadoc(),
    "org.scala-lang.modules" %% "scala-swing" % "2.1.1",
    "junit" % "junit" % "4.13" % "test",
    "com.novocode" % "junit-interface" % "0.11" % "test"
  ) ++ bytedecoPresetLibs)

val examples = project.in(file("examples"))
  .dependsOn(javacpp, app)
  .settings(Settings.app: _*)


// resolvers += "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
// resolvers += Resolver.mavenLocal

autoCompilerPlugins := true