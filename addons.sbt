
lazy val core = LocalProject("coreJVM")
lazy val app = LocalProject("gdx_app_desktop")
lazy val graphics = LocalProject("gdx_graphics")

/**
* Addon Modules
*/

/** Allows for livecoding via scala scripts */
lazy val script = project.in(file("modules/addons/script")).dependsOn(core, graphics).settings(Common.settings: _*)

/** Apple Trackpad */
lazy val apple_trackpad = project.in(file("modules/addons/apple-trackpad")).dependsOn(core).settings(Common.settings: _*)

/** OpenNI + NiTE */
lazy val openni = project.in(file("modules/addons/openni")).dependsOn(core).settings(Common.settings: _*)

/** OpenNI2 + NiTE2 */
lazy val openni2 = project.in(file("modules/addons/openni2")).dependsOn(core).settings(Common.settings: _*)
lazy val openni2_examples = project.in(file("modules/addons/openni2/examples")).dependsOn(openni2, app).settings(Common.appSettings: _*)

/** Computer Vision */
lazy val opencv = project.in(file("modules/addons/opencv")).dependsOn(core).settings(Common.settings: _*)
  // settings(libraryDependencies ++= Dependencies.opencvD)

/** Video playback */
lazy val video = project.in(file("modules/addons/video")).dependsOn(core, app).settings(Common.settings: _*)

/** OpenVR */
lazy val openvr = project.in(file("modules/addons/openvr")).dependsOn(core, graphics, app).settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)

/** Oculus Rift */
lazy val rift = project.in(file("modules/addons/rift")).dependsOn(core, graphics, app).settings(Common.settings: _*)

/** HID device access */
lazy val hid = project.in(file("modules/addons/hid")).dependsOn(core, graphics).settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.hidD)

/** libGdx extensions */
lazy val gdx_box2d = project.in(file("modules/backends/gdx/gdx-box2d")).dependsOn(core).settings(Common.settings: _*)
lazy val gdx_vr = project.in(file("modules/backends/gdx/gdx-vr")).dependsOn(core, graphics, app).settings(Common.settings: _*).
  settings(libraryDependencies ++= Dependencies.openvrD)



