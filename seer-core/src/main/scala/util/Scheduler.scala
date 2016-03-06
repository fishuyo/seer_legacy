
package com.fishuyo.seer
package util

import graphics._

import actor._
import akka.actor._
import scala.concurrent.duration._

import collection.mutable.ListBuffer


class Schedulable extends Cancellable with Animatable{
	var cancelled = false
	var duration = 0 millis
	var t = 0 millis
	var percent = 0.0
	var speed = 1.0
	var paused = false

	Scene.push(this)

	def updateDuration(d:FiniteDuration){
		if(d == 0.seconds) return
		val tt = d*percent
		if(tt.isFinite){
			duration = d
			t = tt.asInstanceOf[FiniteDuration]
		}
	}

	override def isCancelled = cancelled
	override def cancel() = { Scene.remove(this); cancelled = true; true}
}

object Schedule {
	implicit def int2fd(i:Int):FiniteDuration = i.milliseconds
	implicit def d2f(d:Double) = d.toFloat

	val events = ListBuffer[Cancellable]()

	val system = System()
	import system.dispatcher


	def after(t:FiniteDuration)(f: =>Unit) = {
		val e = System().scheduler.scheduleOnce(t)(f)
		events += e
		e
	}

	def every(t:FiniteDuration)(f: =>Unit) = {
		val e = System().scheduler.schedule(t,t)(f)
		events += e
		e
	}

	def over(len:FiniteDuration)(f:(Float)=>Unit) = {
		val e = new Schedulable {
			duration = len
			override def animate(dt:Float){
				if(paused) return
				t += (speed * dt.toDouble).seconds
				if(t > duration){
					cancel()
					f(1f)
				} else {
					percent = t/duration
					f(percent)
				}
			}
		}
		// val e = new Cancellable{
		// 	var cancelled = false

		// 	val a = new Animatable {
		// 		val deadline = len.fromNow
		// 		var t = 0.millis
		// 		override def animate(dt:Float){
		// 			t += (dt.toDouble).seconds
		// 			if(deadline.isOverdue){
		// 				Scene.remove(this)
		// 				cancelled = true
		// 				f(1f)
		// 			} else f(t/len)
		// 		}
		// 	}
		// 	Scene.push(a)

		// 	override def isCancelled = cancelled
		// 	override def cancel(){ Scene.remove(a); cancelled = true}
		// }
		events += e
		e
	}

	def cycle(len:FiniteDuration)(f:(Float)=>Unit) = {
		val e = new Schedulable {
			duration = len
			override def animate(dt:Float){
				if(paused) return
				t += (speed * dt.toDouble).seconds
				if(t > duration) t -= duration 
				percent = t/duration
				f(percent)
			}
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
