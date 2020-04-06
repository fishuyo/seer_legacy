// lazy val core = LocalProject("core")
// lazy val app = LocalProject("gdx_app_desktop")
// lazy val graphics = LocalProject("gdx_graphics")

// // lazy val test = ProjectRef(file("modules/addons/test"), "test")

// /**
//   * Addon Modules
//   */
// /**
//   * Dynamic - live coding, repl
//   */
// // lazy val script = project
// //   .in(file("modules/addons/script"))
// //   .dependsOn(core, graphics)
// //   .settings(Settings.common: _*)

// lazy val repl = project
//   .in(file("modules/addons/repl"))
//   .dependsOn(core)
//   .settings(Settings.app: _*)

// /**
//   * HCI Modules - trackpad, kinect, opencv
//   */
// lazy val apple_trackpad = project
//   .in(file("modules/addons/apple-trackpad"))
//   .dependsOn(core)
//   .settings(Settings.common: _*)

// lazy val openni = project
//   .in(file("modules/addons/openni"))
//   .dependsOn(core)
//   .settings(Settings.common: _*)

// lazy val openni2 = project
//   .in(file("modules/addons/openni2"))
//   .dependsOn(core)
//   .settings(Settings.common: _*)

// lazy val openni2_examples = project
//   .in(file("modules/addons/openni2/examples"))
//   .dependsOn(openni2, app)
//   .settings(Settings.app: _*)

// // lazy val javacv = project.in(file("modules/addons/javacv"))
// // lazy val opencv = project.in(file("modules/addons/opencv")).dependsOn(core).settings(Settings.common: _*)
// // settings(libraryDependencies ++= Dependencies.opencvD)

// lazy val hid = project
//   .in(file("modules/addons/hid"))
//   .dependsOn(core, graphics)
//   .settings(Settings.common: _*)
//   .settings(libraryDependencies ++= Dependencies.hid)

// /**
//   * Video playback
//   */
// lazy val video = project
//   .in(file("modules/addons/video"))
//   .dependsOn(core, app)
//   .settings(Settings.common: _*)

// /**
//   * VR support
//   */
// lazy val openvr = project
//   .in(file("modules/addons/openvr"))
//   .dependsOn(core, graphics, app)
//   .settings(Settings.common: _*)
//   .settings(libraryDependencies ++= Dependencies.openvr)

// lazy val rift = project
//   .in(file("modules/addons/rift"))
//   .dependsOn(core, graphics, app)
//   .settings(Settings.common: _*)

// /**
//   * libGdx extensions
//   */
// lazy val gdx_box2d = project
//   .in(file("modules/backends/gdx/gdx-box2d"))
//   .dependsOn(core)
//   .settings(Settings.common: _*)

// lazy val gdx_vr = project
//   .in(file("modules/backends/gdx/gdx-vr"))
//   .dependsOn(core, graphics, app)
//   .settings(Settings.common: _*)
//   .settings(libraryDependencies ++= Dependencies.openvr)
