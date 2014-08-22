
package com.fishuyo.seer 
package openni

import graphics._
import spatial._
import util._

// import scala.collection.JavaConversions._
// import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.HashMap
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

import java.nio._

import org.openni._
// import com.primesense.nite._


object OpenNI {

  var connected = false
	var context:Context = _

  var depthGen:DepthGenerator = _
  var depthMD:DepthMetaData = _
  var imageGen:ImageGenerator = _
	var imageMD:ImageMetaData = _

	var userGen:UserGenerator = _
	var skeletonCap:SkeletonCapability = _
	var poseDetectionCap:PoseDetectionCapability = _

  val tracking = HashMap[Int,Boolean]()

  import SkeletonJoint._
  
  val colors = RGB(1,0,0) :: RGB(0,1,0) :: RGB(0,0,1) :: RGB(1,1,0) :: RGB(0,1,1) :: RGB(1,0,1) :: RGB(1,1,1) :: List()
  // val colors = RGB(1,1,1) :: RGB(0.7,0.,0.1) :: RGB(0.,.7,.5) :: RGB(.5,.5,.7) :: RGB(1,1,0) :: RGB(0,1,1) :: RGB(1,0,1) :: RGB(1,1,1) :: List()

  val skeletons = HashMap[Int,TriangleMan]()
  for( i <- 1 to 4 ){ 
    skeletons(i) = new TriangleMan(i)
    skeletons(i).setColor(colors(i))
  }
  // val joints = HashMap[Int,HashMap[String,Vec3]]()
  // val vel = HashMap[Int,HashMap[String,Vec3]]()
  val s2j = HashMap[String,SkeletonJoint](
    "head" -> HEAD,
    "neck" -> NECK,
    "torso" -> TORSO,
    "waist" -> WAIST,
    "lcollar" -> LEFT_COLLAR,
    "lshoulder" -> LEFT_SHOULDER,
    "lelbow" -> LEFT_ELBOW,
    "lwrist" -> LEFT_WRIST,
    "lhand" -> LEFT_HAND,
    "lfingers" -> LEFT_FINGER_TIP,
    "rcollar" -> RIGHT_COLLAR,
    "rshoulder" -> RIGHT_SHOULDER,
    "relbow" -> RIGHT_ELBOW,
    "rwrist" -> RIGHT_WRIST,
    "rhand" -> RIGHT_HAND,
    "rfingers" -> RIGHT_FINGER_TIP,
    "lhip" -> LEFT_HIP,
    "lknee" -> LEFT_KNEE,
    "lankle" -> LEFT_ANKLE,
    "lfoot" -> LEFT_FOOT,
    "rhip" -> RIGHT_HIP,
    "rknee" -> RIGHT_KNEE,
    "rankle" -> RIGHT_ANKLE,
    "rfoot" -> RIGHT_FOOT
  )

	def connect(){
    if(connected) return
		try{
			context = new Context
	 		
      depthGen = DepthGenerator.create(context)
      depthMD = depthGen.getMetaData()

      imageGen = ImageGenerator.create(context)
			imageMD = imageGen.getMetaData()

			val transform = new AlternativeViewpointCapability(depthGen)
			// depthGen.GetAlternativeViewPointCap().SetViewPoint(imageGen);
			transform.setViewpoint(imageGen)

			userGen = UserGenerator.create(context)
			skeletonCap = userGen.getSkeletonCapability()
		  poseDetectionCap = userGen.getPoseDetectionCapability()

			userGen.getNewUserEvent().addObserver(new NewUserObserver())
			userGen.getLostUserEvent().addObserver(new LostUserObserver())
		
			skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationObserver());

			skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
			context.startGeneratingAll()
      connected = true
		} catch { case e:Exception => println(e)}
	}

	def disconnect(){
		// userGen.getNewUserEvent().deleteObservers
		// userGen.getLostUserEvent().deleteObservers
		// skeletonCap.getCalibrationCompleteEvent().deleteObservers
		if(context != null) context.release
    connected = false
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

  def getJoints(user:Int){
    getJoint(user,"head")
    getJoint(user,"neck")
    getJoint(user,"torso")
    getJoint(user,"lshoulder")
    getJoint(user,"lelbow")
    getJoint(user,"lhand")
    getJoint(user,"rshoulder")
    getJoint(user,"relbow")
    getJoint(user,"rhand")
    getJoint(user,"lhip")
    getJoint(user,"lknee")
    getJoint(user,"lfoot")
    getJoint(user,"rhip")
    getJoint(user,"rknee")
    getJoint(user,"rfoot")
  }

  def getJoint(user:Int, joint:String) = {
    val jpos = skeletonCap.getSkeletonJointPosition(user, s2j(joint))
    val p = jpos.getPosition
    val x = -p.getX / 1000.f
    val y = p.getY / 1000.f + 1.f
    val z = -p.getZ / 1000.f
    val v = Vec3(x,y,z)
    skeletons(user).updateJoint(joint,v)
    (v, jpos.getConfidence )
  }

}



class NewUserObserver extends IObserver[UserEventArgs]{
	override def update( observable:IObservable[UserEventArgs], args:UserEventArgs){
		val sk = OpenNI.skeletonCap
    val id = args.getId
		println("New user " + id + " pose: " + sk.needPoseForCalibration() );
		sk.requestSkeletonCalibration(id, true);
    OpenNI.skeletons.getOrElseUpdate(id, new TriangleMan(id)).calibrating = true
	}
}

class LostUserObserver extends IObserver[UserEventArgs]{
	override def update( observable:IObservable[UserEventArgs], args:UserEventArgs){
    val id = args.getId
		println("Lost user " + id);
    OpenNI.tracking(id) = false
    OpenNI.skeletons.getOrElseUpdate(id, new TriangleMan(id)).tracking = false
    OpenNI.skeletons(id).calibrating = false
	}
}

class CalibrationObserver extends IObserver[CalibrationProgressEventArgs]{
	override def update( observable:IObservable[CalibrationProgressEventArgs], args:CalibrationProgressEventArgs){
		println("Calibration complete " + args.getStatus());

		if (args.getStatus() == CalibrationProgressStatus.OK){
      val id = args.getUser
			println("starting tracking "  + id);
			OpenNI.skeletonCap.startTracking(id);
      OpenNI.skeletons.getOrElseUpdate(id, new TriangleMan(id)).calibrating = false
      OpenNI.skeletons(id).randomizeIndices
      OpenNI.skeletons(id).tracking = true

      OpenNI.tracking(id) = true
		} else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT){
			OpenNI.skeletonCap.requestSkeletonCalibration(args.getUser(), true);
		}
	}
}
