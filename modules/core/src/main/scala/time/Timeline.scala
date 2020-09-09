
package seer
package time

import actor._
import akka.actor._
import scala.concurrent.duration._

import collection.mutable.HashMap
import collection.mutable.ListBuffer

import scala.language.postfixOps
import scala.language.implicitConversions



sealed trait Event 
case class Trigger(f:()=>Unit) extends Event
// case class Continuous(f:(Float)=>Unit, t:Double, dur:FiniteDuration) extends Event
case class Fork(t:Timeline) extends Event

object Timeline {}

class Timeline {

  var active = false
  var currentTime = 0.0
  var speed = 0f
  var looping = false
  var bpm = 60

  private val timelineEvents = HashMap[FiniteDuration, ListBuffer[Event]]()
  private val activeEvents = ListBuffer[Event]()

  def apply(t:FiniteDuration) = timelineEvents.getOrElseUpdate(t, ListBuffer[Event]())
  
  def at(t:FiniteDuration)(f: ()=>Unit) = this(t) += Trigger(f) 
  // def over(t:FiniteDuration)(f:(Float)=>Unit) = this(0 seconds) += Continuous(f, 0, t)

  // def at(t:FiniteDuration) = {
  //   val tl = new Timeline()
  //   this(t) += Fork(tl)
  //   tl
  // }


  def start() = active = true
  def stop() = active = false
  def reset() = currentTime = 0f

  def step(dt:Double):Unit = {
    if(!active) return
    val oldTime = currentTime.seconds
    currentTime += dt
    val events = timelineEvents.filter{ case (t,es) => t > oldTime && t <= currentTime.seconds }.values.flatten
    events.foreach{ 
      case Trigger(f) => f()
      // case e @ Continuous(f,t,d) => activeEvents += e
      case e @ Fork(tl) => activeEvents += e
    }

    activeEvents.foreach {
      // case Continuous(f,t,d) => 
      case Fork(tl) => tl.step(dt)
    }
  }

}