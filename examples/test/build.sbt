// name := "examples_test"

// scalaVersion in ThisBuild := "2.11.8"

// // lazy val root = project.in(file(".")).
// //   aggregate(testJS, testJVM).
// //   settings(
// //     publish := {},
// //     publishLocal := {}
// //   )

// // lazy val core = RootProject(file("../../modules/core"))
// lazy val coreJVM = ProjectRef(file("../../modules/core"),"coreJVM")

// lazy val test = crossProject.in(file(".")).
//   settings(
//     name := "test",
//     version := "0.1-SNAPSHOT"
//   ).
//   jvmSettings(
//     // Add JVM-specific settings here
//   ).
//   jsSettings(
//     // Add JS-specific settings here
//   )

// lazy val testJS = test.js
// lazy val testJVM = test.jvm dependsOn coreJVM