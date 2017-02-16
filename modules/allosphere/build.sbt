
//excludeFilter in unmanagedSources := "controller.scala" || "simulator.scala" || ""


libraryDependencies ++= Seq(
	"com.typesafe.akka" %% "akka-remote" % "2.3.4",
	"com.typesafe.akka" %% "akka-cluster" % "2.3.4",
	"com.typesafe.akka" %% "akka-contrib" % "2.3.4"
	//"org.spark-project.akka" %% "akka-zeromq" % "2.3.4"
)
