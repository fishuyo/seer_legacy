
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
  var device:Device = _
  var depthStream:VideoStream = _
  var tracker:UserTracker = _

  var flipCamera = false
  var mirror = false
  var offset = Vec3()

  var pointCloud = true
  var pointCloudDensity = 4
  var rem = 0
  var points = ArrayBuffer[Vec3]()
  val pointBuffers = ArrayBuffer[ArrayBuffer[Vec3]]()
  for( i <- 0 until 8){
   pointBuffers += ArrayBuffer[Vec3]()
  }

  val skeletons = HashMap[Int,Skeleton]()
  val users = HashMap[Int,User]()

  def getSkeleton(id:Int) = skeletons.getOrElseUpdate(id, new Skeleton(id))
  def getUser(id:Int) = users.getOrElseUpdate(id, new User(id))



  /**
    * initialize openni and nite libraries, open default devoce
    */
  def initAll() = init()
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
    
    device = Device.open(devices(0).getUri())
    depthStream = VideoStream.create(device, SensorType.DEPTH)
    tracker = UserTracker.create //(device)
    tracker.addNewFrameListener(TrackerListener)
    initd = true
  }

  /**
    * Close openni and nite
    */
  def shutdown(){
    tracker.destroy
    depthStream.destroy
    device.close
    NiTE.shutdown
    NI.shutdown
    initd = false
  }

}

// class Device(indx:Int){
//   var device:Device = _
//   var depthStream:VideoStream = _
//   var tracker:UserTracker = _
// }

object TrackerListener extends UserTracker.NewFrameListener {

  var mLastFrame:UserTrackerFrameRef = _

  def onNewFrame(tracker:UserTracker){
    try {
    if (mLastFrame != null) {
      mLastFrame.release()
      mLastFrame = null
    }
        
    mLastFrame = tracker.readFrame()
        
    // check if any new user detected start skeleton tracking
    mLastFrame.getUsers().asScala.foreach { case user =>
      val id = user.getId
      val u = OpenNI.getUser(id)

      if (user.isNew){
        tracker.startSkeletonTracking(id)
        u.tracking = true
      } else if (user.isLost){
        u.tracking = false
      } //else if(user.isVisible)

      val skel = user.getSkeleton()
      skel.getState match { //if(skel != null){
        case SkeletonState.CALIBRATING =>
        case SkeletonState.TRACKED => 
          Joint.strings.foreach{ case s => 
            val j = skel.getJoint(Joint(s))
            if(j.getPositionConfidence > 0f){
              val p = j.getPosition
              u.skeleton.updateJoint(s,point3DtoVec3(p))
            }
          }
          u.skeleton.updateBones
        case SkeletonState.NONE =>
        case _ =>
      }

    }

    var depthFrame:VideoFrameRef = mLastFrame.getDepthFrame()
        
    if (depthFrame != null) {
      val w = depthFrame.getWidth
      val h = depthFrame.getHeight
      val depthData:ByteBuffer = depthFrame.getData().order(ByteOrder.LITTLE_ENDIAN)
      val usersFrame:ByteBuffer = mLastFrame.getUserMap().getPixels().order(ByteOrder.LITTLE_ENDIAN)
    
      // make sure we have enough room
      // if (mDepthPixels == null || mDepthPixels.length < depthFrame.getWidth() * depthFrame.getHeight()) {
        // mDepthPixels = new int[depthFrame.getWidth() * depthFrame.getHeight()];
      // }
    
      // calcHist(depthData)
      // depthData.rewind()

      // OpenNI.points = ArrayBuffer[Vec3]()
      OpenNI.pointBuffers.foreach(_.clear)

      var pos = 0
      while(depthData.remaining() > 0) {
        val userId = usersFrame.getShort()
        val z = depthData.getShort()
        val y = pos / w
        val x = pos % w

        if (z != 0 && userId > 0 && (x % OpenNI.pointCloudDensity == OpenNI.rem) && (y % OpenNI.pointCloudDensity == OpenNI.rem) ){
          val p = CoordinateConverter.convertDepthToWorld(OpenNI.depthStream,x,y,z)
          OpenNI.pointBuffers(userId) += point3DtoVec3(p) //Vec3(p.getX, p.getY, p.getZ)
        }

        pos += 1
      }

      OpenNI.pointBuffers.zipWithIndex.foreach { case (b,i) =>
        OpenNI.getUser(i).points = b.clone
        // OpenNI.points ++= b.clone
      } 
      
      depthFrame.release();
      depthFrame = null;
    }

    } catch { case e:Exception => e.printStackTrace(); }

  }

  def point3DtoVec3(p:org.openni.Point3D[java.lang.Float]) = {
    var v:Vec3 = null
    if(OpenNI.flipCamera) v = Vec3(-p.getX(), p.getY(), p.getZ()) / 1000f + OpenNI.offset
    else v = Vec3(p.getX(), p.getY(), -p.getZ()) / 1000f + OpenNI.offset
    if(OpenNI.mirror) v.x *= -1
    v
  }
  def point3DtoVec3(p:com.primesense.nite.Point3D[java.lang.Float]) = {
    var v:Vec3 = null
    if(OpenNI.flipCamera) v = Vec3(-p.getX(), p.getY(), p.getZ()) / 1000f + OpenNI.offset
    else v = Vec3(p.getX(), p.getY(), -p.getZ()) / 1000f + OpenNI.offset
    if(OpenNI.mirror) v.x *= -1
    v
  }


  val histogram = new Array[Float](10000)
  def calcHist(depth:ByteBuffer){
    // reset
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
        
    for (i <- 1 until histogram.length){
      histogram(i) += histogram(i-1)
    }

    if (points > 0){
      for (i <- 1 until histogram.length){
        histogram(i) = 1.0f - (histogram(i) / points.toFloat)
      }
    }
  }

}