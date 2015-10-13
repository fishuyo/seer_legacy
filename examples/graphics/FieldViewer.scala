

package com.fishuyo.seer
package examples.graphics

// import dynamic._
import graphics._
import spatial._
import io._

object FieldViewer extends SeerApp {

  val fieldViewer = new DiffuseFV
  Scene.push(fieldViewer)

  override def animate(dt:Float){
    try{
      val m = Mouse.xy() * fieldViewer.w
      fieldViewer.field.set(m.x.toInt,m.y.toInt,1f)
    } catch { case e:Exception => ()}
  }

  Keyboard.clear
  Keyboard.use
  Keyboard.bind("i", () => {fieldViewer.eta += 1; println(fieldViewer.eta)})
  Keyboard.bind("k", () => {fieldViewer.eta -= 1; println(fieldViewer.eta)})
}

class DiffuseFV extends FieldViewer(200,200) {

  var alphaA = .75f
  // var alphaB = 0f
  val dx = 1f //165f / 200f //Main.n
  var dtt = .15f //.02f
  var eta = 9f
  
  override def runOnce(){
    field.set(util.Random.float(0.1,0.9)())
    val (cx,cy) = (50,50)
    var idx = 0
    val l = List(0.75f,0.5,0.5,0.75,1,0,0,1,0.75,0.5,0.5,0.75)
    for( y <- -1 to 1; x <- -2 to 1){
      field.set(cx+x,cy+y,l(idx))  
      idx +=1  
    }
  }

  override def runEverytime(dt:Float) = {

    // diffusion
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      if( field(x,y).r < 1 && field(x,y).r > 0){
        var a =  -4f * field(x,y) + field(x+1,y) + field(x-1,y) + field(x,y+1) + field(x,y-1)
        a = field(x,y) + a * dtt * alphaA / (dx*dx)
        next.set(x,y,a)
      } else next.set(x,y,field(x,y))
    }

    var sum=0f
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      if( field(x,y).r != 0 ){
        if(field(x+1,y).r == 0 || field(x-1,y).r == 0 || field(x,y+1).r == 0 || field(x,y-1).r == 0){
          sum += math.pow(field(x,y).r, eta)
        }
      }
    }
    val pick = sum * util.Random.float()
    // println(pick)
    sum = 0f
    var break = false
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      if( !break && field(x,y).r != 0 ){
        if(field(x+1,y).r == 0 || field(x-1,y).r == 0 || field(x,y+1).r == 0 || field(x,y-1).r == 0){
          sum += math.pow(field(x,y).r, eta)
          if(sum > pick){
            next.set(x,y,0f)
            // println(s"$x $y")
            break = true
          }
        }
      }
    }


    field.set( next )
  }
}

// solve laplace eq everywhere except the border(= 1) and the aggregate( = 0) stored in field “potential”
// sum = 0
// for each non-aggregate cell(x,y)
//   if neighbor is an aggregate
//     sum += potential(x,y)
//   end
// end
// pick = sum * random(0,1)
// sum = 0
// for each non aggregate cell(x,y)
//   if neighbor is in aggregate
//     sum += potential(x,y)
//     if( sum > pick)
//       add cell(x,y) to aggregate
//       break loop
//     end
//   end
// end


class ReactDiffuseFV extends FieldViewer(100,100) {
  
  val chemA = new ChemField
  val chemB = new ChemField

  var alphaA = .75f
  var alphaB = 0f
  var eps = .02f
  val dx = 165f / 200f //Main.n
  var dtt = .02f
  var A = .75f
  var B = .01f
  
  type RA = List[Float]
  val ra = (a: Float, b:Float ) => (1f/eps)*(a*(1f-a)*(a-(b+B)/A))
  val rb = (a: Float, b:Float ) => a-b

  override def resize(x:Int,y:Int){
    chemA.resize(x,y)
    chemB.resize(x,y)
    super.resize(x,y)
  }

  override def runOnce() = chemB.set( A * .5f )

  override def runEverytime(dt:Float) = {

    val s = 5;
    for( j <- (-s to s); i <- (-s to s) ) chemA.set( w/2+i, h/2+j, 1f )

    // diffusion
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      var a =  -4f * chemA(x,y) + chemA(x+1,y) + chemA(x-1,y) + chemA(x,y+1) + chemA(x,y-1)
      var b =  -4f * chemB(x,y) + chemB(x+1,y) + chemB(x-1,y) + chemB(x,y+1) + chemB(x,y-1)
      a = chemA(x,y) + a * dtt * alphaA / (dx*dx)
      b = chemB(x,y) + b * dtt * alphaB / (dx*dx)
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)
    }

    // reaction
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      
      var a = chemA.next(x,y) + dtt * ra( chemA.next(x,y), chemB.next(x,y))
      var b = chemB.next(x,y) + dtt * rb( chemA.next(x,y), chemB.next(x,y))
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)

      val color = RGBA(0,1,0,1) * a + RGBA(.5f,.0f,.5f,1) * b
      field.set(x,y, color )
    }
    
    chemA.set(chemA.next)
    chemB.set(chemB.next)

  }
}

class ChemField extends Field2D(100,100){
  val next = new Field2D(100,100)

  override def resize(x:Int,y:Int){
    next.resize(x,y)
    super.resize(x,y)
  }
}


// make subclass of FieldViewer and override runOnce and runEverytime
class ConwayFV(ww:Int, hh:Int) extends FieldViewer(ww,hh) {

  override def runOnce() = {
    for( y <- 20 to 40; x <- 20 to 40){
      field.set(x,y,1f)
    }
  }

  override def runEverytime(dt:Float) = {
    for( y <- (0 until h); x <- (0 until w)){  
      var count = 0;
      for( j <- (-1 to 1); i <- (-1 to 1)){
        count += field.getToroidal(x+i,y+j).r.toInt
      }
      
      //was alive
      if( field(x,y).r > 0f ){
        count -= 1
        if( count == 2 || count == 3) next.set(x,y,1f)
        else {
        next.set(x,y,0f)
        //println( x + " " + y + " dieing")
        }
      }else if( field(x,y).r == 0f) { //was dead
        if( count == 3 ){
          next.set(x,y,1f)
          //println( x + " " + y + " born")
        }
        else next.set(x,y,0f)
      }
    }
    field.set( next )
  }
  
}

class FieldViewer(var w:Int, var h:Int) extends Animatable {

  var running = true;
  var field = new Field2D(w,h)
  var next = new Field2D(w,h)

  def resize(width:Int,height:Int){
    w = width
    h = height
    field.resize(w,h)
    next.resize(w,h)
  }

  def toggleRunning() = running = !running

  override def init() = runOnce()
  override def draw() = {
    // Shader.textureMix = 1f
    field.draw
  }
  override def animate(dt:Float) = if(running) runEverytime(dt)


  def runOnce(){

  }

  def runEverytime(dt:Float){
  }
}



