
resolvers in ThisBuild ++= Seq(
  //"Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots",
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-actor" % "2.3.4", //"2.4-SNAPSHOT",
	//"org.scala-lang" % "scala-actors" % "2.11.2",
	//"de.sciss" %% "scalaosc" % "1.1.+",
	"de.sciss" %% "scalaaudiofile" % "1.4.3+", //"1.4.+",
  "de.sciss" %% "scalaosc" % "1.1.+",
	"net.sourceforge.jtransforms" %  "jtransforms" % "2.4.0",
	"com.scalarx" %% "scalarx" % "0.2.6",
  "com.beachape.filemanagement" %% "schwatcher" % "0.1.5"
)