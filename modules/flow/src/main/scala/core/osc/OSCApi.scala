// package flow

// import de.sciss.osc.Message
// import java.net.SocketAddress
// import java.net.InetSocketAddress

// import julienrf.json.derived._
// import play.api.libs.json._

// import protocol.Mapping

// /**
//   * The OSCApi exposes functionality for controlling the flow server through an OSC namespace
//   */
// object OSCApi {

//   /** Starts listening for osc api message on port provided */
//   def listen(port:Int) = OSCManager() ! OSCManagerActor.Bind(port, handler)

//   val handler:OSC.OSCHandler = {
    
//     /** AppManager */
//     case ( Message("/handshake", name:String), addr) => 
//       println(s"OSCApi handshake: $name $addr")
//       val hostname = addr.asInstanceOf[InetSocketAddress].getHostName
//       AppManager.handshake(name, hostname, 12001)
//     case ( Message("/handshake", name:String, port:Int), addr) => 
//       println(s"OSCApi handshake: $name $addr $port")
//       val hostname = addr.asInstanceOf[InetSocketAddress].getHostName
//       AppManager.handshake(name, hostname, port)
//     case ( Message("/handshake", name:String, address:String, port:Int), addr) => 
//       println(s"OSCApi handshake: $name $addr $address $port")
//       AppManager.handshake(name, address, port)
    
//     case ( Message("/handshakeConfig", config:String), addr) =>
//       println(s"OSCApi handshakeConfig: $config")
//       val hostname = addr.asInstanceOf[InetSocketAddress].getHostName
//       AppManager.handshakeConfig(config, hostname, 12001)

//     case ( Message("/handshakeConfig", config:String, port:Int), addr) =>
//       println(s"OSCApi handshakeConfig: $config")
//       val hostname = addr.asInstanceOf[InetSocketAddress].getHostName
//       AppManager.handshakeConfig(config, hostname, port)

//     case ( Message("/disconnectApplication", name:String), addr) => 
//       println(s"OSCApi disconnectApplication: $name")
//       AppManager.close(name)
      
//     case ( Message("/close", name:String), addr) => 
//       println(s"OSCApi close app: $name")
//       AppManager.close(name)

//     // Mappings
//     case ( Message("/runMapping", name:String, code:String), addr) => 
//       MappingManager.run(Mapping(name,code))
      
//     case msg => println(msg)
  
//   }

// }
