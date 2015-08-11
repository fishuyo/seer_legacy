
package com.fishuyo.seer 
package openni

import graphics._
import spatial._
import util._
import actor._

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer
import scala.collection.mutable.ArrayBuffer

import java.nio._

import org.openni._
import org.openni.SkeletonJoint._

// import com.primesense.nite._

import akka.actor._
import akka.event.Logging

object OpenNI {

  val (w,h) = (640, 480)
  var connected = false
  var depth, rgb, tracking, pointCloud = false
	var context:Context = _

  var depthGen:DepthGenerator = _
  var depthMD:DepthMetaData = _
  var imageGen:ImageGenerator = _
	var imageMD:ImageMetaData = _

	var userGen:UserGenerator = _
	var skeletonCap:SkeletonCapability = _
	var poseDetectionCap:PoseDetectionCapability = _

  var debugImage = Image(w,h,4,1)
  var depthImage = Image(w,h,1,2)
  var rgbImage = Image(w,h,3,1)

  val depthBytes = Array.fill(w*h*4)(255.toByte)
  // val rgbbytes = Array.fill(w*h*4)(255.toByte)
  val maskBytes = Array.fill(w*h*4)(0.toByte)
  val maskBytes1 = Array.fill(w*h*4)(0.toByte)
  val maskBytes2 = Array.fill(w*h*4)(0.toByte)
  val maskBytes3 = Array.fill(w*h*4)(0.toByte)
  val maskBytes4 = Array.fill(w*h*4)(0.toByte)

  var depthBuffer:ShortBuffer = _ 
  var sceneBuffer:ShortBuffer = _ 
  var rgbBuffer:ByteBuffer = _ 

  val meshBuffer = new Mesh() 
  val pointMesh = new Mesh()
  pointMesh.maxVertices = w*h
  pointMesh.primitive = Points

  val pointBuffer = ArrayBuffer[Point3D]()
  var rem = 0
  var pointCloudDensity = 4

  // val tracking = HashMap[Int,Boolean]()
  
  val colors = RGB(1,0,0) :: RGB(0,1,0) :: RGB(0,0,1) :: RGB(1,1,0) :: RGB(0,1,1) :: RGB(1,0,1) :: RGB(1,1,1) :: List()
  val skeletons = HashMap[Int,Skeleton]()

  val actor = System().actorOf( Props[OpenNIActor], name="openni" )

	def connect():Boolean = {
    if(connected) return true
		try{
			context = new Context
			// context.startGeneratingAll()
      connected = true
      return true
		} catch { case e:Exception => println(e)}
    return false
	}

	def disconnect(){
		// userGen.getNewUserEvent().deleteObservers
		// userGen.getLostUserEvent().deleteObservers
		// skeletonCap.getCalibrationCompleteEvent().deleteObservers
		if(context != null){ 
      context.stopGeneratingAll()
      context.release
      context = null
    }
    connected = false
	}

  def initDepth(){
    if(!connect()) return
    depthGen = DepthGenerator.create(context)
    depthMD = depthGen.getMetaData()
    depth = true
  }
  def initRGB(){
    if(!connect()) return
    imageGen = ImageGenerator.create(context)
    imageMD = imageGen.getMetaData()
    rgb = true
  }
  def alignDepthToRGB(){
    if(!(rgb && depth)) return
    val transform = new AlternativeViewpointCapability(depthGen)
    // depthGen.GetAlternativeViewPointCap().SetViewPoint(imageGen);
    transform.setViewpoint(imageGen)
  }

  def initTracking(){
    if(!depth) initDepth()
    userGen = UserGenerator.create(context)
    skeletonCap = userGen.getSkeletonCapability()
    poseDetectionCap = userGen.getPoseDetectionCapability()

    userGen.getNewUserEvent().addObserver(new NewUserObserver())
    userGen.getLostUserEvent().addObserver(new LostUserObserver())
    skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationObserver());
    skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
    tracking = true
  }

  def initAll(){
    initDepth()
    initRGB()
    initTracking()
  }

  def start() = if(connected){
    context.startGeneratingAll()
    actor ! "start"
  }

  def stop() = if(connected){ 
    context.stopGeneratingAll()
    actor ! "stop"
  }

  val histogram = new Array[Float](10000)
  def calcHist(depth:ShortBuffer){
    // reset
    for (i <- 0 until histogram.length)
      histogram(i) = 0
        
    depth.rewind()

    var points = 0;
    while(depth.remaining() > 0){
      val depthVal = depth.get();
      if (depthVal != 0){
        histogram(depthVal) += 1
        points += 1
      }
    }
        
    for (i <- 1 until histogram.length){
      histogram(i) += histogram(i-1);
    }

    if (points > 0){
      for (i <- 1 until histogram.length){
        histogram(i) = 1.0f - (histogram(i) / points.toFloat)
      }
    }
  }


  def update(){

  	if( context == null) return
    try {
      context.waitNoneUpdateAll();

      if(rgb){
        val imageMD = imageGen.getMetaData();
        rgbBuffer = imageMD.getData().createByteBuffer();
        rgbImage.buffer = rgbBuffer.duplicate
        // rgbImage.buffer.rewind()
        // rgbImage.buffer.put(rgbBuffer)
        // rgbImage.buffer.rewind()
      }

      if(depth){

        val depthMD = depthGen.getMetaData();
        val sceneMD = userGen.getUserPixels(0);
        sceneBuffer = sceneMD.getData().createShortBuffer();
        depthBuffer = depthMD.getData().createShortBuffer();

        // if(depthImage){
          calcHist(depthBuffer);
          depthBuffer.rewind();
        // }
          
        // meshBuffer.clear
        pointBuffer.clear

        while(depthBuffer.remaining() > 0){
          val pos = depthBuffer.position();
          val z = depthBuffer.get();
          val user = sceneBuffer.get();
          
          for( o <- 0 until 4){    
        		maskBytes(4*pos+o) = user.toByte //if(user != 0) 1.toByte else 0               	
            maskBytes1(4*pos+o) = 0
            maskBytes2(4*pos+o) = 0
            maskBytes3(4*pos+o) = 0
            maskBytes4(4*pos+o) = 0

            user match {
              case 1 => maskBytes1(4*pos+o) = 255.toByte
              case 2 => maskBytes2(4*pos+o) = 255.toByte
              case 3 => maskBytes3(4*pos+o) = 255.toByte
              case 4 => maskBytes4(4*pos+o) = 255.toByte
              case _ => ()
            }
          }

        	var c = RGB.white
        	if (user > 0) c = RGB(0,1,0)
        	if (z != 0){
        		val histValue = histogram(z);
        		depthBytes(4*pos) = (c.r * histValue*255).toByte 
        		depthBytes(4*pos+1) = (c.g * histValue*255).toByte
            depthBytes(4*pos+2) = (c.b * histValue*255).toByte
            depthBytes(4*pos+3) = 255.toByte
        	} else{
            depthBytes(4*pos) = 0.toByte 
            depthBytes(4*pos+1) = 0.toByte
            depthBytes(4*pos+2) = 0.toByte
            depthBytes(4*pos+3) = 0.toByte
          }

          if(pointCloud){
            val y = pos / w
            val x = pos % w
            if (z != 0 && user > 0 && x%pointCloudDensity==rem && y%pointCloudDensity==rem){
              pointBuffer += new Point3D(x, y, z)
              // val p = depthGen.convertProjectiveToRealWorld(new Point3D(x, y, z));
              // meshBuffer.vertices += Vec3(p.getX(), p.getY(), p.getZ()) / 1000
            }
          }
        }
        if(pointCloud){
          // pointMesh.clear 
          // pointMesh.vertices ++= meshBuffer.vertices
          // pointMesh.clear
          val ps = depthGen.convertProjectiveToRealWorld(pointBuffer.toArray)
          val vs = ps.map { case p => Vec3(p.getX(), p.getY(), -p.getZ()) / 1000f }
          pointMesh.clear
          pointMesh.vertices ++= vs
          // rem = (rem+2) % 4
        }

      }
    } catch { case e:Exception => e.printStackTrace(); }
  }

  // def updatePoints(){
  //   if(!depth) return

  //   depthBuffer.rewind();
  //   sceneBuffer.rewind();

  //   // pointMesh.clear
  //   pointBuffer.clear

  //   while(depthBuffer.remaining() > 0){
  //     val pos = depthBuffer.position();
  //     val z = depthBuffer.get();
  //     val user = sceneBuffer.get();
          
  //     val y = pos / w
  //     val x = pos % w
  //     if (z != 0 && user > 0 && x%pointCloudDensity==0 && y%pointCloudDensity==0){
  //       pointBuffer += new Point3D(x, y, z)
  //       // val p = depthGen.convertProjectiveToRealWorld(new Point3D(x, y, z));
  //       // pointMesh.vertices += Vec3(p.getX(), p.getY(), p.getZ()) / 1000
  //     }
  //   }
  //   pointMesh.clear
  //   val ps = depthGen.convertProjectiveToRealWorld(pointBuffer.toArray)
  //   pointMesh.vertices ++= ps.map { case p => Vec3(p.getX(), p.getY(), p.getZ()) / 1000f }
  // }

  def getSkeleton(id:Int) = skeletons.getOrElseUpdate(id, new Skeleton(id))

  def getJoints(user:Int){
    getJoint(user,"head")
    getJoint(user,"neck")
    getJoint(user,"torso")
    getJoint(user,"l_shoulder")
    getJoint(user,"l_elbow")
    getJoint(user,"l_hand")
    getJoint(user,"r_shoulder")
    getJoint(user,"r_elbow")
    getJoint(user,"r_hand")
    getJoint(user,"l_hip")
    getJoint(user,"l_knee")
    getJoint(user,"l_foot")
    getJoint(user,"r_hip")
    getJoint(user,"r_knee")
    getJoint(user,"r_foot")
  }

  def getJoint(user:Int, joint:String) = {
    val jpos = skeletonCap.getSkeletonJointPosition(user, Joint(joint))
    val p = jpos.getPosition
    val x = p.getX / 1000f
    val y = p.getY / 1000f //+ 1f
    val z = p.getZ / 1000f
    val v = Vec3(x,y,-z)
    skeletons(user).updateJoint(joint,v)
    (v, jpos.getConfidence )
  }

}

class OpenNIActor extends Actor with ActorLogging {
  var running = false
  def receive = {
    case "start" => running = true; self ! "update"
    case "update" => if(running){ OpenNI.update(); self ! "update" }
    case "stop" => running = false;
    case _ => ()
  }
     
  override def preStart() = {
    log.debug("OpenNI actor Starting")
  }
  override def preRestart(reason: Throwable, message: Option[Any]) {
    log.error(reason, "OpenNI actor Restarting due to [{}] when processing [{}]",
      reason.getMessage, message.getOrElse(""))
  }
}


class NewUserObserver extends IObserver[UserEventArgs]{
	override def update( observable:IObservable[UserEventArgs], args:UserEventArgs){
		val sk = OpenNI.skeletonCap
    val id = args.getId
		println("New user " + id + " pose: " + sk.needPoseForCalibration() );
		sk.requestSkeletonCalibration(id, true);
    OpenNI.getSkeleton(id).calibrating = true
	}
}

class LostUserObserver extends IObserver[UserEventArgs]{
	override def update( observable:IObservable[UserEventArgs], args:UserEventArgs){
    val id = args.getId
		println("Lost user " + id);
    // OpenNI.tracking(id) = false
    OpenNI.getSkeleton(id).tracking = false
    OpenNI.getSkeleton(id).calibrating = false
	}
}

class CalibrationObserver extends IObserver[CalibrationProgressEventArgs]{
	override def update( observable:IObservable[CalibrationProgressEventArgs], args:CalibrationProgressEventArgs){
		println("Calibration complete " + args.getStatus());

		if (args.getStatus() == CalibrationProgressStatus.OK){
      val id = args.getUser
			println("starting tracking "  + id);
			OpenNI.skeletonCap.startTracking(id);
      OpenNI.getSkeleton(id).calibrating = false
      // OpenNI.skeletons(id).randomizeIndices
      OpenNI.getSkeleton(id).tracking = true

      // OpenNI.tracking(id) = true
		} else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT){
			OpenNI.skeletonCap.requestSkeletonCalibration(args.getUser(), true);
		}
	}
}
