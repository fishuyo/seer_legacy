
resolvers in ThisBuild ++= Seq(
    "Xuggle Repo" at "http://xuggle.googlecode.com/svn/trunk/repo/share/java/"
)

libraryDependencies += "xuggle" % "xuggle-xuggler" % "5.4"
