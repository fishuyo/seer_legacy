
// // adapted from https://github.com/meshelton/shue

// package flow

// //import io.circe.generic.auto._
// import akka.actor.ActorSystem
// import akka.http.scaladsl.Http
// import akka.http.scaladsl.marshalling.Marshal
// import akka.http.scaladsl.model.HttpMethods._
// import akka.http.scaladsl.model.{HttpRequest, RequestEntity, ResponseEntity}
// import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
// import akka.stream.{ActorMaterializer, Materializer}

// import scala.concurrent.{ExecutionContext, Future}

// import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
// import io.circe.Printer

// import seer.spatial.Vec2
// import seer.actor._

// object Hue {
//   import HueProtocol._

//   implicit val system = System()
//   implicit val materializer = ActorMaterializer()
//   implicit val printer = Printer.noSpaces.copy(dropNullValues = true)

//   // hard coding hue bridge info for now.
//   // flowServer --> "JSZ6EN1Zp599wZ81IHtttzpv3vJSgUX-cZlC5EMI"
//   var ip = "192.168.1.124"
//   var id = "JSZ6EN1Zp599wZ81IHtttzpv3vJSgUX-cZlC5EMI"

//   val url = s"http://${ip}/api/$id"

//   def apiRequest[T](request: HttpRequest)(implicit um: Unmarshaller[ResponseEntity, T], ec: ExecutionContext = null, mat: Materializer): Future[T] = {
//     Http().singleRequest(request).flatMap(resp ⇒ Unmarshal(resp.entity).to[T])
//   }
  
//   import scala.concurrent.ExecutionContext.Implicits.global

//   def getLights: Future[Map[String, Light]] = apiRequest[Map[String,Light]](HttpRequest(uri = s"$url/lights"))
//   def getLight(id: Int): Future[Light] = apiRequest[Light](HttpRequest(uri = s"$url/lights/$id"))

//   def setLightState(id:Int, lightState:SetLightState): Future[Map[String, Map[String, String]]] = Marshal(lightState).to[RequestEntity]
//     .flatMap(e ⇒ apiRequest[Map[String, Map[String, String]]](HttpRequest(uri = s"$url/lights/$id/state", method = PUT, entity = e)))
  
//   def setGroupState(id:Int, lightState:SetLightState): Future[Map[String, Map[String, String]]] = Marshal(lightState).to[RequestEntity]
//     .flatMap(e ⇒ apiRequest[Map[String, Map[String, String]]](HttpRequest(uri = s"$url/groups/$id/action", method = PUT, entity = e)))

//   def setLightMapState(pos:Vec2, width:Float, lightState:SetLightState) = {
//     LightMap.lights.foreach{ case l =>
//       val d = (l.pos - pos).mag
//       if(d < width){
//         setLightState(l.id, lightState)
//       }
//     }
//   }
// }

// case class LightPos(id:Int, pos:Vec2)
// object LightMap {
// // [-3.1957667 2.0330815 3.5609382] 1
// // [-2.971905 2.06142 1.1983707] 4
// // [-2.727625 2.0553503 -1.2889802] 2
// // [0.10216379 2.0348523 2.6770852] 10
// // [0.32669067 2.0022845 0.31212902] 9
// // [0.53138804 2.0044413 -2.0563166] 3
// // [2.3       x         3.8       ] 8
// // [2.5249105 1.2420385 1.5551238] 6
// // [2.7691389 1.6528126 -1.125459] 11
//   val lights = LightPos(1, Vec2(-3.1,3.5)) ::
//             LightPos(4, Vec2(-2.9,1.2)) ::
//             LightPos(2, Vec2(-2.7,-1.2)) ::
//             LightPos(10, Vec2(0.1,2.6)) ::
//             LightPos(9, Vec2(0.3,0.3)) ::
//             LightPos(3, Vec2(0.5,-2.0)) ::
//             LightPos(8, Vec2(2.3,3.8)) ::
//             LightPos(6, Vec2(2.5,1.5)) ::
//             LightPos(11, Vec2(2.7,-1.1)) :: List()

  
// // 1  4  2
// // 10 9  3
// // 8  6  11
// }