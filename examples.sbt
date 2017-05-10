
/**
* Example projects
*/
lazy val coreJVM = LocalProject("coreJVM")
lazy val coreJS = LocalProject("coreJS")
lazy val app = LocalProject("gdx_app_desktop")
lazy val graphics = LocalProject("gdx_graphics")
lazy val openni = LocalProject("openni")
lazy val openni2 = LocalProject("openni2")

lazy val examples_graphics = project.in(file("examples/jvm/graphics")).
  dependsOn(app).
  settings(Common.appSettings :_*)

lazy val examples_openni = project.in(file("examples/jvm/openni")).
  dependsOn(app, openni2).
  settings(Common.appSettings :_*)

/**
* EXAMPLES scalaJS
*/
// lazy val examples_js_particle = project.in(file("examples/js/particle")).
//   dependsOn(coreJS).
//   settings(Common.jsSettings: _*)

/**
* EXAMPLE crossProject both JVM and JS versions
*/
// lazy val test = crossProject.in(file("examples/test")).
//   settings(Common.settings: _*)
// lazy val testJVM = test.jvm.dependsOn(coreJVM)
// lazy val testJS = test.js.dependsOn(coreJS)


lazy val uiProto = crossProject.in(file("examples/uiProto")).
  settings( Common.settings :_* ).
  jvmSettings( Common.appSettings :_* ).
  jsSettings( 
    // enablePlugins(ScalaJSPlugin),
    // libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "0.9.1"
    libraryDependencies += "be.doeraene" %%% "scalajs-jquery" % "0.9.1",
    skip in packageJSDependencies := false,
    jsDependencies += "org.webjars" % "jquery" % "2.1.4" / "2.1.4/jquery.js"
  )
lazy val uiProtoJVM = uiProto.jvm.dependsOn(coreJVM, app)
lazy val uiProtoJS = uiProto.js.dependsOn(coreJS)

