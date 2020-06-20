/*
 * Seer Modules - Project Refs
 */
val core = ProjectRef(file("../../.."), "core")
val app = ProjectRef(file("../../.."), "gdx_app_desktop")

val video = project.in(file("."))
  .dependsOn(core, app)
  .settings(Settings.common: _*)
  .settings(libraryDependencies += "uk.co.caprica" % "vlcj" % "3.12.1")
  // .settings(compile in Compile := ((compile in Compile).dependsOn(getLibs)).value)

val examples = project.in(file("examples"))
  .dependsOn(video, app)
  .settings(Settings.app: _*)


// resolvers in ThisBuild ++= Seq(
//     "Xuggle Repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"
// )
// libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.4"
//libraryDependencies += "io.humble" % "humble-video-all" % "0.2.1"
// libraryDependencies += "io.humble" % "humble-video-noarch" % "0.2.1"
// libraryDependencies += "io.humble" % "humble-video-arch-x86_64-apple-darwin12" % "0.2.1"
// libraryDependencies += "uk.co.caprica" % "vlcj" % "3.8.0"