
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

import scala.collection.mutable.ListBuffer
import scala.collection.mutable.Map


object Script extends SeerScript {


	val s = Cow()
	override def draw(){
		s.draw
	}

	override def animate(dt:Float){
	}

}




// object Bone {
// 	def apply() = new Bone(Vec3(),Quat(),0.f)
// 	def apply(p:Vec3,q:Quat,l:Float) = new Bone(p,q,l)
// }
// class Bone( var pos:Vec3, var quat:Quat, var length:Float)


// class Skeleton(val id:Int){

//   var calibrating = false
//   var tracking = false

//   var joints = Map[String,Vec3]()

//   joints += "head" -> Vec3(0)
//   joints += "neck" -> Vec3(0)
//   joints += "torso" -> Vec3(0)
//   joints += "r_shoulder" -> Vec3(0)
//   joints += "r_elbow" -> Vec3(0)
//   joints += "r_hand" -> Vec3(0)
//   joints += "l_shoulder" -> Vec3(0)
//   joints += "l_elbow" -> Vec3(0)
//   joints += "l_hand" -> Vec3(0)
//   joints += "r_hip" -> Vec3(0)
//   joints += "r_knee" -> Vec3(0)
//   joints += "r_foot" -> Vec3(0)
//   joints += "l_hip" -> Vec3(0)
//   joints += "l_knee" -> Vec3(0)
//   joints += "l_foot" -> Vec3(0)

//   val bones = ListBuffer[Bone]()
//   for( i <- (0 until 8)) bones += Bone()

//   def setJoints(s:Skeleton){
//     joints = s.joints.clone
//   }

//   def updateBones(){
//     bones(0).pos.set(joints("l_shoulder"))
//     var a = joints("l_elbow") - joints("l_shoulder")
//     bones(0).length = a.mag()
//     bones(0).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(1).pos.set(joints("l_elbow"))
//     a = joints("l_hand") - joints("l_elbow")
//     bones(1).length = a.mag()
//     bones(1).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(2).pos.set(joints("r_shoulder"))
//     a = joints("r_elbow") - joints("r_shoulder")
//     bones(2).length = a.mag()
//     bones(2).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(3).pos.set(joints("r_elbow"))
//     a = joints("r_hand") - joints("r_elbow")
//     bones(3).length = a.mag()
//     bones(3).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(4).pos.set(joints("l_hip"))
//     a = joints("l_knee") - joints("l_hip")
//     bones(4).length = a.mag()
//     bones(4).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(5).pos.set(joints("l_knee"))
//     a = joints("l_foot") - joints("l_knee")
//     bones(5).length = a.mag()
//     bones(5).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(6).pos.set(joints("r_hip"))
//     a = joints("r_knee") - joints("r_hip")
//     bones(6).length = a.mag()
//     bones(6).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)

//     bones(7).pos.set(joints("r_knee"))
//     a = joints("r_foot") - joints("r_knee")
//     bones(7).length = a.mag()
//     bones(7).quat = Quat().getRotationTo(Vec3(0,0,1), a.normalized)
// 	}
// }



// class StickMan(override val id:Int) extends Skeleton(id) with Animatable {

//   var color = RGBA(0.f,.7f,.1f,.5f)

//   var calibrating = false
//   var tracking = false

//   val loadingModel = Cube().scale(0.1f).translate(0,0.5f,0)
//   val m = Cube().rotate(45.f.toRadians,0,45.f.toRadians)
//   loadingModel.addPrimitive(m)
//   m.material.color = color
//   loadingModel.material.color = color

//   var jointModels = Map[String,Model]()

//   jointModels += "head" -> Sphere().scale(.05f,.065f,.05f)
//   jointModels += "neck" -> Sphere().scale(.02f)
//   jointModels += "torso" -> Sphere().scale(.07f,.10f,.05f)
//   jointModels += "r_shoulder" -> Sphere().scale(.02f)
//   jointModels += "r_elbow" -> Sphere().scale(.02f)
//   jointModels += "r_hand" -> Sphere().scale(.02f)
//   jointModels += "l_shoulder" -> Sphere().scale(.02f)
//   jointModels += "l_elbow" -> Sphere().scale(.02f)
//   jointModels += "l_hand" -> Sphere().scale(.02f)
//   jointModels += "r_hip" -> Sphere().scale(.03f)
//   jointModels += "r_knee" -> Sphere().scale(.02f)
//   jointModels += "r_foot" -> Sphere().scale(.02f)
//   jointModels += "l_hip" -> Sphere().scale(.03f)
//   jointModels += "l_knee" -> Sphere().scale(.02f)
//   jointModels += "l_foot" -> Sphere().scale(.02f)
  
//   jointModels.values.foreach( (m) => {
//   	m.material = Material.basic
//   	m.material.color = color 
//   })

//   val boneModels = new ListBuffer[Model]()
//   for( i <- (0 until 8)) boneModels += Cylinder()
//   boneModels.foreach( (b) => {
//   	b.material = Material.basic
//   	b.material.color = color
//   	b.scale.set(.015f,.015f,.15f) 
//   })

//   def setShader(s:String){
//     jointModels.values.foreach(_.shader = s)
//     boneModels.foreach(_.shader = s)
//   }

//   override def draw(){
//     if(calibrating) loadingModel.draw()
//     if(tracking){ 
//       jointModels.values.foreach(_.draw())
//       boneModels.foreach(_.draw())
//     }
//   }

//   override def animate(dt:Float){
//     loadingModel.rotate(0,0.10f,0)
//     updateBones()


//   }

//   def setColor(c:RGBA){
//     color = c
//     loadingModel.material.color = c
//     m.material.color = c
//     // joints.values.foreach( _.material.color = color)
//     bones.foreach( _.material.color = color)
//   }
//   def calibrating(v:Boolean){ calibrating = v }
//   def tracking(v:Boolean){ tracking = v }
// }





Script
