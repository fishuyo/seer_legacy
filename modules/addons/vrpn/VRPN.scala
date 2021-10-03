
package seer
package io 

import spatial._

import collection.mutable.ListBuffer
import collection.mutable.Map

import vrpn._


object VRPN {

  type PoseCallback = (Pose)=>Unit
  type AnalogCallback = (Array[Double])=>Unit

	var ip = "localhost" //"192.168.0.100" // use full

	val trackers = Map[String,TrackerRemote]()
	val analogs = Map[String,AnalogRemote]()

	def clear(){ 
    trackers.values.foreach(_.stopRunning)
    trackers.clear
    analogs.values.foreach(_.stopRunning)
    analogs.clear
  }

  // remote takes form "[name]@[ip]"
  def analogListen(remote:String, f:AnalogCallback) = {
    val t = analogs.getOrElseUpdate(remote, new AnalogRemote(remote, null,null,null,null))
    t.addAnalogChangeListener( new AnalogListener(f))
  }

	def bind(rigid:String, f:PoseCallback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addPositionChangeListener( new PoseListener(f))
	}
	def bindVel(rigid:String, f:PoseCallback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addVelocityChangeListener( new VelListener(f))
	}
	def bindAccel(rigid:String, f:PoseCallback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addAccelerationChangeListener( new AccListener(f))
	}
}


// AnalogRemoteListener
import AnalogRemote._ 
class AnalogListener(f:VRPN.AnalogCallback) extends AnalogChangeListener {
  override def analogUpdate(u:AnalogRemote#AnalogUpdate, r:AnalogRemote){
    val values = u.channel
    f(values)
  }
}
// TrackerRemoteListeners
import TrackerRemote._
class PoseListener(f:VRPN.PoseCallback) extends PositionChangeListener {
	override def trackerPositionUpdate(u:TrackerRemote#TrackerUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.pos(0),u.pos(1),u.pos(2)),Quat(u.quat(3),u.quat(0),u.quat(1),u.quat(2)))
		f(p)
	}
}
class VelListener(f:VRPN.PoseCallback) extends VelocityChangeListener {
	override def trackerVelocityUpdate(u:TrackerRemote#VelocityUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.vel(0),u.vel(1),u.vel(2)),Quat(u.vel_quat(3),u.vel_quat(0),u.vel_quat(1),u.vel_quat(2)))
		f(p)
	}
}
class AccListener(f:VRPN.PoseCallback) extends AccelerationChangeListener {
	override def trackerAccelerationUpdate(u:TrackerRemote#AccelerationUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.acc(0),u.acc(1),u.acc(2)),Quat(u.acc_quat(3),u.acc_quat(0),u.acc_quat(1),u.acc_quat(2)))
		f(p)
	}
}
