
// package com.fishuyo.seer

// import types._

// import rx._
// import rx.async._
// import rx.async.Platform._
// import scala.concurrent.duration._

// import scala.language.postfixOps

// class Sampler[T:scala.reflect.ClassTag](val in:Rx[T])(implicit ownerCtx: rx.Ctx.Owner){

//   val buffer = new LoopBuffer[T](1000)
//   val out = Var[T](in.now)

//   var frequency = 60.0f
//   var recording = false
//   var playing = false
//   var clock = Timer(1/frequency seconds)

//   createObserver

//   def setFrequency(f:Float): Unit ={
//     frequency = f
//     clock.kill()
//     clock = Timer(1/frequency seconds)
//     createObserver
//   }

//   def createObserver(implicit ownerCtx: rx.Ctx.Owner): Unit ={
//     clock.triggerLater {
//       if(recording){
//         buffer += in.now
//       }
//       if(playing){
//         out() = buffer()
//       }
//     }
//   }

//   def record() = recording = true
//   def play() = playing = true
//   def stop(): Unit ={ recording = false; playing = false}


// }