
package com.fishuyo.seer
package hid

import actor._

import org.hid4java._
import org.hid4java.event.HidServicesEvent

import collection.JavaConverters._
import collection.mutable.ListBuffer
import collection.mutable.HashMap

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

sealed abstract class DeviceConnectionEvent
case class DeviceAttached(device:Device) extends DeviceConnectionEvent
case class DeviceDetached(device:Device) extends DeviceConnectionEvent
case class DeviceFailure(id:String) extends DeviceConnectionEvent
// case class DeviceConnectionEvent(device:HidDevice, event:String)

object DeviceManager extends HidServicesListener {

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  // listen to hid device connection events
  val services = HidManager.getHidServices
  services.addHidServicesListener(this)

  // map of connected devices
  val devices = HashMap[String,ListBuffer[Device]]()

  // create a stream actor for connection events
  var eventStreamActor = None:Option[ActorRef]
  val connectionEventStream = Source.actorRef[DeviceConnectionEvent](bufferSize = 0, OverflowStrategy.fail)
                                    .mapMaterializedValue( (a:ActorRef) => eventStreamActor = Some(a) )

  // use a broadcast hub to allow sending connection events to multiple consumers
  val broadcastConnectionEvent = connectionEventStream.toMat(BroadcastHub.sink)(Keep.right).run() 
  broadcastConnectionEvent.runWith(Sink.ignore) // default consumer just discards messages
  // broadcastConnectionEvent.runForeach(println(_))

  // add already connected devices
  getHidDevices.foreach(attach(_))


  def shutdown() = services.shutdown

  def getRegisteredDevices = devices.filter { case (k,v) => Device.registeredDevices.contains(k) }

  def getHidDevices : List[HidDevice] = services.getAttachedHidDevices.asScala.toList
  def getHidDevices(vendorId:Int, productId:Int, serialNumber:String) : List[HidDevice] = getHidDevices.filter( _.isVidPidSerial(vendorId, productId, serialNumber))
  def getHidDevices(product:String) : List[HidDevice] = getHidDevices.filter( _.getProduct == product )
  def getDevicesWithManufacturer(manufacturer:String) = getHidDevices.filter( _.getManufacturer == manufacturer )

  def joysticks = devices.values.flatMap( _.filter(_.deviceType == DualAnalogJoystick)).toList
  


  def attach(d:HidDevice){
    val device = Device(d)
    val ds = devices.getOrElseUpdate(d.getProduct, ListBuffer[Device]())
    device.id = ds.length
    ds += device
    eventStreamActor.foreach( _ ! DeviceAttached(device))
  }

  def detach(d:HidDevice){
    val ds = devices(d.getProduct).filter(_.device.getId() == d.getId())
    ds.foreach { case device =>
      devices(d.getProduct) -= device
      eventStreamActor.foreach( _ ! DeviceDetached(device))
    }
  }

  override def hidDeviceAttached(e:HidServicesEvent){
    println("Device attached: " + e);
    attach(e.getHidDevice)
  }

  override def hidDeviceDetached(e:HidServicesEvent){
    println("Device detached: " + e);
    detach(e.getHidDevice)
  }

  override def hidFailure(e:HidServicesEvent){
    println("HID failure: " + e);
    eventStreamActor.foreach( _ ! DeviceFailure(e.getHidDevice.getId()))
  }

}