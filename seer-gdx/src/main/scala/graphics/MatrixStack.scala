
package com.fishuyo.seer
package graphics

import spatial._
import spatial._

import collection.immutable.Stack

import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.math.Quaternion

object MatrixStack {

	var stack = new Stack[Matrix4]()
	var model = new Matrix4()
	var projModelView = new Matrix4()
  var modelView = new Matrix4()
  var view = new Matrix4()
  var normal = new Matrix4()

  var worldPose = Pose()
  var worldScale = Vec3(1)

	def push(){ stack = stack.push(new Matrix4(model)) }
	def pop(){ model = stack.top; stack = stack.pop }

  def translate(x:Float,y:Float,z:Float){ translate(Vec3(x,y,z)) }
	def translate(p:Vec3){
		val m = new Matrix4().translate(p.x,p.y,p.z)
		Matrix4.mul(model.`val`, m.`val`)
	}

  def rotate(x:Float, y:Float, z:Float){ rotate(Quat(x,y,z)) }
	def rotate(q:Quat){
  	val quat = new Quaternion(q.x,q.y,q.z,q.w)
		val m = new Matrix4().rotate(quat)
		Matrix4.mul(model.`val`, m.`val`)	
	}

  def scale(s:Float){ scale(Vec3(s)) }
  def scale(x:Float,y:Float,z:Float){ scale(Vec3(x,y,z)) }
	def scale( s:Vec3 ){
		val m = new Matrix4().scale(s.x,s.y,s.z)
		Matrix4.mul(model.`val`, m.`val`)	
	}

	def transform(pose:Pose, scale:Vec3=Vec3(1)){
  	val quat = new Quaternion(pose.quat.x,pose.quat.y,pose.quat.z,pose.quat.w)
		val m = new Matrix4().translate(pose.pos.x,pose.pos.y,pose.pos.z).rotate(quat).scale(scale.x,scale.y,scale.z)
		Matrix4.mul(model.`val`, m.`val`)
	}

	def clear() = { stack = new Stack[Matrix4](); model.idt; transform(worldPose,worldScale) }
	def setIdentity() = model.idt

	def apply(camera:NavCamera=Camera) = {
		projModelView.set(camera.combined)
  	modelView.set(camera.view)
  	view.set(camera.view)
  	Matrix4.mul( projModelView.`val`, model.`val`)
  	Matrix4.mul( modelView.`val`, model.`val`)
  	normal.set(modelView).toNormalMatrix()
	}

	def projectionModelViewMatrix() = projModelView
	def modelViewMatrix() = modelView
	def viewMatrix() = view
	def modelMatrix() = model
	def normalMatrix() = normal

}


object ColorStack {

	var stack = new Stack[HSV]()
	var hsv = HSV(0,1,1)

	def push(){ stack = stack.push(HSV(hsv)) }
	def pop(){ hsv = stack.top; stack = stack.pop }

	def transform(c:HSV){
		hsv *= c
	}

	def clear() = { stack = new Stack[HSV]() }
}