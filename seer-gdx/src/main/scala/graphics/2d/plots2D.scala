
// package com.fishuyo.seer
// package graphics

// import spatial._
// import spatial._

// import scala.collection.mutable.Queue

// import com.badlogic.gdx.Gdx
// import com.badlogic.gdx.graphics._
// import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

// /* 
// * Plot stream of size data points scaled by range
// */
// class Plot2D( var size:Int, var range:Float=1f) extends Drawable {
//   var color = Vec3(1f)
//   var pose = Pose()
//   var scale = Vec3(1f)
//   val mesh = new GdxMesh(false,size,0, VertexAttribute.Position)
//   var data = Queue[Float]()
//   data.enqueue(new Array[Float](size):_*)
//   val vertices = new Array[Float](size*3)
//   var dirty = true

//   def apply(f:Float) = {
//     data.enqueue(f)
//     data.dequeue()
//     for( i<-(0 until size)){
//       vertices(3*i) = (i - size/2) / size.toFloat
//       vertices(3*i+1) = data(i) / range
//       vertices(3*i+2) = 0f
//     }
//     dirty = true
//   }

//   override def draw(){ 
//     if( dirty ){
//       mesh.setVertices( vertices )
//       dirty = false
//     }
//     Renderer().setColor(RGBA(color,1f))
//     val s = scale / 2f
//     MatrixStack.push()
//     MatrixStack.transform(pose,s)
//     Renderer().setMatrices()
//     mesh.render(Renderer().shader(), GL20.GL_LINE_STRIP)
//     MatrixStack.pop()
//   }

// }


// /* 
// * Display Audio Samples and boundary/playback cursors
// */
// class AudioDisplay(val size:Int) extends Drawable {
//   var color = RGBA(0,1,0,1)
//   var cursorColor = RGBA(1,1,0,1)
//   var pose = Pose()
//   var scale = Vec3(1)
//   val mesh = new GdxMesh(false,size*2,0, VertexAttribute.Position)
//   val vertices = new Array[Float](size*2*3)
//   val cursorMesh = new GdxMesh(false,6,0,VertexAttribute.Position)
//   val cursorVert = new Array[Float](6*(3))
//   var primitive = GL20.GL_LINE_STRIP
//   var renderSize = size
//   var dirty = true
//   var cursorDirty = true
//   var samples:Array[Float] = _
//   var left = 0
//   var right = 0

//   def setSamplesSimple(s:Array[Float], l:Int=0, r:Int=0){
//     samples = s
//     left = l
//     right = if(r == 0) s.size-1 else r-1
//     for( i<-(0 until size)){
//       val s = i / (size-1).toFloat * (right-left) + left
//       val si = s.toInt
//       val si2 = if( si >= right) left else si + 1
//       val f = s-si
//       vertices(3*i) = (i - size/2) / size.toFloat
//       vertices(3*i+1) = samples(si)*(1f-f) + samples(si2)*f
//       vertices(3*i+2) = 0f
//     }
//     primitive = GL20.GL_LINE_STRIP
//     renderSize = size
//     dirty = true
//   }

//   def setSamples(s:Array[Float], l:Int=0, r:Int=0){
//     samples = s
//     left = l
//     right = if(r == 0) s.size-1 else r-1
//     // for( i<-(0 until size)){
//     //   val s = i / (size-1).toFloat * (right-left) + left
//     //   val si = s.toInt
//     //   val si2 = if( si >= right) left else si + 1
//     //   val f = s-si
//     //   vertices(3*i) = (i - size/2) / size.toFloat
//     //   vertices(3*i+1) = samples(si)*(1f-f) + samples(si2)*f
//     //   vertices(3*i+2) = 0f
//     // }

//     var sp = 0.0f
//     var s1 = left
//     var max = -1f
//     var min = 1f
//     for( i<-(0 until size)){
//       val s = (i+1) / (size).toFloat * (right-left) + left
//       val s2 = s.toInt

//       var offx = 0
//       if( s2 - s1 < 8 ){
//         val off1 = sp - s1
//         val off2 = s - s2
//         try{
//         max = samples(s1)*(1f-off1) + samples(s1+1) * off1
//         min = samples(s2)*(1f-off2) + samples(s2+1) * off2
//         } catch { case e:Exception => println(e) }
//         offx = 1
//       }else{
//         max = -1f
//         min = 1f
      
//         for( j <- (s1 until s2)){
//           val f = samples(j)
//           if( f > max ) max = f
//           if( f < min ) min = f
//         }
//       }
//       s1 = s2
//       sp = s

//       vertices(6*i) = (i - size/2) / size.toFloat
//       vertices(6*i+1) = max
//       vertices(6*i+2) = 0f
//       vertices(6*i+3) = (i + offx - size/2) / size.toFloat
//       vertices(6*i+4) = min
//       vertices(6*i+5) = 0f
//     }
//     primitive = GL20.GL_LINES
//     renderSize = size*2
//     dirty = true
//   }

//   def setCursor(i:Int,sample:Int){
//     val x = (sample - left).toFloat / (right-left).toFloat - .5f
//     cursorVert(6*i) = x
//     cursorVert(6*i+1) = 0.5f
//     cursorVert(6*i+2) = 0.01f
//     cursorVert(6*i+3) = x
//     cursorVert(6*i+4) = -0.5f
//     cursorVert(6*i+5) = 0.01f
//     cursorDirty = true
//   }

//   override def draw(){ 
//     if( dirty ){
//       mesh.setVertices( vertices )
//       dirty = false
//     }
//     if( cursorDirty ){
//       cursorMesh.setVertices( cursorVert)
//       cursorDirty = false
//     }
//     Renderer().textureMix = 0f
//     Renderer().setColor(color)
//     val s = scale / 2f
//     MatrixStack.push()
//     MatrixStack.transform(pose,s)
//     Renderer().setMatrices()
//     mesh.render(Renderer().shader(), primitive, 0, renderSize)
//     Renderer().setColor(cursorColor)
//     cursorMesh.render(Renderer().shader(), GL20.GL_LINES)
//     MatrixStack.pop()
//   }

// }



