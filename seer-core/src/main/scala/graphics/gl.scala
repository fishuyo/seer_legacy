
// package com.fishuyo.seer
// package graphics
// import maths._
// import spatial._


// // import scala.collection.mutable.ListBuffer
// // import scala.collection.mutable.ArrayBuffer
// // import scala.collection.mutable.Queue
// import scala.collection.immutable.Stack

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._
// import com.badlogic.gdx.graphics.glutils.ImmediateModeRenderer20
// import com.badlogic.gdx.math.Matrix4
// import com.badlogic.gdx.math.Vector3

// object GLImmediate {
//   val renderer = new ImmediateModeRenderer20(true,true,2)
// }

// trait GLThis {
//   def gli = GLImmediate.renderer
//   def gl = Gdx.gl
//   def gl10 = Gdx.gl10
//   def gl11 = Gdx.gl11
//   def gl20 = Gdx.gl20
// }

// trait Drawable extends GLThis {
//   def init(){}
//   def draw(){}
//   def draw2(){}
// }
// trait Animatable extends Drawable {
//   def step( dt: Float){}
// }


// // class Poseable(val p:GlDrawable) extends Drawable {
// //   var pose = Pose()
// //   var scale = Vec3(1)
// //   override def draw(){

// //   }
// // }

// /*
// * Model as a collection of primitives with relative transforms, and animation
// */
// object Model {
//   def apply(pos:Vec3) = new Model(Pose(pos,Quat()),Vec3(1))
//   def apply(pose:Pose=Pose(),scale:Vec3=Vec3(1)) = new Model(pose,scale)
//   def apply(prim:Drawable) = { val m=new Model(); m.add(prim) }
//   def apply(m:Model):Model = {
//     val model = new Model(Pose(m.pose),Vec3(m.scale))
//     model.color = m.color
//     m.nodes.foreach( (n) => model.nodes = model.nodes :+ Model(n) )
//     m.primitives.foreach( (p) => model.primitives = model.primitives :+ p )
//     model
//   }
// }

// class Model(var pose:Pose=Pose(), var scale:Vec3=Vec3(1)) extends Animatable with geometry.Pickable {
//   var color = RGBA(1,1,1,.6f)
//   var nodes = Vector[Model]()
//   var primitives = Vector[Drawable]()
//   // var pickable = Vector[geometry.Pickable]()

//   var worldTransform = new Matrix4

//   def translate(p:Vec3) = transform(Pose(p,Quat()),Vec3(1))
//   def rotate(q:Quat) = transform(Pose(Vec3(),q),Vec3(1))
//   def scale(s:Vec3) = transform(Pose(),s)

//   def transform(p:Pose,s:Vec3=Vec3(1.f)) = {
//     val m = new Model(p,s)
//     nodes = nodes :+ m
//     m
//   }

//   def add(p:Drawable) = {
//     primitives = primitives :+ p
//     this
//   }
//   // def add(p:geometry.Pickable) = {
//   //   pickable = pickable :+ p
//   //   this
//   // }
//   def addNode(m:Model) = {
//     nodes = nodes :+ Model(m)
//   }
//   def getLeaves():Vector[Model] = {
//     if( nodes.length == 0) Vector(this)
//     else nodes.flatMap( _.getLeaves )
//   }

//   override def draw(){
//     MatrixStack.push()

//     MatrixStack.transform(pose,scale)
//     worldTransform.set(MatrixStack.model)

//     Shader.setMatrices()
//     Shader.setColor(color)

//     primitives.foreach( _.draw() )
//     nodes.foreach( _.draw() )

//     MatrixStack.pop()
//   }
//   override def step(dt:Float){

//   }

//   override def intersect(ray:Ray):Option[geometry.Hit] = {
    
//     val (pos,scale) = (new Vector3(),new Vector3())
//     worldTransform.getTranslation(pos)
//     worldTransform.getScale(scale)

//     var hits:Vector[geometry.Hit] = primitives.collect {
//       case q:Quad => ray.intersectQuad(Vec3(pos.x,pos.y,pos.z), scale.x, scale.y ) match {
//           case Some(t) => new geometry.Hit(this, ray, t)
//           case None => null
//       }
//     }

//     hits = (hits ++ nodes.map( _.intersect(ray).getOrElse(null) )).filterNot( _ == null).sorted
//     if( hits.isEmpty ) None
//     else Some(hits(0))
//   }

//   override def toString() = {
//     var out = pose.pos.toString + " " + scale.toString + " " + nodes.length + " " + primitives.length + "\n"
//     nodes.foreach( out += _.toString )
//     out
//   }

// }








