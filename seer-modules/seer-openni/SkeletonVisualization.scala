

// package com.fishuyo.seer 
// package openni

// import spatial._
// import graphics._
// import util._

// import scala.collection.mutable.HashMap
// import scala.collection.mutable.Map
// import scala.collection.mutable.ListBuffer


// class SkeletonVisualization( val skeleton:Skeleton ) extends Animatable {
//   var color = RGB(1,1,1)
// }

// class StickMan(override val skeleton:Skeleton) extends SkeletonVisualization(skeleton) {

//   val loadingModel = Cube().scale(0.1f).translate(0,0.5f,0)
//   val m = Cube().rotate(45f.toRadians,0,45f.toRadians)
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
//     m.material = Material.basic
//     m.material.color = color 
//   })

//   val boneModels = new ListBuffer[Model]()
//   for( i <- (0 until 8)) boneModels += Cylinder()
//   boneModels.foreach( (b) => {
//     b.material = Material.basic
//     b.material.color = color
//     b.scale.set(.015f,.015f,.15f) 
//   })

//   // def setShader(s:String){
//   //   jointModels.values.foreach(_.shader = s)
//   //   boneModels.foreach(_.shader = s)
//   // }

//   override def draw(){
//     if(skeleton.calibrating) loadingModel.draw()
//     if(skeleton.tracking){ 
//       jointModels.values.foreach(_.draw())
//       boneModels.foreach(_.draw())
//     }
//   }

//   override def animate(dt:Float){
//     skeleton.droppedFrames += 1
//     if(skeleton.calibrating) loadingModel.rotate(0,0.10f,0)
    
//     if(skeleton.tracking) {
//       skeleton.updateJoints()
//       skeleton.updateBones()

//       jointModels.foreach{ case(name,m) => 
//         m.pose.pos.set( skeleton.joints(name) )
//       }
//       boneModels.zip(skeleton.bones).foreach{ case (m,b) =>
//         m.pose.pos.set( b.pos )
//         m.pose.quat.set( b.quat )
//         m.scale.z = b.length
//       }
//     }
//   }

//   def setColor(c:RGBA){
//     color.set(c)
//     loadingModel.material.color = c
//     m.material.color = c
//     // joints.values.foreach( _.material.color = color)
//     boneModels.foreach( _.material.color = color)
//   }
// }

// // class QuadMan(override val id:Int) extends Skeleton(id) {

// //   var jointModels = Map[String,Model]()
// //   jointModels += "head" -> Plane().scale(.05f,.065f,.05f)
// //   jointModels += "neck" -> Plane().scale(.02f)
// //   jointModels += "torso" -> Plane().scale(.07f,.10f,.05f)
// //   jointModels += "rshoulder" -> Plane().scale(.02f)
// //   jointModels += "relbow" -> Plane().scale(.02f)
// //   jointModels += "rhand" -> Plane().scale(.02f)
// //   jointModels += "lshoulder" -> Plane().scale(.02f)
// //   jointModels += "lelbow" -> Plane().scale(.02f)
// //   jointModels += "lhand" -> Plane().scale(.02f)
// //   jointModels += "rhip" -> Plane().scale(.03f)
// //   jointModels += "rknee" -> Plane().scale(.02f)
// //   jointModels += "rfoot" -> Plane().scale(.02f)
// //   jointModels += "lhip" -> Plane().scale(.03f)
// //   jointModels += "lknee" -> Plane().scale(.02f)
// //   jointModels += "lfoot" -> Plane().scale(.02f)
  
// //   jointModels.values.foreach( (m) => {
// //     m.material = Material.basic
// //     m.material.color = color
// //     m.shader = "s1"
// //   })

// //   val boneModels = new ListBuffer[Model]()
// //   for( i <- (0 until 8)) boneModels += Cylinder() //Plane()
// //   boneModels.foreach( (b) => {
// //     b.material = Material.basic
// //     b.material.color = color
// //     b.shader = "s1"
// //     b.scale.set(.015f,.015f,.15f) 
// //     // b.scale.set(.5f) 
// //   })

// //   // def setShader(s:String){
// //   //   jointModels.values.foreach(_.shader = s)
// //   //   boneModels.foreach(_.shader = s)
// //   // }

// //   override def draw(){
// //     if(tracking){ 
// //       jointModels.values.foreach(_.draw())
// //       boneModels.foreach(_.draw())
// //     }
// //   }

// //   override def animate(dt:Float){
// //     droppedFrames += 1
// //     updateBones()

// //     jointModels.foreach{ case(name,m) => 
// //       m.pose.pos.set( joints(name) )
// //     }    
// //     boneModels.zip(bones).foreach{ case (m,b) =>
// //       m.pose.pos.set( b.pos )
// //       m.pose.quat.set( b.quat )
// //       m.scale.z = b.length
// //     }
// //   }

// //   def setColor(c:RGBA){
// //     color.set(c)
// //     // joints.values.foreach( _.material.color = color)
// //     boneModels.foreach( _.material.color = color)
// //   }
// // }


// // class TriangleMan(override val id:Int) extends Skeleton(id) {

// //   val mesh = new Mesh()
// //   mesh.primitive = Triangles
// //   mesh.maxVertices = 100
// //   mesh.maxIndices = 500

// //   val linemesh = new Mesh()
// //   linemesh.primitive = Lines
// //   linemesh.maxVertices = 100
// //   linemesh.maxIndices = 100
// //   val linemodel = Model(linemesh)
// //   linemodel.shader = "bone"

// //   val lineindices = Array[Int](13,11,11,6,6,12,12,14,11,0,0,2,2,9,6,5,11,5,0,5,5,1,5,10,1,10,1,3,3,7,10,8,8,4)


// //   val model = Model(mesh)  
// //   model.material = Material.specular
// //   model.material.color = color
// //   // model.shader = "joint"

// //   var jointModels = Map[String,Model]()
// //   jointModels += "head" -> Plane().scale(.06f,.065f,.06f)
// //   jointModels += "neck" -> Plane().scale(.02f)
// //   jointModels += "torso" -> Plane().scale(.08f,.08f,.08f)
// //   jointModels += "rshoulder" -> Plane().scale(.02f)
// //   jointModels += "relbow" -> Plane().scale(.02f)
// //   jointModels += "rhand" -> Plane().scale(.02f)
// //   jointModels += "lshoulder" -> Plane().scale(.02f)
// //   jointModels += "lelbow" -> Plane().scale(.02f)
// //   jointModels += "lhand" -> Plane().scale(.02f)
// //   jointModels += "rhip" -> Plane().scale(.03f)
// //   jointModels += "rknee" -> Plane().scale(.02f)
// //   jointModels += "rfoot" -> Plane().scale(.02f)
// //   jointModels += "lhip" -> Plane().scale(.03f)
// //   jointModels += "lknee" -> Plane().scale(.02f)
// //   jointModels += "lfoot" -> Plane().scale(.02f)

// //   jointModels.values.foreach( (m) => {
// //     m.material = Material.specular
// //     m.material.color = color
// //     m.shader = "joint"
// //   })

// //   var phase = Map[String,Float]()
// //   jointModels.keys.foreach((k) => { phase(k) = 2*Pi*Random.float() })
  
// //   var indices = for( i <- 0 until 30; j <- 0 until 3) yield Random.int(0,15)()

// //   def randomizeIndices(){
// //     indices = for( i <- 0 until 30; j <- 0 until 3) yield Random.int(0,15)() 
// //   }

// //   override def draw(){
// //     if(tracking){
// //       model.draw()
// //       linemodel.draw()
// //       // jointModels.foreach{ case (k,m) => 
// //       //   Shader("joint")
// //       //   var sh = Shader.shader.get
// //       //   sh.uniforms("phase") = phase(k)
// //       //   m.draw()
// //       // }
// //     }
// //   }

// //   def drawJoints(){
// //     if(tracking){
// //       jointModels.foreach{ case (k,m) => 
// //         // XXX
// //         // Shader("joint")
// //         // var sh = Shader.shader.get
// //         // sh.uniforms("phase") = phase(k)
// //         // sh.uniforms("color") = m.material.color
// //         m.draw()
// //       }
// //     }
// //   }

// //   override def animate(dt:Float){
// //     droppedFrames += 1
// //     updateBones()

// //     jointModels.foreach{ case(name,m) => 
// //       m.pose.pos.set( joints(name) )
// //     } 

// //     // val list = joints.values.toArray
// //     // joints.zipWithIndex.foreach{ case((k,v),i) =>
// //     //   println(s"$i $k : $v ${list(i)}")
// //     // }

// //     mesh.clear
// //     // val vs = joints.values.toSeq
// //     // for( i <- 0 until 9; j <- 0 until 3){
// //       // mesh.vertices += Random.oneOf(vs : _*)()
// //     // }
// //     mesh.vertices ++= joints.values
// //     // mesh.texCoords ++= joints.values.map( _.xy )
// //     mesh.indices ++= indices
// //     mesh.recalculateNormals()
// //     mesh.update

// //     linemesh.clear
// //     linemesh.vertices ++= mesh.vertices
// //     linemesh.indices ++= lineindices
// //     linemesh.update
// //   }

// //   def setColor(c:RGBA){
// //     color.set(c)
// //     model.material.color.set(color)
// //     jointModels.values.foreach(_.material.color.set(color))
// //   }
// // }
