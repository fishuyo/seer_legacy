
package com.fishuyo.seer
package hid

import org.hid4java._
import org.hid4java.event.HidServicesEvent

import collection.JavaConverters._

object DeviceManager extends HidServicesListener {

  val services = HidManager.getHidServices
  services.addHidServicesListener(this)

  def shutdown() = services.shutdown

  def getDevices : List[HidDevice] = services.getAttachedHidDevices.asScala.toList
  def getDevices(vendorId:Int, productId:Int, serialNumber:String) : List[HidDevice] = getDevices.filter( _.isVidPidSerial(vendorId, productId, serialNumber))
  def getDevices(product:String) : List[HidDevice] = getDevices.filter( _.getProduct == product )
  def getDevicesWithManufacturer(manufacturer:String) = getDevices.filter( _.getManufacturer == manufacturer )



  override def hidDeviceAttached(e:HidServicesEvent){
    println("Device attached: " + e);
    // Add serial number when more than one device with the same
    // vendor ID and product ID will be present at the same time
    // if (e.getHidDevice().isVidPidSerial(VENDOR_ID, PRODUCT_ID, null)) {
    //   sendMessage(e.getHidDevice());
    // }
  }
  override def hidDeviceDetached(e:HidServicesEvent){
    println("Device detached: " + e);
  }
  override def hidFailure(e:HidServicesEvent){
    println("HID failure: " + e);
  }

}