
// package com.fishuyo
// package drone

// import maths.Vec3
// import maths.Quat
// import spatial.Pose
// import com.fishuyo.net.SimpleTelnetClient

// import com.codeminders.ardrone._

// import com.cycling74.max._
// import com.cycling74.jitter._
// import MaxObject._

// import java.net._
// import java.awt.image.BufferedImage

// //import org.apache.log4j._

// import scala.collection.mutable.Queue

// class DroneControl extends MaxObject with NavDataListener with DroneVideoListener {

//   var ip = "192.168.1.1"
//   declareAttribute("ip")

//   // current state flags
//   var (connecting, ready, flying, navigating, goingHome) = (false,false,false,false,false)

//   // javadrone api ARDrone
//   var drone : ARDrone = _

//   // holds last video frame
//   var frame: BufferedImage = _

//   // initial position stored when first received tracking data
//   var homePose:Pose = null


//   // legacy state
//   var pos = Vec3()
//   var yaw = 0.f; var destYaw = 0.f;

//   // drone state measured from tracker
//   var pose = Pose()
//   var vel = Vec3()
//   var accel = Vec3()
//   var jerk = Vec3()
//   var euler = Vec3()  // relates to accel  -->  x'' = g*tan(theta)
//   var angVel = Vec3()       // relates to jerk  --> x''' = g*sec(theta)^2
//   var t0:Long = 0
//   var (t,time1,time2,d0,neg) = (Vec3(),Vec3(),Vec3(),Vec3(),Array[Boolean](false,false,false))
//   var expected_a = Vec3()
//   var (kp,kd,kdd) = (Vec3(1.85,8.55,1.85),Vec3(.75),Vec3(1))

//   // destination / path info
//   var dest = Pose()

//   // drone state internal
//   var gyroAngles = Vec3()
//   var accelerometer = Vec3()

//   var nd:NavData = _

//   // control input (% of maximum)(delta relates to angVel and to jerk)
//   var control = Vec3()
//   var rot = 0.f
//   var maxEuler = .25f  //(0 - .52 rad)
//   var maxVert = 1000   //(200-2000 mm/s)
//   var maxRot = 3.0f    //(.7 - 6.11 rad/s)

//   // dropped tracking frame count
//   var dropped = 0

//   private var mat: JitterMatrix = _
//   private var lookingAt:Vec3 = null

//   var waypoints = new Queue[(Float,Float,Float,Float)]
  
//   // config params
//   var yawThresh = 20.f
//   declareAttribute("yawThresh")
//   var posThresh = .33f
//   declareAttribute("posThresh")
//   var velThresh = .1f
//   declareAttribute("velThresh")
//   var moveSpeed = .1f       //horizontal movement speed default
//   declareAttribute("moveSpeed")
//   var vmoveSpeed = .1f      //vertical movement speed default
//   declareAttribute("vmoveSpeed")
//   var rotSpeed = .9f      //rotation speed default
//   declareAttribute("rotSpeed")
//   var smooth = false
//   declareAttribute("smooth")
//   var rotFirst = false
//   declareAttribute("rotFirst")
//   var look = false
//   declareAttribute("look")
//   var useHover = true
//   declareAttribute("useHover")
//   var patrol = false
//   declareAttribute("patrol")
//   var switchRot = true
//   declareAttribute("switchRot")
//   var emergency = false
//   declareAttribute("emergency")

//   //navdata values
//   var altitude = 0.f
//   declareAttribute("altitude")
//   var battery = 0
//   declareAttribute("battery")

//   var qSize = 0

//   post("DroneControl version 0.4.0")
  

//   def connect(){
//     if( drone != null){
//       post("Drone already connected.")
//       return
//     }else if( connecting ){
//       return
//     }
//     connecting = true
//     val _this = this
//     val thread = new Thread(){
//       override def run(){
//         try {
//           val d = new ARDrone( InetAddress.getByName(ip), 1000, 1000 )
//           post("connecting to ARDrone at " + ip + " ..." )
//           d.connect
//           d.clearEmergencySignal
//           d.waitForReady(3000)
//           post("ARDrone connected and ready!")
//           d.trim
//           d.addImageListener(_this)
//           d.addNavDataListener(_this)
//           drone = d
//           connecting = false
//           ready = true

//         } catch {
//           case e: Exception => post("Drone connection failed."); e.printStackTrace 
//           connecting = false
//         }  
//       }
//     }
//     thread.start
//   }

//   def disconnect(){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     if( flying ) drone.land
//     drone.disconnect
//     Thread.sleep(100)
//     drone = null
//     ready = false
//     post("Drone disconnected.") 
//   }

//   def clearEmergency(){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     drone.clearEmergencySignal
//   }

//   def trim() = drone.trim
  
//   def takeOff(){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     drone.takeOff
//   }
  
//   def land(){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     drone.land
//   }

//   def reboot() = {
//     val thread = new Thread(){
//       override def run(){
//         try{
//           post("Sending reboot...")
//           if( drone != null ) disconnect
//           val c = new SimpleTelnetClient(ip)
//           c.send("reboot")
//           c.disconnect
//           post("Rebooting ARDrone. ")
//         } catch {
//           case e: Exception => post("Reboot failed.") 
//         }
//       }
//     }
//     thread.start
//   }

//   def telnet(command:String) = {
//     val thread = new Thread(){
//       override def run(){
//         post("Sending telnet command: " + command)
//         val c = new SimpleTelnetClient(ip)
//         c.send(command)
//         c.disconnect
//       }
//     }
//     thread.start
//   }

//   def playLed(anim:Int, freq:Float, dur:Int){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     drone.playLED(anim, freq, dur)
//   }

//   def dance(anim:Int, dur:Int){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     drone.playAnimation(anim, dur)
//   }

//   def setSkip( s:Int) { drone.setSkip(s) }
//   def selectCameraMode( mode: Int){
//     var m = mode % 4
//     m match {
//       case 0 => drone.selectVideoChannel( ARDrone.VideoChannel.HORIZONTAL_ONLY )
//       case 1 => drone.selectVideoChannel( ARDrone.VideoChannel.VERTICAL_ONLY )
//       case 2 => drone.selectVideoChannel( ARDrone.VideoChannel.HORIZONTAL_IN_VERTICAL )
//       case 3 => drone.selectVideoChannel( ARDrone.VideoChannel.VERTICAL_IN_HORIZONTAL )
//     }
//   }

//   def toggleFly = {
//     if( flying ) land
//     else takeOff
//     //post( "fly: " + flying )
//   }

//   def move( lr: Float, fb: Float, udv: Float, rv: Float ){
//     if( drone == null){
//       post("Drone not connected.")
//       return
//     }
//     navigating=false
//     drone.move(lr,fb,udv,rv) 
//   }

//   def moveTo( x:Float,y:Float,z:Float,w:Float ){
//     if( goingHome ) return
//     dest = Pose(Vec3(x,y,z),Quat())
//     destYaw = w;
//     while( destYaw < -180.f ) destYaw += 360.f
//     while( destYaw > 180.f ) destYaw -= 360.f
//     navigating = true
//     t.set(0.f)
//     expected_a.set(0.f)
//   }

//   def lookAt( x:Float, y:Float, z:Float){
//     lookingAt = Vec3(x,y,z)
//   }
//   def dontLookAt(){
//     lookingAt = null
//   }

//   def addWaypoint( x:Float,y:Float,z:Float,w:Float ) = {
//     if( waypoints.size > 1000 ) waypoints.clear
//     waypoints.enqueue((x,y,z,w))
//     navigating = true
//   }
//   def nextWaypoint(){
//     if( waypoints.isEmpty || drone == null) return
//     drone.playLED(4,10,1)
//     val (x,y,z,w) = waypoints.dequeue
//     moveTo(x,y,z,w)
//     if( patrol ) waypoints.enqueue((x,y,z,w))
//   }
//   def clearWaypoints() = waypoints.clear

//   def setHome( x:Float,y:Float,z:Float,w:Float ) = homePose = Pose(Vec3(x,y,z),Quat())
//   def goHome() = {
//     // val (x,y,z,w) = (homePose
//     // moveTo(x,y+1.f,z,w)
//     // posThresh = .07f
//     // smooth = true
//     // goingHome = true
//   }
//   def cancelHome() = {
//     posThresh = .33f
//     smooth = false
//     goingHome = false
//   }

//   def hover = { navigating=false; drone.hover }

//   def acurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
//     var ret=0.f
//     if(t < t1) ret = math.sin(math.Pi/t1*t).toFloat
//     else if( t > t2) ret = -math.sin(math.Pi/t1*(t-t2)).toFloat
//     else ret = 0.f
//     ret *= math.tan(maxEuler).toFloat*9.8f
//     if(neg) -ret else ret
//   }
//   def vcurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
//     var ret=0.f
//     if(t < t1) ret = t1/math.Pi.toFloat*(1.f - math.cos(math.Pi/t1*t).toFloat)  
//     else if( t > t2) ret = t1/math.Pi.toFloat*(1.f + math.cos(math.Pi/t1*(t-t2)).toFloat)
//     else ret = 2*t1/math.Pi.toFloat
//     ret *= math.tan(maxEuler).toFloat*9.8f
//     if(neg) -ret else ret
//   }
//   def dcurve(t1:Float,t2:Float,neg:Boolean)(t:Float):Float = {
//     var ret=0.f
//     if(t < t1) ret = t1/math.Pi.toFloat*(t - t1/math.Pi.toFloat*math.sin(math.Pi/t1*t).toFloat)  
//     else if( t > t2) ret = t1/math.Pi.toFloat*(t + t1/math.Pi.toFloat*math.sin(math.Pi/t1*(t-t2)).toFloat) + t1/math.Pi.toFloat*(t2-t1)
//     else ret = t1/math.Pi.toFloat*(2*t - t1)
//     ret *= math.tan(maxEuler).toFloat*9.8f
//     if(neg) -ret else ret
//   }

//   def solver(d:Float):(Float,Float) = {
//     var t1 = moveSpeed*math.Pi.toFloat/(2*math.tan(maxEuler).toFloat*9.8f)  // ---> d = 2*t1*t2*a/pi, vmax = 2*t1*a/pi
//     var t2 = d / moveSpeed
//     if( t2 < t1 ){
//       var tmp = t2
//       t2 = t1
//       t1 = t2
//     }
//     (t1,t2)
//   }

//   // vmax = 2*t1/math.Pi --> solve for t1 based on vmax
//   // d = 2*t1*t2/math.Pi --> solve for t2, if t2 < t1, then ..

//   // step used to update tracker info and calculate next trajectory
//   def step2(x:Float,y:Float,z:Float,qx:Float,qy:Float,qz:Float,qw:Float){

//     if( homePose == null ) homePose = Pose(Vec3(x,y,z),Quat(qw,qz,qy,qz))
//     if( !ready || !flying || !navigating ) return

//     // calculate time since last step
//     val t1 = System.currentTimeMillis()
//     val dt = (t1 - t0) / 1000.f
//     t0 = t1
//     println("dt: "+dt)

//     // current pose and velocity
//     val p = Pose(Vec3(x,y,z),Quat(qw,qx,qy,qz))
//     val v = (p.pos - pose.pos) / dt

//     // if not moving, most likely lost tracking
//     if( v.x == 0.f && v.y == 0.f && v.z == 0.f ){
//       dropped += 1
      
//       if(dropped > 2){
//         println("lost tracking or step size too short!!!") // TODO
//         drone.hover
//         return
//       }
//     }else dropped = 0

//     // update tracked state
//     val a = (v - vel) / dt
//     jerk = (a - accel) / dt
//     accel.set(a)
//     vel.set(v)
//     pose.set(p)

//     val e = Vec3()
//     e.set( pose.quat.toEuler )
//     angVel = (e - euler) / dt
//     euler.set(e)

//     // calculate distance to dest and trajectory
//     val dir = dest.pos - pose.pos
//     val dist = dir.mag
//     if( dist > posThresh){
      
//       for( i <- (0 until 3)){

//         if( i == 1){ // use simple method for y direction
//           expected_a(i) = dir.normalize().y

//         } else if( math.abs(dir(i)) < posThresh){    // if close enough in x or z stop moving
//           t(i) = 0.f
//           expected_a(i) = 0.f

//         } else if( t(i) == 0.f ){ //calculate new trajectory
//           d0(i) = pose.pos(i)
//           if( dir(i) < 0.f) neg(i) = true
//           else neg(i) = false
//           val times = solver(math.abs(dir(i)))
//           time1(i) = times._1
//           time2(i) = times._2
//           println( "new trajectory!!!!!!!!!!!! " + i + " " + times._1 + " " + times._2)
//           t(i) += dt
//         }else{
//           val d = dcurve(time1(i),time2(i),neg(i))(t(i))
//           val dd = vcurve(time1(i),time2(i),neg(i))(t(i))
//           val ddd = acurve(time1(i),time2(i),neg(i))(t(i))
//           expected_a(i) = kp(i)*((d+d0(i))-pose.pos(i)) + kd(i)*(dd-vel(i)) + kdd(i)*(ddd-a(i))
//           t(i) += dt
//           if(t(i) > time2(i) + time1(i)) t(i) = 0.f
//         }

//       }
//       val cos = math.cos(euler.y).toFloat
//       val sin = math.sin(euler.y).toFloat
//       control.x = math.atan((expected_a.x*cos - expected_a.z*sin) / (expected_a.y+9.8f)).toFloat
//       control.y = math.atan((expected_a.x*sin + expected_a.z*cos) / (expected_a.y+9.8f)).toFloat
//       control.z = expected_a.y * vmoveSpeed; //expected_a.y

//       //recalculate control x y tilts as percentage of maxEuler angle
//       control.x = control.x / maxEuler
//       control.y = control.y / maxEuler

//       // limit or desaturate control params
//       if( control.x > 1.f || control.x < -1.f){ control.x /= math.abs(control.x); control.y /= math.abs(control.x) }
//       if( control.y > 1.f || control.y < -1.f){ control.y /= math.abs(control.y); control.x /= math.abs(control.y) }
//       if( control.z > 1.f) control.z = 1.f
//       else if( control.z < -1.f) control.z = -1.f
//       if( rot > 1.f) rot = 1.f
//       else if( rot < -1.f) rot = -1.f

//       drone.move(control.x,control.y,control.z,rot)
//     }
//   }

//   def step(x:Float,y:Float,z:Float,w:Float ){
//     if( homePose == null ) homePose = Pose(Vec3(x,y,z),Quat())
//     if( !ready || !flying || !navigating ) return
//     var tilt = Vec3(0.f)
//     var (ud,r) = (0.f,0.f)
//     var hover = useHover

//     var p = Vec3(x,y,z)
//     vel = p - pos

//     if( vel.x == 0.f && vel.y == 0.f && vel.z == 0.f ){
//       dropped += 1
//       if(dropped > 5){
//         println("lost tracking or step size too short!!!") // TODO
//         drone.hover
//         return
//       }
//     }else dropped = 0

//     pos.set(p) //check this
//     yaw = w; while( yaw < -180.f ) yaw += 360.f; while( yaw > 180.f) yaw -= 360.f

//     //if look always look where it's going
//     if(look) destYaw = math.atan2(dest.pos.z - pos.z, dest.pos.x - pos.x).toFloat.toDegrees + 90.f
//     else if( lookingAt != null ) destYaw = math.atan2(lookingAt.z - pos.z, lookingAt.x - pos.x).toFloat.toDegrees + 90.f
//     while( destYaw < -180.f ) destYaw += 360.f
//     while( destYaw > 180.f ) destYaw -= 360.f

//     var dw = destYaw - yaw
//     if( dw > 180.f ) dw -= 360.f 
//     if( dw < -180.f ) dw += 360.f 
//     if( math.abs(dw) > yawThresh ){ 
//       hover = false
//       if( !switchRot ) r = -rotSpeed else r = rotSpeed
//       if( dw < 0.f) r *= -1.f
//       if( smooth ) r *= dw / 180.f
//       //drone.move(0,0,0,r)
//       //return
//     }

//     val dir = (dest.pos - (pos+vel))
//     val dp = dir.mag
//     if( dp  > posThresh ){
//       hover = false
//       val cos = math.cos(w.toRadians)
//       val sin = math.sin(w.toRadians)
//       val d = (dest.pos - pos).normalize
//       ud = d.y * vmoveSpeed

//       //assumes drone oriented 0 degrees looking down negative z axis, positive x axis to its right
//       tilt.y = (d.x*sin + d.z*cos).toFloat * moveSpeed //forward backward tilt
//       tilt.x = (d.x*cos - d.z*sin).toFloat * moveSpeed //left right tilt
//       if( smooth ) {
//         tilt *= dp
//         if( tilt.x > 1.f || tilt.y > 1.f) tilt = tilt.normalize       
//       }
      
//     } else if( goingHome && vel.mag < .05f ){
//       drone.land
//       posThresh = .33f
//       smooth = false
//       goingHome = false
//       return
//     }else nextWaypoint

//     if(hover) drone.hover
//     else if(rotFirst && r != 0.f) drone.move(0,0,0,r)
//     else drone.move(tilt.x,tilt.y,ud,r)      
//   }

//   def logger( l:String="INFO" ){
//     var v = Level.INFO
//     l.toUpperCase match {
//       case "WARN" => v = Level.WARN
//       case "DEBUG" => v = Level.DEBUG
//       case _ => v = Level.INFO
//     }
//     Logger.getRootLogger.setLevel( v )
//   }

//   def getVersion(){ post("version: " + drone.getDroneVersion() )}

//   def setConfigOption(name:String,value:String){
//     drone.setConfigOption(name,value)
//   }
//   def setMaxEuler(v:Float){
//     maxEuler = v
//     if( v < 0.f ) maxEuler = 0.f
//     else if( v > .6f) maxEuler = .6f
//     setConfigOption("control:euler_angle_max",maxEuler.toString)
//   }
//   def setMaxVertical(v:Float){
//     setConfigOption("control:control_vz_max",v.toString)
//   } 
//   def setMaxRotation(v:Float){
//     setConfigOption("control:control_yaw",v.toString)
//   }

//   def getPos(){ outlet(1, Array[Atom](Atom.newAtom("pos"),Atom.newAtom(pose.pos.x),Atom.newAtom(pose.pos.y),Atom.newAtom(pose.pos.z))) }
//   def getVel(){ outlet(1, Array[Atom](Atom.newAtom("vel"),Atom.newAtom(vel.x),Atom.newAtom(vel.y),Atom.newAtom(vel.z))) }
//   def getAccel(){ outlet(1, Array[Atom](Atom.newAtom("accel"),Atom.newAtom(accel.x),Atom.newAtom(accel.y),Atom.newAtom(accel.z))) }
//   def getJerk(){ outlet(1, Array[Atom](Atom.newAtom("jerk"),Atom.newAtom(jerk.x),Atom.newAtom(jerk.y),Atom.newAtom(jerk.z))) }
//   def getEuler(){ outlet(1, Array[Atom](Atom.newAtom("euler"),Atom.newAtom(euler.x),Atom.newAtom(euler.y),Atom.newAtom(euler.z))) }
//   def getAngVel(){ outlet(1, Array[Atom](Atom.newAtom("angVel"),Atom.newAtom(angVel.x),Atom.newAtom(angVel.y),Atom.newAtom(angVel.z))) }
//   def getGyroAngles(){ outlet(1, Array[Atom](Atom.newAtom("gyroAngles"),Atom.newAtom(gyroAngles.x),Atom.newAtom(gyroAngles.y),Atom.newAtom(gyroAngles.z))) }
//   def getAccelerometer(){ outlet(1, Array[Atom](Atom.newAtom("accelerometer"),Atom.newAtom(accelerometer.x),Atom.newAtom(accelerometer.y),Atom.newAtom(accelerometer.z))) }  

//   def debug(){
//     post( "ready: " + ready)
//     post("flying: " + flying)
//     post("navigating: " + navigating)
//     post("pos: " + pose.pos.x + " " + pose.pos.y + " " + pose.pos.z + " " + euler.y)
//     post("dest: " + dest.pos.x + " " + dest.pos.y + " " + dest.pos.z )
//     post("tracked vel: " + vel.x + " " + vel.y + " " + vel.z)
//     post("tracked accel: " + accel.x + " " + accel.y + " " + accel.z)
//     post("tracked jerk: " + jerk.x + " " + jerk.y + " " + jerk.z)    
//     post("internal vel: " + accelerometer.x + " " + accelerometer.y + " " + accelerometer.z)
//     post("expected_a: " + expected_a.x + " " + expected_a.y + " " + expected_a.z)
//     post("control: " + control.x + " " + control.y + " " + control.z + " " + rot)
//     post("pitch roll yaw: " + gyroAngles.x + " " + gyroAngles.z + " " + gyroAngles.y)
//     post("altitude: " + altitude)
//     post("battery: " + battery)
//     //if( drone != null ) qSize = drone.queueSize
//     post("command queue size: " + qSize)
//     getPos
//     getVel
//     getAccel
//     getJerk
//     getEuler
//     getAngVel
//     getGyroAngles
//     getAccelerometer
//   }

//   def navDataReceived(nd:NavData){
//     flying = nd.isFlying
//     altitude = nd.getAltitude
//     battery = nd.getBattery
//     gyroAngles.x = nd.getPitch
//     gyroAngles.z = nd.getRoll
//     gyroAngles.y = nd.getYaw
//     accelerometer.x = nd.getVx
//     accelerometer.y = nd.getLongitude
//     accelerometer.z = nd.getVz
//     emergency = drone.isEmergencyMode()
//     //qSize = drone.queueSize
//   }

//   def frameReceived(startX:Int, startY:Int, w:Int, h:Int, rgbArray:Array[Int], offset:Int, scansize:Int){
//     if( frame == null ) frame = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)
//     frame.setRGB(startX, startY, w, h, rgbArray, offset, scansize)
//   }

//   //def connectVideo() = drone.connectVideo();
//   override def bang(){
//     if( frame != null ){
//       if( mat == null ){ mat = new JitterMatrix }
//       mat.copyBufferedImage(frame)
//       outlet(0,"jit_matrix",mat.getName())
//     } else post("no frames from drone received yet.")
//   }
//   override def notifyDeleted(){
//     disconnect
//   }

// }