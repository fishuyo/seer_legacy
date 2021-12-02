
import seer.ui._

class Curve {
  val mesh = Mesh()
  val model = Model(mesh)
  mesh.primitive = LineStrip
  

  val cp = Array(Vec3(),Vec3(0.3,0,0),Vec3(0.7,0.7,0),Vec3(1,0.7,0))
  

  def point(t:Float) = {
		val t1 = 1f - t
		cp(0) * t1 * t1 * t1 + cp(1) * 3f * t1 * t1 * t + cp(2) * 3f * t1 * t * t + cp(3) * t * t * t
  }

  def tangent(t:Float) = {
    val t1 = 1f - 1
    (cp(1) - cp(0)) * 3f * t1 * t1 + (cp(2) - cp(1)) * 6f * t1 * t + (cp(3)-cp(2)) * 3f * t * t
  }

  def update() = {
    val n = 100
    mesh.clear
    for(i <- 0 until n){
      val t = i.toFloat / (n-1)
      mesh.vertices += point(t)
    }
    mesh.update
  }

  def draw() = {
    model.draw
    cp.foreach { case p =>
      val s = Sphere().scale(0.01)
      s.translate(p)
      s.draw
    }
  }

} 


class Curves extends SeerActor {

  val c = new Curve

  override def draw() = {
    c.update
    c.draw
  }


}

classOf[Curves]