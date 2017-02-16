
package com.fishuyo.seer
package io

import spatial._
import spatial._

import collection.mutable.ListBuffer
import collection.mutable.Map

import vrpn._


object VRPN {
	type Callback = (Pose)=>Unit

	var ip = "192.168.0.100"

	val trackers = Map[String,TrackerRemote]()

	def clear(){ trackers.values.foreach(_.stopRunning); trackers.clear }

	def bind(rigid:String, f:Callback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addPositionChangeListener( new PoseListener(f))
	}
	def bindVel(rigid:String, f:Callback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addVelocityChangeListener( new VelListener(f))
	}
	def bindAccel(rigid:String, f:Callback){
		val t = trackers.getOrElseUpdate(rigid,new TrackerRemote(rigid+"@"+ip,null,null,null,null))
		t.addAccelerationChangeListener( new AccListener(f))
	}
}

import TrackerRemote._
class PoseListener(f:VRPN.Callback) extends PositionChangeListener {
	override def trackerPositionUpdate(u:TrackerRemote#TrackerUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.pos(0),u.pos(1),u.pos(2)),Quat(u.quat(3),u.quat(0),u.quat(1),u.quat(2)))
		f(p)
	}
}
class VelListener(f:VRPN.Callback) extends VelocityChangeListener {
	override def trackerVelocityUpdate(u:TrackerRemote#VelocityUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.vel(0),u.vel(1),u.vel(2)),Quat(u.vel_quat(3),u.vel_quat(0),u.vel_quat(1),u.vel_quat(2)))
		f(p)
	}
}
class AccListener(f:VRPN.Callback) extends AccelerationChangeListener {
	override def trackerAccelerationUpdate(u:TrackerRemote#AccelerationUpdate, r:TrackerRemote){
		val p = Pose(Vec3(u.acc(0),u.acc(1),u.acc(2)),Quat(u.acc_quat(3),u.acc_quat(0),u.acc_quat(1),u.acc_quat(2)))
		f(p)
	}
}
