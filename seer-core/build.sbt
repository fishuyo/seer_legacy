
resolvers in ThisBuild ++= Seq(
  "typesafe snap" at "http://repo.typesafe.com/typesafe/snapshots"
)

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.4-SNAPSHOT", //2.1.4",
	"com.typesafe.akka" %% "akka-remote" % "2.4-SNAPSHOT", //2.1.4",
	"com.typesafe.akka" %% "akka-cluster" % "2.4-SNAPSHOT",
	"com.typesafe.akka" %% "akka-contrib" % "2.4-SNAPSHOT",
	"org.scala-lang" % "scala-actors" % "2.10.2",
	"de.sciss" %% "scalaosc" % "1.1.+",
	"de.sciss" %% "scalaaudiofile" % "1.2.0", //"1.4.+",
	"net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
	"net.java.dev.jna" % "jna" % "3.5.2",
	"com.scalarx" % "scalarx_2.10" % "0.2.3"
)