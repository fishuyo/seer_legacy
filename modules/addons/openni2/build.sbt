lazy val getLibs:TaskKey[Unit] = TaskKey[Unit]("Download unmanaged libs.")

getLibs := {
  val dir = baseDirectory.value / "lib"
  UnmanagedLibs.getOpenNI2(dir)
}

compile in Compile <<= (compile in Compile).dependsOn(getLibs)
