resolvers in ThisBuild ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

libraryDependencies ++= Seq(
  //"com.googlecode.scalascriptengine" %% "scalascriptengine" % "1.3.10",
  //"joda-time" % "joda-time" % "2.9",
  //"org.slf4j" % "slf4j-api" % "1.7.5",
  //"org.slf4j" % "slf4j-simple" % "1.7.5",
  "com.twitter" % "util-eval_2.11" % "6.29.0",
  //"org.scalameta" %% "scalameta" % "0.1.0-SNAPSHOT",
  //"org.scalameta" %% "scalahost" % "0.1.0-SNAPSHOT",
  "org.scala-lang" % "scala-reflect" % "2.11.8",
  "org.scala-lang" % "scala-compiler" % "2.11.8",
  "org.scala-lang" % "scala-library" % "2.11.8"
)