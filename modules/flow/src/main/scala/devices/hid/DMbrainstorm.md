
val device = Device("Name")
device.X >> someSink 

// script gets deviceIO (template)
	// which knows what streams are offered - no real device connection
	// able to materializes streams (even when not connected)
	// implies a broadcast stream can connect to it to power streams
	// if device connected --> DM open device --> connect
	// on device connect --> open? --> connect device bytes to streams..

// DM (hid) (all hid related code here?)
	// listens to device connections
	// holds device infos (showing connected devices)
	// opens devices, reads data
	// holds open device instances

// Device
	// Option[HidDevice]
	// open / close..
	// byte stream actor..
	// broadcast bytes ---> sink.ignore..
	// keep track of DeviceTemplates using it.. close if none?


1. a new DeviceIO gets corresponding Device from DM (or creates)
2. DeviceIO uses Device byteStream for its sources.. (even if not open or connected)
3a. DeviceIO requests Device to be opened (on stream materialize?)(dev.openWhenAvlbe)
3b. (easier) Device opens if possible / registers connect event to open and read bytes
4. 



#simplify

- devices ..
- script needs device..
	- return deviceio representing device..(ref to parent device connection)
	- backed by device connection. open / listen for connections
		- connect -> open..
		- disconnect -> close children deviceIOs?
		- track children, when child closes remove from list, if empty, close device
-

##DM
- monitors device connections (available devices)
- stream for connection events

##Mapping Script
- requests device instance for mappings
	- returns deviceIO 
