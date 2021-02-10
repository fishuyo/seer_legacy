package seer
package flow
package hid

import purejavahidapi._

import collection.JavaConverters._
import collection.mutable.ListBuffer
import collection.mutable.HashMap

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._

import seer.actor._

/** DeviceConnectionEvent case classes **/
sealed trait DeviceConnectionEvent { def device:HidDeviceInfoW; def index:Int }
case class DeviceAttached(device:HidDeviceInfoW, index:Int) extends DeviceConnectionEvent
case class DeviceDetached(device:HidDeviceInfoW, index:Int) extends DeviceConnectionEvent


/** Wrapper for device polling, override equality checking of HidDeviceInfo */
class HidDeviceInfoW(val info:HidDeviceInfo) {
  override def equals(other:Any) = other match {
    case b:HidDeviceInfoW =>
      (info.getProductId == b.info.getProductId && info.getVendorId == b.info.getVendorId && info.getSerialNumberString == b.info.getSerialNumberString)
    case _ => false
  }
  override def hashCode = {
    val s = info.getSerialNumberString
    41*(41+info.getProductId) + info.getVendorId + (if(s == null) 0 else s.hashCode)
  }
}


/** 
  * Device Manager handles connection events of HidDevices 
  */
object DeviceManager {

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  // map of connected devices keyed on product string
  private val infoLock = new Object
  private val availableDevices = ListBuffer[HidDeviceInfoW]()
  private val deviceConnections = ListBuffer[HidDeviceConnection]()
  
  // create a stream actor for connection events
  // using a broadcast hub to allow sending connection events to multiple consumers
  private var eventStreamActor = None:Option[ActorRef]
  private val connectionEventSource = Source.actorRef[DeviceConnectionEvent](bufferSize = 0, OverflowStrategy.fail)
                                    .mapMaterializedValue( (a:ActorRef) => eventStreamActor = Some(a) )
  val connectionEvents = connectionEventSource.toMat(BroadcastHub.sink)(Keep.right).run() 
  connectionEvents.runWith(Sink.ignore)  // default consumer keeps stream running

  def getDevices() = infoLock.synchronized{ availableDevices }

  def getRegisteredDevices() = infoLock.synchronized {
    availableDevices.filter { case i => Device.registeredDevices.contains(i.info.getProductString) }
  }

  // def getDeviceInfo(name:String, index:Int=0) = infoLock.synchronized {
  //   getInfo(name,index)
  // }
  
  private def getInfo(name:Option[String], deviceType:DeviceType, index:Int) = {
    if(name.isDefined){
      val ds = availableDevices.filter(_.info.getProductString == name.get)
      if(index < ds.length){
        val di = ds(index)
        Some(di.info)
      } else None
    } else {
      val ds = availableDevices.filter{ case di => Device.typeOf(di.info.getProductString) == deviceType }
      if(index < ds.length){
        val di = ds(index)
        Some(di.info)
      } else None
    }
  }

  def getDeviceConnection(name:Option[String], deviceType:DeviceType, index:Int) = this.synchronized {
    
    (name, deviceType) match {
      case (Some(str), _) =>
        val dcs = deviceConnections.find{ case dc => dc.name.contains(str) && dc.index == index }
        dcs match {
          case Some(dc) =>
            println(s"Using existing device connection: $str $index")
            dc
          case None =>
            val dc = new HidDeviceConnection(name, deviceType, index)
            if(index >= 0){
              deviceConnections += dc 
              openDeviceConnection(dc)
              listenConnectionEvents(dc)
            } else closeDeviceConnection(dc)
            dc
        }

      case (None, dtype) =>
        val dcs = deviceConnections.find{ case dc => dc.deviceType == dtype && dc.index == index }
        dcs match {
          case Some(dc) =>
            println(s"Using existing device connection: $dtype $index")
            dc
          case None =>
            val dc = new HidDeviceConnection(name, deviceType, index)
            if(index >= 0){
              deviceConnections += dc 
              openDeviceConnection(dc)
              listenConnectionEvents(dc)
            } else closeDeviceConnection(dc)
            dc
        }
    }   
  }

  def listenConnectionEvents(dc:HidDeviceConnection) = {
    DeviceManager.connectionEvents.via(dc.kill.flow).filter { case e =>
      val name = e.device.info.getProductString
      dc.index == e.index && 
        (dc.name.contains(name) ||
        (dc.name.isEmpty && Device.typeOf(name) == dc.deviceType))
    }.runForeach {
      case DeviceAttached(dev,idx) => openDeviceConnection(dc) 
      case DeviceDetached(dev,idx) => closeDeviceConnection(dc)
    }
  }
  
  def openDeviceConnection(dc:HidDeviceConnection) = infoLock.synchronized {
    val option = getInfo(dc.name, dc.deviceType, dc.index)
    option.foreach { case di =>
      println(s"Opening device connection: ${dc.name} ${dc.deviceType} ${dc.index}")
      val dev = PureJavaHidApi.openDevice(di)
      dev.setInputReportListener( new InputReportListener(){
        override def onInputReport(source:HidDevice, id:Byte, data:Array[Byte], len:Int){
          dc.byteStreamActor.foreach(_ ! data)
        }
      })
      dc.openDevice = Some(dev)
    }
  }

  def closeDeviceConnection(dc:HidDeviceConnection) = {
    dc.kill.shutdown
    // println(s"TODO close device connection: ${dc.name} ${dc.index}")
  }

  // for polling device list to generate device connection Events
  private var poller:Option[Cancellable] = None
  private var lastDeviceList = List[HidDeviceInfoW]()
  
  // start polling for devices at 1 second interval
  def startPolling(){
    import concurrent.duration._
    import concurrent.ExecutionContext.Implicits.global
    if(poller.isDefined) return
    poller = Some( system.scheduler.schedule(0.seconds, 1000.millis)(poll) )
  }

  def stopPolling(){
    poller.foreach(_.cancel)
    poller = None
  }

  private def poll(){
    infoLock.synchronized {
      val devs = PureJavaHidApi.enumerateDevices.asScala.toList
      val deviceList = devs.map(new HidDeviceInfoW(_))
      val removed = lastDeviceList.diff(deviceList)
      val added = deviceList.diff(lastDeviceList)
      val updated = deviceList.intersect(lastDeviceList)

      lastDeviceList = deviceList
      removed.foreach(detach(_)) // TODO how does device index change, or not change.
      updated.foreach(update(_))
      added.foreach(attach(_))

      // if(removed.length + added.length > 0) 
        // WebsocketActor.sendDeviceList
    }
  }

  private def attach(d:HidDeviceInfoW){
    println(s"Attached: ${d.info.getProductString}")
    val index = availableDevices.filter( _.info.getProductString == d.info.getProductString).length
    availableDevices += d
    eventStreamActor.foreach( _ ! DeviceAttached(d, index)) // Maybe don't pass deviceinfo in events, as they could potentially be stale, require call back into DM to get current info
  }

  private def update(d:HidDeviceInfoW){
    val index = availableDevices.indexOf(d) // will match old info
    availableDevices(index) = d               // replace with new info
  }

  private def detach(d:HidDeviceInfoW){
    println(s"Detached: ${d.info.getProductString}")
    val index = availableDevices.indexOf(d)
    val di = availableDevices.remove(index)
    eventStreamActor.foreach( _ ! DeviceDetached(di, index))
  }

}