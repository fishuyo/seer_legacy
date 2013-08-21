
package com.fishuyo
package graphics

import maths._
import spatial._

import collection.immutable.Stack

import com.badlogic.gdx.math.Matrix4

object MatrixStack {

	var stack = new Stack[Matrix4]()
	var model = new Matrix4()
	var projModelView = new Matrix4()
  var modelView = new Matrix4()

	def push(){ stack = stack.push(new Matrix4(model)) }
	def pop(){ model = stack.top; stack = stack.pop }

	def translate(p:Vec3){
		val m = new Matrix4().translate(p.x,p.y,p.z)
		Matrix4.mul(model.`val`, m.`val`)	
	}
	def rotate(q:Quat){
		val m = new Matrix4().rotate(q.toQuaternion)
		Matrix4.mul(model.`val`, m.`val`)	
	}
	def scale( s:Vec3 ){
		val m = new Matrix4().scale(s.x,s.y,s.z)
		Matrix4.mul(model.`val`, m.`val`)	
	}

	def transform(pose:Pose, scale:Vec3=Vec3(1)){
		val m = new Matrix4().translate(pose.pos.x,pose.pos.y,pose.pos.z).rotate(pose.quat.toQuaternion()).scale(scale.x,scale.y,scale.z)
		Matrix4.mul(model.`val`, m.`val`)
	}

	def clear() = { stack = new Stack[Matrix4](); model.idt }
	def setIdentity() = model.idt

	def apply() = {
		projModelView.set(Camera.combined)
  	modelView.set(Camera.view)
  	Matrix4.mul( projModelView.`val`, model.`val`)
  	Matrix4.mul( modelView.`val`, model.`val`)
  	projModelView
	}

}