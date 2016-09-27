
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

import akka.actor._
import akka.event.Logging

object OpenNI {

  val (w,h) = (640, 480)
  var connected = false
  var depth, rgb, tracking, pointCloud = false
  var pointCloudEach = false
  var flipCamera = false
  var offset = Vec3()
	var context:Context = _

  var depthGen:DepthGenerator = _
  var depthMD:DepthMetaData = _
  var imageGen:ImageGenerator = _
	var imageMD:ImageMetaData = _

	var userGen:UserGenerator = _
	var skeletonCap:SkeletonCapability = _
	var poseDetectionCap:PoseDetectionCapability = _

  var makeDebugImage = false
  var debugImage = Image(w,h,3,1)
  var depthImage = Image(w,h,1,2)
  var userImage = Image(w,h,1,2)
  var userMaskImage = Image(w,h,1,1)
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
  var debugBuffer:ByteBuffer = _ 
  var debugBufferSafe:ByteBuffer = _ 
  var userMaskBufferSafe:ByteBuffer = userMaskImage.buffer.duplicate

  val meshBuffer = new Mesh() 
  val pointMesh = new Mesh()
  pointMesh.maxVertices = w*h
  pointMesh.primitive = Points
  val pointMeshes = ArrayBuffer[Mesh]()

  val pointBuffer = ArrayBuffer[Point3D]()
  val pointBuffers = ArrayBuffer[ArrayBuffer[Point3D]]()
  var rem = 0
  var pointCloudDensity = 4

  for( i <- 0 until 4){
   pointMeshes += new Mesh
   pointMesh.maxVertices = w*h 
   pointMesh.primitive = Points 
   pointBuffers += ArrayBuffer[Point3D]()
  }

  // val tracking = HashMap[Int,Boolean]()
  
  val colors = RGB(1,0,0) :: RGB(0,1,0) :: RGB(0,0,1) :: RGB(1,1,0) :: RGB(0,1,1) :: RGB(1,0,1) :: List()
  val skeletons = HashMap[Int,Skeleton]()
  val users = HashMap[Int,User]()

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
    try{
      depthGen = DepthGenerator.create(context)
      depthMD = depthGen.getMetaData()
      depth = true
    } catch { case e:Exception => println(s"OpenNI.initDepth: $e") }
  }
  def initRGB(){
    if(!connect()) return
    try{
      imageGen = ImageGenerator.create(context)
      imageMD = imageGen.getMetaData()
      rgb = true
    } catch { case e:Exception => println(s"OpenNI.initRGB: $e") }
  }
  def alignDepthToRGB(){
    if(!(rgb && depth)) return
    val transform = new AlternativeViewpointCapability(depthGen)
    // depthGen.GetAlternativeViewPointCap().SetViewPoint(imageGen);
    transform.setViewpoint(imageGen)
  }

  def initTracking(){
    if(!depth) initDepth()
    try {
    userGen = UserGenerator.create(context)
    skeletonCap = userGen.getSkeletonCapability()
    poseDetectionCap = userGen.getPoseDetectionCapability()

    userGen.getNewUserEvent().addObserver(new NewUserObserver())
    userGen.getLostUserEvent().addObserver(new LostUserObserver())
    skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationObserver());
    skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);
    tracking = true
    } catch { case e:Exception => println(s"OpenNI.initTracking: $e") }
  }

  def resetTracking(){
    users.values.foreach { case u =>
      skeletonCap.reset(u.id)
      skeletonCap.startTracking(u.id)
    }
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
        // rgbBuffer = imageMD.getData().createByteBuffer();
        // rgbBuffer = rgbImage.buffer
        imageMD.getData().copyToBuffer(rgbImage.buffer, rgbImage.sizeInBytes )
        // rgbImage.buffer = imageMD.getData().createByteBuffer();
      }

      if(depth){

        val depthMD = depthGen.getMetaData();
        val sceneMD = userGen.getUserPixels(0);

        // sceneBuffer = sceneMD.getData().createShortBuffer();
        sceneMD.getData().copyToBuffer(userImage.buffer, userImage.sizeInBytes)
        sceneBuffer = userImage.buffer.asShortBuffer()

        // depthBuffer = depthMD.getData().createShortBuffer();
        depthMD.getData().copyToBuffer(depthImage.buffer, depthImage.sizeInBytes )
        depthBuffer = depthImage.buffer.asShortBuffer()

        if(makeDebugImage){
          if( debugBuffer == null){
            debugBuffer = ByteBuffer.allocateDirect(640*480*3);
            debugBuffer.order(ByteOrder.nativeOrder());
            debugBufferSafe = debugBuffer.duplicate
          }
          debugBuffer.rewind
          calcHist(depthBuffer);
          depthBuffer.rewind();
        }
          
        // meshBuffer.clear
        pointBuffer.clear
        pointBuffers.foreach( _.clear )

        users.values.foreach { case user =>
          if(user.updateMask){
            // user.maskBufferSafe.rewind
          }
        }
        userMaskBufferSafe.rewind

        while(depthBuffer.remaining() > 0){
          val pos = depthBuffer.position();
          val z = depthBuffer.get();
          val userId = sceneBuffer.get();
          
          if(userId > 0) userMaskBufferSafe.put(255.toByte)
          else userMaskBufferSafe.put(0.toByte)
          
          users.values.foreach { case user =>
            if(user.updateMask){
              // if(user.id == userId.toInt) user.maskBufferSafe.put(255.toByte)
              // else user.maskBufferSafe.put(0.toByte)
            }
          }

          // for( o <- 0 until 4){    
        		// maskBytes(4*pos+o) = userId.toByte //if(userId != 0) 1.toByte else 0               	
          //   maskBytes1(4*pos+o) = 0
          //   maskBytes2(4*pos+o) = 0
          //   maskBytes3(4*pos+o) = 0
          //   maskBytes4(4*pos+o) = 0

          //   userId match {
          //     case 1 => maskBytes1(4*pos+o) = 255.toByte
          //     case 2 => maskBytes2(4*pos+o) = 255.toByte
          //     case 3 => maskBytes3(4*pos+o) = 255.toByte
          //     case 4 => maskBytes4(4*pos+o) = 255.toByte
          //     case _ => ()
          //   }
          // }

          if(makeDebugImage){
            var c = RGB.white
            if (userId > 0) c = colors(userId)
            if (z != 0){
              val b = histogram(z);
              debugBuffer.put(Array[Byte]((c.r*b*255).toByte, (c.g*b*255).toByte, (c.b*b*255).toByte))
            } else{
              debugBuffer.put(Array[Byte](0,0,0))
            }
          }

          if(pointCloud){
            if(pointCloudEach){
              val y = pos / w
              val x = pos % w
              if (z != 0 && userId > 0 && x%pointCloudDensity==rem && y%pointCloudDensity==rem){
                pointBuffers(userId) += new Point3D(x, y, z)
                // val p = depthGen.convertProjectiveToRealWorld(new Point3D(x, y, z));
                // meshBuffer.vertices += Vec3(p.getX(), p.getY(), p.getZ()) / 1000
              }
            } else{
              val y = pos / w
              val x = pos % w
              if (z != 0 && userId > 0 && x%pointCloudDensity==rem && y%pointCloudDensity==rem){
                pointBuffer += new Point3D(x, y, z)
                // val p = depthGen.convertProjectiveToRealWorld(new Point3D(x, y, z));
                // meshBuffer.vertices += Vec3(p.getX(), p.getY(), p.getZ()) / 1000
              }
            }
          }
        }
        if(makeDebugImage){
          // debugImage.buffer.rewind();
          // debugImage.buffer.put(depthBytes)
          debugBuffer.rewind
          debugImage.buffer = debugBufferSafe
        }
        if(pointCloud){
          if(pointCloudEach){
            pointBuffers.zipWithIndex.foreach { case (b,i) =>
              val ps = depthGen.convertProjectiveToRealWorld(b.toArray)
              val vs = ps.map(point3DtoVec3(_))
              pointMeshes(i).clear
              pointMeshes(i).vertices ++= vs
              getUser(i).points = vs
            }            

          } else {
            // pointMesh.clear 
            // pointMesh.vertices ++= meshBuffer.vertices
            // pointMesh.clear
            val ps = depthGen.convertProjectiveToRealWorld(pointBuffer.toArray)
            val vs = ps.map(point3DtoVec3(_))
            pointMesh.clear
            pointMesh.vertices ++= vs
            users.values.foreach( _.points = vs )
            // rem = (rem+2) % 4     
          }
        }

      }

      callbacks.values.foreach{ case f =>  
        val users = getTrackedUsers().toList
        users.foreach{ case u => 
          u.skeleton.updateJoints 
          u.skeleton.updateBones
        }
        f(users)
      }

    } catch { case e:Exception => e.printStackTrace(); }
  }

  def point3DtoVec3(p:Point3D) = {
    if(flipCamera) Vec3(-p.getX(), p.getY(), p.getZ()) / 1000f + offset
    else Vec3(p.getX(), p.getY(), -p.getZ()) / 1000f + offset
  }

  def getSkeleton(id:Int) = skeletons.getOrElseUpdate(id, new Skeleton(id))
  def getUser(id:Int) = users.getOrElseUpdate(id, new User(id))

  def getTrackedSkeleton() = skeletons.filter( _._2.tracking ).head._2
  def getTrackedUsers() = users.filter( _._2.tracking ).values



  def getJoints(user:Int) = for(j <- Joint.strings) yield (j,getJoint(user,j))

  def getJoint(user:Int, joint:String) = {
    val jpos = skeletonCap.getSkeletonJointPosition(user, Joint(joint))
    val v = point3DtoVec3(jpos.getPosition)
    // (v, jpos.getConfidence)
    v
  }

  val callbacks = HashMap[String, PartialFunction[List[User],Unit]]()
  def listen(p:PartialFunction[List[User],Unit])(implicit name:String){
    if(!connected){
      initAll()
      start()
      pointCloud = true
    }
    callbacks(name) = p
  }
  def unlisten(name:String) = callbacks.remove(name)

}


class OpenNIActor extends Actor with ActorLogging {
  var running = false
  def receive = {
    case "start" => if(!running){ running = true; self ! "update" }
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
    OpenNI.getUser(id).tracking = true
	}
}

class LostUserObserver extends IObserver[UserEventArgs]{
	override def update( observable:IObservable[UserEventArgs], args:UserEventArgs){
    val id = args.getId
		println("Lost user " + id);
    // OpenNI.tracking(id) = false
    OpenNI.getSkeleton(id).tracking = false
    OpenNI.getSkeleton(id).calibrating = false
    OpenNI.getUser(id).tracking = false
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
