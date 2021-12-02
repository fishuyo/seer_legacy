
// package com.fishuyo.seer
// package examples.graphics

// import graphics._

// object ImageField extends SeerApp {

//   val fv = new Diffuse(400,400)

//   Scene += fv

//   override def animate(dt:Float){
//     try{
//       val m = io.Mouse.xy.now * fv.w
//       fv.field.setFloat(m.x.toInt,m.y.toInt,1f)
//     } catch { case e:Exception => ()}
//   }
// }

// class Diffuse(_w:Int, _h:Int) extends FieldView(_w,_h) {

//   var alphaA = .75f
//   // var alphaB = 0f
//   val dx = 1f //165f / 200f //Main.n
//   var dtt = .15f //.02f
//   var eta = 9f
  
//   override def init(){
//     // field.set(util.Random.float(0.1,0.9)())
//     // field.set(1f)
//     val (cx,cy) = (w/2,h/2)
//     var idx = 0
//     for( y <- -10 to 10; x <- -10 to 10){
//       field.setFloat(cx+x,cy+y,1f)  
//     }
//     super.init()
//   }

//   override def animate(dt:Float) = {
//     // diffusion
//     val (cx,cy) = (w/2,h/2)
//     var idx = 0
//     for( y <- -10 to 10; x <- -10 to 10){
//       field.setFloat(cx+x,cy+y,1f)  
//     }

//     for( y <- ( 1 until h-1 ); x <- ( 1 until w-1 )){
//       var a =  -4f * field.getFloat(x,y) + field.getFloat(x+1,y) + field.getFloat(x-1,y) + field.getFloat(x,y+1) + field.getFloat(x,y-1)
//       a = field.getFloat(x,y) + a * dtt * alphaA / (dx*dx)
//       next.setFloat(x,y,a)
//     }

//     field.set( next )
//   }
// }


// class FieldView(var w:Int, var h:Int) extends Animatable {

//   var field = Image(w,h,1,4)
//   var next = Image(w,h,1,4)
//   var texture:Texture = _ 
//   val quad = Plane()

//   override def init(){
//     texture = Texture(field)
//     quad.material.color.set(1,0,0,1)
//     quad.material.loadTexture(texture)
//   }

//   def resize(width:Int,height:Int){
//     w = width
//     h = height
//     field.resize(w,h)
//     next.resize(w,h)
//   }

//   override def draw() = {
//     texture.update
//     quad.draw
//   }

// }