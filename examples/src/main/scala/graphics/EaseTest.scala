

package seer
package examples.graphics

import graphics._
import spatial._
import time._
import util._

import concurrent.duration._

object EaseTest extends SeerApp { 

  // make a list of a few generated models
  val models = for(i <- 0 until 8) yield Sphere().scale(0.1f)
  val modelsIn = for(i <- 0 until 8) yield Sphere().scale(0.1f)
  val modelsOut = for(i <- 0 until 8) yield Sphere().scale(0.1f)

  Schedule.oscillate(2.seconds){ case t =>
    // in/out
    for(i <- 0 until 8){
      val d = i match {
        case 0 => t
        case 1 => Ease.quad(t)
        case 2 => Ease.cubic(t)
        case 3 => Ease.quart(t)
        case 4 => Ease.quint(t)
        case 5 => Ease.expo(t)
        case 6 => Ease.sine(t)
        case 7 => Ease.circ(t)
      }
      models(i).pose.pos.set(2*d - 1, -1 + i*0.25, 0)
    }
    // in
    for(i <- 0 until 8){
      val d = i match {
        case 0 => t
        case 1 => Ease.quadIn(t)
        case 2 => Ease.cubicIn(t)
        case 3 => Ease.quartIn(t)
        case 4 => Ease.quintIn(t)
        case 5 => Ease.expoIn(t)
        case 6 => Ease.sineIn(t)
        case 7 => Ease.circIn(t)
      }
      modelsIn(i).pose.pos.set(2*d - 1, 3 + i*0.25, 0)
    }
    //out
    for(i <- 0 until 8){
      val d = i match {
        case 0 => t
        case 1 => Ease.quadOut(t)
        case 2 => Ease.cubicOut(t)
        case 3 => Ease.quartOut(t)
        case 4 => Ease.quintOut(t)
        case 5 => Ease.expoOut(t)
        case 6 => Ease.sineOut(t)
        case 7 => Ease.circOut(t)
      }
      modelsOut(i).pose.pos.set(2*d - 1, 7 + i*0.25, 0)
    }
  }

  override def draw(){
    // call each shapes' draw method
    models.foreach( _.draw() )
    modelsIn.foreach( _.draw() )
    modelsOut.foreach( _.draw() )
  }

  override def animate(dt:Float){
  }

}