// package flow
// package ijs

// import julienrf.json.derived._
// import play.api.libs.json._
// import play.api.libs.functional.syntax._

// import akka.actor._
// import akka.stream._
// import akka.stream.scaladsl._
// import akka.http.scaladsl.model.ws.TextMessage


// object InterfaceWSActor {

//   // sealed trait Msg
//   case class Msg(`type`:String, address:String, typetags:String, parameters:Seq[JsValue]) //extends Msg
//   implicit val msgFormat = oformat[Msg]()

//   def props(out:ActorRef, name:String, request:String) = Props(new InterfaceWSActor(out,name,request))
// }

// class InterfaceWSActor(out:ActorRef, name:String, request:String) extends Actor with ActorLogging {

//   import InterfaceWSActor._

//   val io = Interface(name)
//   io.sinkActors += self  //TODO remove on ws close

//   val index = io.sinkActors.size - 1
//   io.sync(index)

//   def receive = {
//     case TextMessage.Strict(msg) if msg == "keepalive" => ()
//     case TextMessage.Strict(msg) => 
//       // println(msg)
//       val message = Json.parse(msg).as[Msg]
//       message match {
//         case Msg("osc", addr, tt, params) => 
//           // println(s"OSC $addr $tt $params")
//           val vs = params.zip(tt).map { 
//             case (p,'f') => p.as[Float]
//             case (p,'i') => p.as[Int]
//             case (p,'s') => p.as[String]
//             case (p,t) => println(s"InterfaceWSActor: Unhandled type $t")
//           }
//           if(vs.length == 1) io.sourceActor.foreach(_ ! (addr.tail, vs.head))
//           else io.sourceActor.foreach(_ ! (addr.tail, vs))
//         case m => println(m)
//       }

//     case (name:String, value:Float) => 
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "f", Seq(JsNumber(value)))).toString)
//     case (name:String, value:Double) => 
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "f", Seq(JsNumber(value)))).toString)
//     case (name:String, value:Int) => 
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "f", Seq(JsNumber(value)))).toString)
//     case (name:String, value:Seq[Float]) => 
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "f"*value.length, value.map(JsNumber(_)))).toString)
//     case (name:String, value:(Float,Float)) =>
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "ff", Seq(JsNumber(value._1), JsNumber(value._2)))).toString)
//     case (name:String, value:String) =>
//       out ! TextMessage(Json.toJson(Msg("osc", "/"+name, "s", Seq(JsString(value)))).toString)
//     case m => println(s"InterfaceWSActor unhandled msg: $m")
//   }
// }


