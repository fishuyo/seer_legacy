package seer
package flow

import hid._

import collection.mutable.HashMap

/**
  * Helper object for accessing registered deviceIO from DeviceManager
  */
object Device {
  val registeredDevices = HashMap[String, (Int) => IO]()
  val deviceTypes = HashMap[String,DeviceType]()

  hid.DeviceManager.startPolling() 
  
  register("PLAYSTATION(R)3 Controller", new PS3Controller(_), DualAnalogJoystick)
  register("Joy-Con (L)", new JoyconL(_))
  register("Joy-Con (R)", new JoyconR(_))
  register("Nintendo RVL-CNT-01", new Wiimote(_))

  def typeOf(name:String):DeviceType = deviceTypes.getOrElse(name, Unknown)
  // def apply(info:HidDeviceInfo, index:Int):Device = apply(info.getProductString, index)
  def apply(name:String, index:Int=0):IO = registeredDevices.getOrElse(name, (i:Int) => new UnknownDevice(name,i))(index)


  // TODO make abstract joystick device? how else can this work?
  // Because it will need to work without knowing which device will be connected, and then become that device
  // maybe some kind of wrapping class that listens for nth connecting joystick, or checks with DM 
  def joystick(index:Int = 0):IO = new PS3Controller(index)
  // = new DualAnalogJoystick(index) no. --> abstract. future? promise?
  // how do I return something, generic.. in time.. or..
  // DualAnalogJoystickIO is not abstract, but a wrapper sort of? -- its genericness allows
  // it to grab a device connection based on type --
  // once connection is made, a way to retrieve actual deviceIO ... hmm.. no. doesn't work.
  // Ummm
  // Ok.
  

  // - option 1 -> HidDeviceGenericTypeConnection -- listen for device connection, filter for type -- become that device..
  //   - would potentially double specific HidDeviceConnection for device..
  // - option 2 -> HidDeviceGenericTypeIO -- add listen into device IO (instance)? hmm
  // - option 3 -> add multiple constructors to HidDeviceIO to create from type instead
  // -- need to keep order of all connections not just by name
  // -- need awareness of device type in device connection?
  // who knows? the implementation knows --> impl is name to source & sinks config..
  // Could have a lookup function: name -> deviceType -- which implementation augments..
  // then a devicehidIO can getDeviceConnectionByType(type, index) --> and fill in info?
  // so Joystick funciton above needs to map an index to an generic class JoystickIO 
  // JoystickIO should be checkable / castable to specific ie PS3Controller..
  // not possible, must already be connected to work..
  

  // mechanism to register and implement devices at runtime..
  // def register(d:Device) = registeredDevices(d.name)
  def register(name:String, construct:(Int) => IO, deviceType:DeviceType = Unknown) = {
    registeredDevices(name) = construct
    deviceTypes(name) = deviceType
  }
}
