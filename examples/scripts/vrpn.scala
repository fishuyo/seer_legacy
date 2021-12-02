
class VRPNTest extends SeerActor {

  val osc = new flow.OSCSend()
  osc.connect("localhost", 7000)

  var ps = List[(Vec3,Float)]() 
  VRPN.clear()
  // VRPN.analogListen("PoseNet0@67.58.49.49:3083", (data) => {  
  // VRPN.analogListen("blyn.pose0@tcp://67.58.49.49", (data) => {  
  // VRPN.analogListen("PoseNet0@tcp://49.49.58.67:3083" , (data) => {  
  // VRPN.analogListen("PoseNet0@Thunder.local", (data) => { 
  // VRPN.analogListen("PoseNet0@tcp://localhost", (data) => { 
  VRPN.analogListen("tim.pose0@tcp://67.58.49.49", (data) => {  
  // VRPN.analogListen("tim.pose0@localhost", (data) => { 
    // osc.send("/pose0", data.map(_.toFloat): _*)
    ps = data.init.grouped(4).map { case a => (Vec3(a(0),a(1),a(2)), a(3).toFloat) }.toList
    // println(ps)
  })
  
  override def draw() = {
    ps.foreach { case (p,s) =>
      val v = Vec3(p.x,-p.y,p.z*0.1f)
      val color = RGB(Vec3(1,0,0).lerp(Vec3(0,1,0), s))
      // println(s)
      val sphere = Sphere()
      // sphere.material = Material.basic
      sphere.material.color.set(color)
      if(s > 0.5f) sphere.scale(0.1f).translate(v).draw()
    }
  }


}

classOf[VRPNTest]