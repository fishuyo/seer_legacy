
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

import org.openni.{OpenNI => NI}
import org.openni.Point3D
import org.openni._
import com.primesense.nite._

import akka.actor._
import akka.event.Logging

import collection.JavaConverters._

object OpenNI {
  var initd = false
  var device:Option[Device] = None
  var depthStream:Option[VideoStream] = None
  var rgbStream:Option[VideoStream] = None
  var tracker:Option[UserTracker] = None

  /** Initialize openni and nite libraries, open default device */
  def init(){
    if(initd){
      println("OpenNI Warn: already initialized.")
      return
    }

    NI.initialize
    NiTE.initialize
    val devices = NI.enumerateDevices.asScala.toList
    if (devices.size == 0) {
        println("OpenNI Error: No device is connected")
        this.shutdown()
        return;
    } else println(s"OpenNI: ${devices.size} devices connected")
    
    device = Some(Device.open(devices(0).getUri()))
    initd = true
  }

  def startDepth(){
    if(!initd) return
    if(depthStream.isEmpty){
      depthStream = Some(VideoStream.create(device.get, SensorType.DEPTH))
      depthStream.get.addNewFrameListener(FrameListener)
      depthStream.get.start()
    }
  }

  def startColor(){
    if(!initd) return
    if(rgbStream.isEmpty){
      rgbStream = Some(VideoStream.create(device.get, SensorType.COLOR))
      rgbStream.get.addNewFrameListener(FrameListener)
      rgbStream.get.start()
    }
  }

  def startTracking(){
    if(!initd) return
    if(depthStream.isEmpty) depthStream = Some(VideoStream.create(device.get, SensorType.DEPTH))
    if(tracker.isEmpty){
      tracker = Some(UserTracker.create) //(device)
      tracker.get.addNewFrameListener(TrackerListener)
    }
  }

  def setPointCloudThinning(factor:Int){
    var v = factor
    if(v < 1) v = 1
    TrackerListener.thinFactor = v
    PointCloud.thinFactor = v
  }

  val frameCallbacks = ListBuffer[PartialFunction[Frame,Unit]]()
  def onFrame(f:PartialFunction[Frame,Unit]) = frameCallbacks += f
  
  val userCallbacks = ListBuffer[PartialFunction[List[User],Unit]]()
  def onUser(f:PartialFunction[List[User],Unit]) = userCallbacks += f

  def clearCallbacks(){
    frameCallbacks.clear
    userCallbacks.clear
  }

  /** Close openni and nite */
  def shutdown(){
    // tracker.destroy
    // depthStream.destroy
    // device.close
    // NiTE.shutdown
    // NI.shutdown
    // initd = false
  }

}

sealed trait Frame { def image:Image }
case class DepthFrame(image:Image) extends Frame {
  def toPoints() = PointCloud.fromImage(image)
}
case class ColorFrame(image:Image) extends Frame

object FrameListener extends VideoStream.NewFrameListener {
  def onFrameReady(stream:VideoStream){
    val frame = stream.readFrame()
    val data = frame.getData().order(ByteOrder.LITTLE_ENDIAN)

    frame.getVideoMode.getPixelFormat match {
      case PixelFormat.RGB888 =>
        val image = new Image(data, frame.getWidth, frame.getHeight, 3, 1)
        OpenNI.frameCallbacks.foreach(_(ColorFrame(image)))

      case PixelFormat.DEPTH_1_MM =>
        val image = new Image(data, frame.getWidth, frame.getHeight, 1, 2)
        OpenNI.frameCallbacks.foreach(_(DepthFrame(image)))
    }
    frame.release()
  }
}

object PointCloud {
  var thinFactor = 2
  var thinOffset = 0

  def fromImage(depthImage:Image):ArrayBuffer[Vec3] = {
    val w = depthImage.w
    val h = depthImage.h
    val depthData = depthImage.buffer
    fromByteBuffer(depthData, w, h)
  }

  def fromDepthFrameRef(depthFrame:VideoFrameRef):ArrayBuffer[Vec3] = {
    val w = depthFrame.getWidth
    val h = depthFrame.getHeight
    val depthData:ByteBuffer = depthFrame.getData().order(ByteOrder.LITTLE_ENDIAN)
    fromByteBuffer(depthData, w, h)
  }

  def fromByteBuffer(depthData:ByteBuffer, w:Int, h:Int):ArrayBuffer[Vec3] = {
    val buffer = ArrayBuffer[Vec3]()

    var pos = 0
    while(depthData.remaining() > 0) {
      val y = pos / w
      val x = pos % w
      val z = depthData.getShort()

      if(z != 0
        && x % thinFactor == thinOffset
        && y % thinFactor == thinOffset ){
        val p = CoordinateConverter.convertDepthToWorld(OpenNI.depthStream.get, x, y, z)
        buffer += point3DtoVec3(p)
      }
      pos += 1
    }
    buffer
  }

  def point3DtoVec3(p:org.openni.Point3D[java.lang.Float]) = Vec3(-p.getX(),p.getY(),-p.getZ()) /= 1000f
  def point3DtoVec3(p:com.primesense.nite.Point3D[java.lang.Float]) = Vec3(-p.getX(),p.getY(),-p.getZ()) /= 1000f
}

object TrackerListener extends UserTracker.NewFrameListener {
  var thinFactor = 2
  var thinOffset = 0
  val pointBuffers = HashMap[Int,ArrayBuffer[Vec3]]()

  def onNewFrame(tracker:UserTracker){          
    try{
    val frame = tracker.readFrame()
    
    /** Get detected Users and Skeletons */
    val users = frame.getUsers.asScala.map { case user =>
      val id = user.getId
      val u = new User(id) //OpenNI.getUser(id)

      if (user.isNew){
        tracker.startSkeletonTracking(id)
        u.tracking = true
      } else if (user.isLost){
        tracker.stopSkeletonTracking(id)
        u.tracking = false
      } else if(user.isVisible){
        u.tracking = true
      }

      val skel = user.getSkeleton()
      if(skel != null) skel.getState match {
        case SkeletonState.CALIBRATING =>
          u.skeleton.calibrating = true
        case SkeletonState.TRACKED => 
          u.skeleton.tracking = true
          u.skeleton.calibrating = false
          Joint.strings.foreach{ case s => 
            val j = skel.getJoint(Joint(s))
            if(j.getPositionConfidence > 0f){
              val p = j.getPosition
              u.skeleton.updateJoint(s, point3DtoVec3(p))
            }
          }
          u.skeleton.updateBones
        case SkeletonState.NONE =>
        case _ =>
      }
      u
    }

    /** Read Depth data and convert to point clouds for each user */
    var depthFrame:VideoFrameRef = frame.getDepthFrame()
    if (depthFrame != null) {
      val w = depthFrame.getWidth
      val h = depthFrame.getHeight
      val depthData:ByteBuffer = depthFrame.getData().order(ByteOrder.LITTLE_ENDIAN)
      val userMap:ByteBuffer = frame.getUserMap().getPixels().order(ByteOrder.LITTLE_ENDIAN)

      pointBuffers.values.foreach(_.clear) // clear points from existing buffers

      val maskImage = Image(w,h,1,1) // image to hold user mask

      // traverse depthData converting to 3d points for each associated userId
      var pos = 0
      while(depthData.remaining() > 0) {
        val userId = userMap.getShort()
        val buffer = pointBuffers.getOrElseUpdate(userId, ArrayBuffer[Vec3]())
        val y = pos / w
        val x = pos % w
        val z = depthData.getShort()

        maskImage.buffer.put((userId*128).toByte)

        if(z != 0 && userId > 0
          && x % thinFactor == thinOffset
          && y % thinFactor == thinOffset ){
          val p = CoordinateConverter.convertDepthToWorld(OpenNI.depthStream.get, x, y, z)
          buffer += point3DtoVec3(p)
        }
        pos += 1
      }

      maskImage.buffer.rewind 
      
      // assign point buffers to user objects
      users.foreach { case u =>
        val points = pointBuffers.getOrElseUpdate(u.id, ArrayBuffer[Vec3]())
        u.points ++= points
        // u.mask = Some(maskImage)
      }
      depthFrame.release()
    }

    frame.release

    // Call user callback functions (even when users is empty)
    OpenNI.userCallbacks.foreach(_(users.toList))    
    } catch{ case e:Exception => println(e) }
  }

  def point3DtoVec3(p:org.openni.Point3D[java.lang.Float]) = Vec3(-p.getX(),p.getY(),-p.getZ()) /= 1000f
  def point3DtoVec3(p:com.primesense.nite.Point3D[java.lang.Float]) = Vec3(-p.getX(),p.getY(),-p.getZ()) /= 1000f
}

object Histogram {
  val histogram = new Array[Float](10000)

  def calculateHistogram(depth:ByteBuffer){
    for (i <- 0 until histogram.length)
      histogram(i) = 0
        
    depth.rewind()

    var points = 0;
    while(depth.remaining() > 0){
      val depthVal = depth.getShort() & 0xFFFF
      if (depthVal != 0){
        histogram(depthVal) += 1
        points += 1
      }
    }
        
    for (i <- 1 until histogram.length)
      histogram(i) += histogram(i-1)

    if (points > 0){
      for (i <- 1 until histogram.length)
        histogram(i) = 1.0f - (histogram(i) / points.toFloat)
    }
  }
}