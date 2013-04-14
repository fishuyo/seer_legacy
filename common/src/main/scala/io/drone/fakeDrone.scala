package com.fishuyo
package drone

import graphics._
import maths._
import spatial._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.GL10

class FakeDrone extends GLAnimatable {

	var destCube = GLPrimitive.cube(Pose(), Vec3(.02f,.02f,.02f))
	//var drone = ObjParser("src/main/scala/drone/drone.obj")
  var drone = GLPrimitive.cube(Pose(), Vec3(0.5f,.05f,.5f))
	var velocity = Vec3()
	var acceleration = Vec3()
	var thrust = 0.f
	var g = Vec3(0,-9.8f,0)
	var mass = 1.f

	var flying = false
	var navigating = false
	var takingOff = false
  var physics = true
  def setPhysics(b:Boolean) = physics = b

	//controller .4
	var pose = Pose()
  var vel = Vec3()
  var accel = Vec3()
  var jerk = Vec3()
  var euler = Vec3()  // relates to accel  -->  x'' = g*tan(theta)
  var angVel = Vec3()       // relates to jerk  --> x''' = g*sec(theta)^2
  var t0:Long = 0
  var (t,time1,time2,d0,neg) = (Vec3(),Vec3(),Vec3(),Vec3(),Array[Boolean](false,false,false))
  var expected_a = Vec3()
  var expected_v = Vec3()
  var (kp,kd,kdd) = (Vec3(1.85,8.55,1.85),Vec3(.75),Vec3(1))

   // control input (% of maximum)(delta relates to angVel and to jerk)
  var control = Vec3()
  var rot = 0.f
  var maxEuler = .6f  //(0 - .52 rad)
  def setMaxEuler(f:Float) = maxEuler = f
  var maxVert = 1000   //(200-2000 mm/s)
  var maxRot = 3.0f    //(.7 - 6.11 rad/s)

  //controller .3
	var dest = Pose()
	var tPos = Vec3()
	var tVel = Vec3()
	var tAcc = Vec3()
	var yaw = 0.f; var destYaw = 0.f

	var posThresh = .1f; var yawThresh = 10.f
	var moveSpeed = 1.f
  def setMoveSpeed(f:Float) = moveSpeed = f
	var vmoveSpeed = 1.f
	var rotSpeed = 1.f
	var smooth = false

  // Plots
  var plot = new Plot2D(100, 15.f)
  plot.pose.pos = Vec3(0.f, 2.f, 0.f)
  var plot2 = new Plot2D(100, 15.f)
  plot2.pose.pos = Vec3(0.f, 2.f, 0.f)
  plot2.color = Vec3(2.f,0.f,0.f)

  var plot3 = new Plot2D(100, 15.f)
  plot3.pose.pos = Vec3(2.f, 2.f, 0.f)
  var plot4 = new Plot2D(100, 15.f)
  plot4.pose.pos = Vec3(2.f, 2.f, 0.f)
  plot4.color = Vec3(2.f,0.f,0.f)

  var trace = new Trace3D(100)



	def eval(state:(Float,Float),dt:Float,dstate:(Float,Float)) = {
		//val s = ()
	}

	override def step(dt:Float){

		val p = drone.pose.pos
		val q = drone.pose.quat
		moveStep2(p.x, p.y, p.z, q.x,q.y,q.z,q.w )

		val angles = q.toEuler()

		if( takingOff && p.y < 0.5f){
			thrust = 12.f
		} else if( takingOff ){
			thrust = 9.8f
			takingOff = false
		}

    if(physics){
  		acceleration.set( drone.pose.uu()*thrust )

  		drone.pose.pos += velocity*dt
  		velocity += (acceleration+g)*dt - velocity*.5f*dt 

  		if( p.y < 0.f){
  			drone.pose.pos.y = 0.f
  			vel.set(0.f,0.f,0.f)
  		}
    }

    plot(acceleration.x)
    plot2(expected_a.x)
    plot3(velocity.x)
    plot4(expected_v.x)

    trace(p)

	}
	override def draw(){
		destCube.draw()
		drone.draw()
    plot.draw()
    plot2.draw()
    plot3.draw()
    plot4.draw()
    trace.draw()
	}

	def move(lr:Float,fb:Float,ud:Float,rot:Float){
		if(!flying) return
		navigating = false
		drone.pose.quat.fromEuler(fb*maxEuler,0.f,-lr*maxEuler)
		thrust = (9.8f + ud)/drone.pose.uu().y
	}
	def navmove(lr:Float,fb:Float,ud:Float,rot:Float){
		drone.pose.quat.fromEuler(fb*maxEuler,0.f,-lr*maxEuler)
		thrust = (9.8f + ud)/drone.pose.uu().y
	}

	def moveTo(x:Float,y:Float,z:Float,w:Float){

		destCube.pose.pos.set(x,y,z)
    dest = Pose(Vec3(x,y,z),Quat())
    destYaw = w;
    while( destYaw < -180.f ) destYaw += 360.f
    while( destYaw > 180.f ) destYaw -= 360.f
    navigating = true
    //t.set(0.f)
    //expected_a.set(0.f)
	}
	def takeOff() = {
		flying = true
		takingOff = true
	}
	def land() = { flying = false; takingOff=false; thrust = 5.f}
	def hover() = { drone.pose.quat.setIdentity(); velocity.set(0,0,0)}

	//def dynamic(f:(Unit)=>Unit} = dynaMove = f
	//var dynaMove = ()=>{}

	//
	// Trajectory Calculation
	//
  def acurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
    var ret=0.f
    if(t < t1) ret = math.sin(math.Pi/t1*t).toFloat
    else if( t > t2) ret = -math.sin(math.Pi/t1*(t-t2)).toFloat
    else ret = 0.f
    ret *= math.tan(maxEuler).toFloat*9.8f
    if(neg) -ret else ret
  }
  def vcurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
    var ret=0.f
    if(t < t1) ret = t1/math.Pi.toFloat*(1.f - math.cos(math.Pi/t1*t).toFloat)  
    else if( t > t2) ret = t1/math.Pi.toFloat*(1.f + math.cos(math.Pi/t1*(t-t2)).toFloat)
    else ret = 2*t1/math.Pi.toFloat
    ret *= math.tan(maxEuler).toFloat*9.8f
    if(neg) -ret else ret
  }
  def dcurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
    var ret=0.f
    if(t < t1) ret = t1/math.Pi.toFloat*(t - t1/math.Pi.toFloat*math.sin(math.Pi/t1*t).toFloat)  
    else if( t > t2) ret = t1/math.Pi.toFloat*(t + t1/math.Pi.toFloat*math.sin(math.Pi/t1*(t-t2)).toFloat) + t1/math.Pi.toFloat*(t2-t1)
    else ret = t1/math.Pi.toFloat*(2*t - t1)
    ret *= math.tan(maxEuler).toFloat*9.8f
    if(neg) -ret else ret
  }

  def solver(d:Float):(Float,Float) = {
  	var t1 = 0.f
  	var t2 = 0.f
  	val maxA = math.tan(maxEuler).toFloat*9.8f

		if( d/moveSpeed < moveSpeed*math.Pi.toFloat/(2*maxA)){
			t1 = math.sqrt(d*math.Pi/(2*maxA)).toFloat
			t2 = t1
		}else{
	    t1 = moveSpeed*math.Pi.toFloat/(2*math.tan(maxEuler).toFloat*9.8f)  // ---> d = 2*t1*t2*a/pi, vmax = 2*t1*a/pi
	    t2 = d / moveSpeed
		}
    (t1,t2)
  }

  //
  // STEP 2.0
  //
  def moveStep2(x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float){

    if( !flying || !navigating ){ return } //navmove(0.f,0.f,0.f,0.f); return }

    // calculate time since last step
    val t1 = System.currentTimeMillis()
    val dt = (t1 - t0) / 1000.f
    t0 = t1
    //println("dt: "+dt)

    // current pose and velocity
    val p = Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz))
    val v = (p.pos - pose.pos) / dt

    // update tracked state
    val a = (v - vel) / dt
    jerk = (a - accel) / dt
    accel.set(a)
    vel.set(v)
    pose.set(p)

    val e = Vec3()
    e.set( pose.quat.toEuler )
    angVel = (e - euler) / dt
    euler.set(e)

    // calculate distance to dest and trajectory
    val dir = dest.pos - pose.pos
    val dist = dir.mag
    if( dist > posThresh){
      
      for( i <- (0 until 3)){

        if( i == 1){ // use simple method for y direction
          expected_a(i) = dir.normalize().y

        } else if( math.abs(dir(i)) < posThresh){    // if close enough in x or z stop moving
          t(i) = 0.f
          expected_a(i) = 0.f
          //this.hover()

        } else if( t(i) == 0.f ){ //calculate new trajectory
          d0(i) = pose.pos(i)
          if( dir(i) < 0.f) neg(i) = true
          else neg(i) = false
          val times = solver(math.abs(dir(i)))
          time1(i) = times._1
          time2(i) = times._2
          println( "new trajectory!!!!!!!!!!!! " + i + " " + times._1 + " " + times._2 )
          t(i) += dt
        }else{
          val d = dcurve(time1(i),time2(i),neg(i))(t(i))
          val dd = vcurve(time1(i),time2(i),neg(i))(t(i))
          expected_v(i) = dd
          val ddd = acurve(time1(i),time2(i),neg(i))(t(i))
          //println( ddd )
          expected_a(i) = kp(i)*((d+d0(i))-pose.pos(i)) + kd(i)*(dd-v(i)) + kdd(i)*(ddd-a(i))
          t(i) += dt
          if(t(i) > (time2(i) + time1(i))){
            t(i) = 0.f
            expected_a(i) = 0.f
            navigating = false
            //move(0.f,0.f,0.f,0.f)
            //this.hover()
          }
        }

      }
      val cos = math.cos(euler.y).toFloat
      val sin = math.sin(euler.y).toFloat
      control.x = math.atan((expected_a.x*cos - expected_a.z*sin) / (expected_a.y+9.8f)).toFloat
      control.y = math.atan((expected_a.x*sin + expected_a.z*cos) / (expected_a.y+9.8f)).toFloat
      control.z = expected_a.y * vmoveSpeed; //expected_a.y

      //recalculate control x y tilts as percentage of maxEuler angle
      control.x = control.x / maxEuler
      control.y = control.y / maxEuler

      // limit or desaturate control params
      if( control.x > 1.f || control.x < -1.f){ control.x /= math.abs(control.x); control.y /= math.abs(control.x) }
      if( control.y > 1.f || control.y < -1.f){ control.y /= math.abs(control.y); control.x /= math.abs(control.y) }
      if( control.z > 1.f) control.z = 1.f
      else if( control.z < -1.f) control.z = -1.f
      if( rot > 1.f) rot = 1.f
      else if( rot < -1.f) rot = -1.f

      navmove(control.x,control.y,control.z,rot)
    } //else { navmove(0.f,0.f,0.f,0.f)}
  }

	// step 1
	def moveStep(x:Float,y:Float,z:Float,w:Float ){
    if( !flying || !navigating ) return
    var tilt = Vec3(0.f)
    var (ud,r) = (0.f,0.f)
    var hover = false
    var switchRot = false

    var p = Vec3(x,y,z)
    tVel = p - tPos

    tPos.set(p) //check this
    yaw = w; while( yaw < -180.f ) yaw += 360.f; while( yaw > 180.f) yaw -= 360.f

    //if look always look where it's going
    //if(look) destYaw = math.atan2(dest.pos.z - pos.z, dest.pos.x - tPos.x).toFloat.toDegrees + 90.f
    //else if( lookingAt != null ) destYaw = math.atan2(lookingAt.z - tPos.z, lookingAt.x - tPos.x).toFloat.toDegrees + 90.f
    while( destYaw < -180.f ) destYaw += 360.f
    while( destYaw > 180.f ) destYaw -= 360.f

    var dw = destYaw - yaw
    if( dw > 180.f ) dw -= 360.f 
    if( dw < -180.f ) dw += 360.f 
    if( math.abs(dw) > yawThresh ){ 
      hover = false
      if( !switchRot ) r = -rotSpeed else r = rotSpeed
      if( dw < 0.f) r *= -1.f
      if( smooth ) r *= dw / 180.f
      //drone.move(0,0,0,r)
      //return
    }

    val dir = (dest.pos - (tPos+tVel))
    val dp = dir.mag
    if( dp  > posThresh ){
      hover = false
      val cos = math.cos(w.toRadians)
      val sin = math.sin(w.toRadians)
      val d = (dest.pos - tPos).normalize
      ud = d.y * vmoveSpeed

      //assumes drone oriented 0 degrees looking down negative z axis, positive x axis to its right
      tilt.y = (d.x*sin + d.z*cos).toFloat * moveSpeed //forward backward tilt
      tilt.x = (d.x*cos - d.z*sin).toFloat * moveSpeed //left right tilt
      if( smooth ) {
        tilt *= dp
        if( tilt.x > 1.f || tilt.y > 1.f) tilt = tilt.normalize       
      }
      
    }//else nextWaypoint

    if(hover) this.hover()
    //else if(rotFirst && r != 0.f) drone.move(0,0,0,r)
    else navmove(tilt.x,tilt.y,ud,r)      
  }
}