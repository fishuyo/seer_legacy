
package com.fishuyo.seer
package util

import graphics._

import actor._
import akka.actor._
import scala.concurrent.duration._

import system.dispatcher

import collection.mutable.ListBuffer


object Schedule {
	implicit def int2fd(i:Int):FiniteDuration = i.milliseconds
	implicit def d2f(d:Double) = d.toFloat

	val events = ListBuffer[Cancellable]()

	def after(t:FiniteDuration)(f: =>Unit) = {
		val e = system.scheduler.scheduleOnce(t)(f)
		events += e
		e
	}

	def every(t:FiniteDuration)(f: =>Unit) = {
		val e = system.scheduler.schedule(t,t)(f)
		events += e
		e
	}

	def over(len:FiniteDuration)(f:(Float)=>Unit) = {
		val e = new Cancellable{
			var cancelled = false

			val a = new Animatable {
				val deadline = len.fromNow
				var t = 0.millis
				override def animate(dt:Float){
					t += (dt.toDouble).seconds
					if(deadline.isOverdue){
						Scene.remove(this)
						cancelled = true
						f(1.f)
					} else f(t/len)
				}
			}
			Scene.push(a)

			override def isCancelled = cancelled
			override def cancel(){ Scene.remove(a); cancelled = true}
		}
		events += e
		e
	}

	def cycle(len:FiniteDuration)(f:(Float)=>Unit) = {
		val e = new Cancellable{ 
			val a = new Animatable {
				var deadline = len.fromNow
				var t = 0.millis
				override def animate(dt:Float){
					t += (dt.toDouble).seconds
					if(deadline.isOverdue){
						deadline = len.fromNow
						t -= len 
					}
					f(t/len)
				}
			}
			Scene.push(a)
			var cancelled = false
			override def isCancelled = cancelled
			override def cancel(){ Scene.remove(a); cancelled = true}
		}
		events += e
		e
	}

	def clear(){events.foreach(_.cancel); events.clear}
}


// class TimeActor extends Actor with akka.actor.ActorLogging {
//   var busy = false

//   override def preStart() = {
//     log.debug("Starting")
//   }
//   override def preRestart(reason: Throwable, message: Option[Any]) {
//     log.error(reason, "Restarting due to [{}] when processing [{}]",
//       reason.getMessage, message.getOrElse(""))
//   }

//   def receive = {
//     case "tick" => 
//     case _ => ()
//   }
// }
