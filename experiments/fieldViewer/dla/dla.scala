
package com.fishuyo.seer
package examples.fieldViewer
package dla

import graphics._
import maths._


object Main extends App {
  DesktopApp.loadLibs()

  val fv = new DlaFV(300,300)
  Scene.push( fv );

  DesktopApp();

}

class DlaFV(ww:Int, hh:Int) extends FieldViewer(ww,hh){
  
  val colors = Array( RGBA(.3f,.7f,.1f,1), RGBA(.6f, .1f, .1f,1), RGBA( .1f, 0,.8f,1), RGBA( 1,.9f,.9f,1))
  var walk = (0,0)
  var c = RGBA(1,1,1,1)

  override def runOnce() = field.set(w/2,h/2, 1.f)
  override def runEverytime(dt:Float) = {

    genWalker()
    var stuck = 0
    while(stuck < 10) if(randWalk()) stuck += 1
  }

  def randWalk():Boolean = {
    import scala.util.Random._
    
    val dir = nextInt(4)
    dir match {
      case 0 => walk = (walk._1,walk._2+1)
      case 1 => walk = (walk._1,walk._2-1)
      case 2 => walk = (walk._1+1,walk._2)
      case 3 => walk = (walk._1-1,walk._2)
      case _ => null
    }
    
    if(walk._1 < 1 || walk._1 > w-2 || walk._2 < 1 || walk._2 > h-2) genWalker()

    val x = walk._1
    val y = walk._2

    if( field(x+1,y) > 0.f || field(x-1,y) > 0.f || field(x,y+1) > 0.f || field(x,y-1) > 0.f ){
      field.set(x,y,c )
      genWalker()
      //c += Vec3( nextFloat * .01 - .005, nextFloat*.01-.005, nextFloat*.01-.005 )
      return true
    }
    false
  }

  def genWalker() = {
    import scala.util.Random._
    val i = nextInt(w-2)+1
    val side = nextInt(4)
    side match {
      case 0 => walk = (1,i)
      case 1 => walk = (w-2,i)
      case 2 => walk = (i,1)
      case 3 => walk = (i,h-2)
      case _ => null
    }
    c = colors(side)

  }

  def color( x: Float ) : RGBA = {

    var i = math.abs(2*x*colors.length-1).toInt - 1
    if( i < 0 ) i = 0
    if( i >= colors.length-1) i = colors.length-2
    val c1 = colors(i)
    val c2 = colors(i+1)
    c1 * (1-x) + c2 * (x)

  }

}


