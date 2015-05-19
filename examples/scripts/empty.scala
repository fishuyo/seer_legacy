
import com.fishuyo.seer._

import dynamic.SeerScript
import graphics._
import io._

import de.sciss.osc._

import collection.mutable.ListBuffer

object Script extends SeerScript {

  Keyboard.bindCamera()

  val cubes = ListBuffer[Model]()
  val w = 3
  for( i <- -w to w; j <- -w to w; k <- -w to w){
    val c = Cube().translate(i,j,k).scale(0.1)
    c.material = Material.basic
    c.material.color = RGBA(.5,.5,.5,.5)
    cubes += c
  }

  val selection = Cube()
  selection.mesh.primitive = Lines
  selection.material = Material.basic
  selection.material.color = RGB.white


  var pinched = false
  var wasPinched = false
  var pinchPos = Vec3()
  var pinchOff = Vec3()

  override def draw(){
    FPS.print
    cubes.foreach( _.draw )
    selection.draw
  }
  override def animate(dt:Float){
    // cubes.zipWithIndex.foreach {
    //   case(c,i) => c.rotate(0,(i+1)*s/100*boost,0)
    // }
    if( Mouse.status() == "down") beginSelect(Mouse.xy())
    else if( Mouse.status() == "drag" ) moveSelect(Mouse.xy())
    else if( Mouse.status() == "up") endSelect(Mouse.xy())

    if( !wasPinched && pinched) beginSelect(pinchPos)
    else if( pinched && wasPinched) moveSelect(pinchOff)
  }

  implicit def f2i(f:Float) = f.toInt

  def beginSelect(p:Vec2){
    val r = Camera.ray(p.x*Window.width,(1-p.y)*Window.height)
    // val t = r.intersectSphere(Camera.nav.pos, 5)
    val tl = r(5)
    selection.pose.pos.set( tl )
    selection.scale.set(0)
  }
  def beginSelect(p:Vec3){
    selection.pose.pos.set( p )
    selection.scale.set(0)
  }

  def moveSelect(p:Vec2){
    val r = Camera.ray(p.x*Window.width,(1-p.y)*Window.height)
    val br = r(5)
    val tl = selection.pose.pos - selection.scale / 2
    val scale = (br - tl)
    val pos = tl + scale/2
    selection.pose.pos.set(pos)
    selection.scale.set(scale)
  }

  def moveSelect(p:Vec3){
    val br = pinchPos + p
    val tl = selection.pose.pos - selection.scale / 2
    val scale = (br - tl)
    val pos = tl + scale/2
    selection.pose.pos.set(pos)
    selection.scale.set(scale)
  }

  def endSelect(p:Vec2){}

  OSC.clear()
  OSC.disconnect()
  OSC.listen(8080)
  // OSC.bind("/new_user", (f) => { OSC.send("/calibrating", f(0)) })
  OSC.bindp {
    case Message("/indexPinch0", pinch) =>
      wasPinched = pinched
      pinched = (pinch == 1.0)
      // println(pinch)
    case Message("/indexPinchPos0", x:Float,y:Float,z:Float) =>
      pinchPos.set(x,y,z)
    case Message("/indexPinchOffset0", x:Float,y:Float,z:Float) =>
      pinchOff.set(x,y,z)

    // case Message("/pinch1", pinch:Float) =>
      // println("pinch1: "+pinch)
    // case m => println(m)
    case _ => ()
  }

  Trackpad.clear()
  Trackpad.connect()
  Trackpad.bind { case touch =>
    // val f = touch.fingers(0)
  }

}

Script