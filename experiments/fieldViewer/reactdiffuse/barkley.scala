
package com.fishuyo.seer
package examples.fieldViewer
package reactdiffuse.barkley

import graphics._
import dynamic._
import maths._


object Main extends App with Animatable {

	DesktopApp.loadLibs()
	Scene.push( this )

  val fv = new ReactDiffuseFV
  Scene.push( fv );

  val live = new Ruby("src/main/scala/examples/fieldViewer/reactdiffuse/barkley.rb")

  DesktopApp()

  override def step(dt:Float) = live.step(dt)
}

class ReactDiffuseFV extends FieldViewer(100,100) {
  
  val chemA = new ChemField
  val chemB = new ChemField

  var alphaA = .75f
  var alphaB = 0f
  var eps = .02f
  val dx = 165.f / 200.f //Main.n
  var dtt = .02f
  var A = .75f
  var B = .01f
  
  type RA = List[Float]
  val ra = (a: Float, b:Float ) => (1.f/eps)*(a*(1.f-a)*(a-(b+B)/A))
  val rb = (a: Float, b:Float ) => a-b

  override def resize(x:Int,y:Int){
  	chemA.resize(x,y)
  	chemB.resize(x,y)
  	super.resize(x,y)
  }

  override def runOnce() = chemB.set( A * .5f )

  override def runEverytime(dt:Float) = {

    val s = 5;
    for( j <- (-s to s); i <- (-s to s) ) chemA.set( w/2+i, h/2+j, 1.f )

    // diffusion
    for( y <- ( 1 to h-2 ); x <- ( 1 to w-2 )){
      var a =  -4.f * chemA(x,y) + chemA(x+1,y) + chemA(x-1,y) + chemA(x,y+1) + chemA(x,y-1)
      var b =  -4.f * chemB(x,y) + chemB(x+1,y) + chemB(x-1,y) + chemB(x,y+1) + chemB(x,y-1)
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

  