lazy val getLibs:TaskKey[Unit] = TaskKey[Unit]("download unmanaged libs.")

libraryDependencies += Dependencies.scodec

getLibs := {
  val dir = baseDirectory.value / "lib"
  Utilities.getOpenNI2(dir)
}

// compile in Compile <<= (compile in Compile).dependsOn(getLibs)
compile in Compile := ((compile in Compile).dependsOn(getLibs)).value
// a := (a dependsOn b).value