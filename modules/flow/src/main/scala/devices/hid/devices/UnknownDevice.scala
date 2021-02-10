package seer
package flow
package hid

class UnknownDevice(productString:String, index:Int) extends HidDeviceIO(index) {
  // lazy val productString = name
  override lazy val name = Some(productString)
  val sourceElements = List()
}