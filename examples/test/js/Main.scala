import scala.scalajs.js

object Main extends js.JSApp {
  def main(): Unit = {
    val lib = new MyLibrary
    println(lib.sq(2))
    println(com.fishuyo.seer.spatial.Vec3(3,3,3))
  }
}