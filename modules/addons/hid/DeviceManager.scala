
package com.fishuyo.seer
package hid

import org.hid4java._
import org.hid4java.event.HidServicesEvent

import collection.JavaConverters._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

case class DeviceConnectionEvent(device:HidDevice, event:String)

object DeviceManager extends HidServicesListener {

  // listen to hid device connection events
  val services = HidManager.getHidServices
  services.addHidServicesListener(this)

  // create a stream actor for connection events
  var eventStreamActors = collection.mutable.ListBuffer[ActorRef]()
  val connectionEventStream = Source.actorRef[DeviceConnectionEvent](bufferSize = 0, OverflowStrategy.fail)
                                    .mapMaterializedValue( (a:ActorRef) => eventStreamActors += a )

// // A simple producer that publishes a new "message" every second
// val producer = Source.tick(1.second, 1.second, "New message")
 
// // Attach a BroadcastHub Sink to the producer. This will materialize to a
// // corresponding Source.
// // (We need to use toMat and Keep.right since by default the materialized
// // value to the left is used)
// val runnableGraph: RunnableGraph[Source[String, NotUsed]] =
//   producer.toMat(BroadcastHub.sink(bufferSize = 256))(Keep.right)
 
// // By running/materializing the producer, we get back a Source, which
// // gives us access to the elements published by the producer.
// val fromProducer: Source[String, NotUsed] = runnableGraph.run()
 
// // Print out messages from the producer in two independent consumers
// fromProducer.runForeach(msg => println("consumer1: " + msg))
// fromProducer.runForeach(msg => println("consumer2: " + msg))

  def shutdown() = services.shutdown

  def getDevices : List[HidDevice] = services.getAttachedHidDevices.asScala.toList
  def getDevices(vendorId:Int, productId:Int, serialNumber:String) : List[HidDevice] = getDevices.filter( _.isVidPidSerial(vendorId, productId, serialNumber))
  def getDevices(product:String) : List[HidDevice] = getDevices.filter( _.getProduct == product )
  def getDevicesWithManufacturer(manufacturer:String) = getDevices.filter( _.getManufacturer == manufacturer )



  override def hidDeviceAttached(e:HidServicesEvent){
    println("Device attached: " + e);
    eventStreamActors.foreach( _ ! DeviceConnectionEvent(e.getHidDevice, "attached"))
  }
  override def hidDeviceDetached(e:HidServicesEvent){
    println("Device detached: " + e);
    eventStreamActors.foreach( _ ! DeviceConnectionEvent(e.getHidDevice, "detached"))
  }
  override def hidFailure(e:HidServicesEvent){
    println("HID failure: " + e);
    eventStreamActors.foreach( _ ! DeviceConnectionEvent(e.getHidDevice, "failure"))
  }

}