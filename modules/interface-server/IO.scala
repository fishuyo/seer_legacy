
package com.fishuyo.seer
package interface

import hid._
import actor._

import com.typesafe.config._
import net.ceedubs.ficus.Ficus._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent._

import collection.mutable.HashMap

/**
  * The IO object holds available io nodes, connected devices.. etc.
  */
object IO {

  val ios = HashMap[String,IO]()
  def apply(name:String) = ios(name)
  def update(name:String, io:IO) = ios(name) = io

  def autoConnect(){
    implicit val system = System()
    implicit val materializer = ActorMaterializer()

    val onConnectionEvent = DeviceManager.broadcastConnectionEvent
                             .runWith(Sink.foreach{
                                case DeviceAttached(d) => 
                                case DeviceDetached(d) =>
                                case _ => ()
                              })
  }

  def connect(d:Device){

  }

}

/**
  * An IO represents a device or abstract node, that can generate data streams, and/or receive data streams.
  * It provides a wrapper around a set of dynamic streams utilizing BroadcastHubs and MergeHubs
  * The wrapper also provides a custom DSL for mapping IOs to other IOs
  */
trait IO {
  val sources = HashMap[String,Source[Float,Future[akka.Done]]]()
  val sinks = HashMap[String,Sink[Float,Future[akka.Done]]]()

  // def >>(io:IO) = 

  def close(){}
}

case class IOSource[T,M](value:Source[T,M]){
  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  def >>[U >: T,N](sink:Sink[U,N])(implicit kill:SharedKillSwitch) = value.via(kill.flow).runWith(sink)
}

// case class IOSink[T](value:Sink[T,akka.NotUsed]){
//   implicit val system = System()
//   implicit val materializer = ActorMaterializer()

// }

