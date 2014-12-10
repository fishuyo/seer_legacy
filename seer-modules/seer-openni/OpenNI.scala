
package com.fishuyo.seer 
package openni

import graphics._
import spatial._
import util._
import actor._

// import scala.collection.JavaConversions._
// import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import java.nio._

import org.openni._
import org.openni.SkeletonJoint._

// import com.primesense.nite._

import akka.actor._
import akka.event.Logging

object OpenNI {

  var connected = false
  var depth, rgb, tracking = false
	var context:Context = _

  var depthGen:DepthGenerator = _
  var depthMD:DepthMetaData = _
  var imageGen:ImageGenerator = _
	var imageMD:ImageMetaData = _

	var userGen:UserGenerator = _
	var skeletonCap:SkeletonCapability = _
	var poseDetectionCap:PoseDetectionCapability = _

  // val tracking = HashMap[Int,Boolean]()
  
  val colors = RGB(1,0,0) :: RGB(0,1,0) :: RGB(0,0,1) :: RGB(1,1,0) :: RGB(0,1,1) :: RGB(1,0,1) :: RGB(1,1,1) :: List()
  val skeletons = HashMap[Int,Skeleton]()

  val actor = System().actorOf( Props[OpenNIActor], name="openni" )

	def connect(){
    if(connected) return
		try{
			context = new Context
			// context.startGeneratingAll()
      connected = true
		} catch { case e:Exception => println(e)}
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
    if(!connected) connect()
    depthGen = DepthGenerator.create(context)
    depthMD = depthGen.getMetaData()
    depth = true
  }
  def initRGB(){
    if(!connected) connect()
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
    if(!connected) connect()
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
    while(depth.remaining() > 0)
    {
      val depthVal = depth.get();
      if (depthVal != 0)
      {
        histogram(depthVal) += 1
        points += 1
      }
    }
        
    for (i <- 1 until histogram.length)
    {
      histogram(i) += histogram(i-1);
    }

    if (points > 0)
    {
      for (i <- 1 until histogram.length)
      {
        histogram(i) = 1.0f - (histogram(i) / points.toFloat)
      }
    }
  }

  val imgbytes = Array.fill(640*480*4)(255.toByte)
  val rgbbytes = Array.fill(640*480*4)(255.toByte)
  val maskbytes = Array.fill(640*480)(0.toByte)
  def updateDepth(){
  	if( context == null) return
    try {
      context.waitNoneUpdateAll();

      val depthMD = depthGen.getMetaData();
      val imageMD = imageGen.getMetaData();
      val sceneMD = userGen.getUserPixels(0);

      val scene = sceneMD.getData().createShortBuffer();
      val image = imageMD.getData().createByteBuffer();
      val depth = depthMD.getData().createShortBuffer();
      calcHist(depth);
      depth.rewind();
        
      while(depth.remaining() > 0){
        val pos = depth.position();
        val pixel = depth.get();
        val user = scene.get();
            
        imgbytes(4*pos) = 0;
        imgbytes(4*pos+1) = 0;
        imgbytes(4*pos+2) = 0;
        imgbytes(4*pos+3) = 0;

        rgbbytes(4*pos) = image.get()
    		rgbbytes(4*pos+1) = image.get()
        rgbbytes(4*pos+2) = image.get()                  
    		rgbbytes(4*pos+3) = 255.toByte 

    		maskbytes(pos) = if(user != 0) 1.toByte else 0               	

    		val drawBackground = false
        if (drawBackground || pixel != 0){
        	var c = user % (colors.length-1);
        	if (user == 0)
        	{
        		c = colors.length-1;
        	}
        	if (pixel != 0)
        	{
        		val histValue = histogram(pixel);
        		imgbytes(4*pos) = (colors(c).r * histValue*255).toByte 
        		imgbytes(4*pos+1) = (colors(c).g * histValue*255).toByte
            imgbytes(4*pos+2) = (colors(c).b * histValue*255).toByte
        		imgbytes(4*pos+3) = 255.toByte
        	}
        }
      }
    } catch { case e:Exception => e.printStackTrace(); }
  }

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
    val jpos = skeletonCap.getSkeletonJointPosition(user, String2Joint(joint))
    val p = jpos.getPosition
    val x = -p.getX / 1000f
    val y = p.getY / 1000f + 1f
    val z = -p.getZ / 1000f
    val v = Vec3(x,y,z)
    skeletons(user).updateJoint(joint,v)
    (v, jpos.getConfidence )
  }

}

class OpenNIActor extends Actor with ActorLogging {
  var running = false
  def receive = {
    case "start" => running = true; self ! "update"
    case "update" => if(running){ OpenNI.updateDepth(); self ! "update" }
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

object String2Joint {
  def apply(s:String) = s match {
    case "head" => HEAD
    case "neck" => NECK
    case "torso" => TORSO
    case "waist" => WAIST
    case "l_collar" => LEFT_COLLAR
    case "l_shoulder" => LEFT_SHOULDER
    case "l_elbow" => LEFT_ELBOW
    case "l_wrist" => LEFT_WRIST
    case "l_hand" => LEFT_HAND
    case "l_fingers" => LEFT_FINGER_TIP
    case "r_collar" => RIGHT_COLLAR
    case "r_shoulder" => RIGHT_SHOULDER
    case "r_elbow" => RIGHT_ELBOW
    case "r_wrist" => RIGHT_WRIST
    case "r_hand" => RIGHT_HAND
    case "r_fingers" => RIGHT_FINGER_TIP
    case "l_hip" => LEFT_HIP
    case "l_knee" => LEFT_KNEE
    case "l_ankle" => LEFT_ANKLE
    case "l_foot" => LEFT_FOOT
    case "r_hip" => RIGHT_HIP
    case "r_knee" => RIGHT_KNEE
    case "r_ankle" => RIGHT_ANKLE
    case "r_foot" => RIGHT_FOOT
    case _ => TORSO
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
