
resolvers in ThisBuild ++= Seq(
  //"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.3.4", //"2.4-SNAPSHOT",
	"org.scala-lang" % "scala-actors" % "2.10.2",
	//"de.sciss" %% "scalaosc" % "1.1.+",
	"de.sciss" %% "scalaaudiofile" % "1.2.0", //"1.4.+",
	"net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
	"com.scalarx" % "scalarx_2.10" % "0.2.3"
)