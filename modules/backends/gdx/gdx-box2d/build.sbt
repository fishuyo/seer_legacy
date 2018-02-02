libraryDependencies ++= Seq(
  "com.badlogicgames.gdx" % "gdx-box2d" % Dependencies.libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-box2d-platform" % Dependencies.libgdxVersion classifier "natives-desktop"
)