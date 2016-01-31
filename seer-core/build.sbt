
resolvers in ThisBuild ++= Seq(
  //"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
  //"com.lihaoyi" % "ammonite-repl" % "0.4.8" cross CrossVersion.full,
  "com.lihaoyi" % "ammonite-sshd" % "0.4.8" cross CrossVersion.full,
  "com.typesafe.akka" %% "akka-actor" % "2.3.4", //"2.4-SNAPSHOT",
	"com.typesafe.akka" %% "akka-remote" % "2.3.4", //"2.4-SNAPSHOT",
  "com.twitter" %% "chill" % "0.5.2",
  "com.twitter" %% "chill-bijection" % "0.5.2",
  "com.twitter" %% "chill-akka" % "0.5.2",

	//"org.scala-lang" % "scala-actors" % "2.11.2",
	//"de.sciss" %% "scalaosc" % "1.1.+",
	"de.sciss" %% "scalaaudiofile" % "1.4.3+", //"1.4.+",
  "de.sciss" %% "scalaosc" % "1.1.+",
	"net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
	"com.scalarx" %% "scalarx" % "0.2.6",
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5"
)