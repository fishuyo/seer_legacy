
import com.fishuyo.seer.spatial.Vec3

object Main {
  def main(args: Array[String]): Unit = {
    val lib = new MyLibrary
    println(lib.sq(2))

    println(Vec3(3)*Vec3(4))
    // println(Vec3(3f)*Vec3(2f))
    // println(Vec3(3.0))
    // println(Vec3(3L))
  }
}