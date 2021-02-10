// package flow

// // adapted from https://github.com/meshelton/shue
// /*
//  * Created by mshelton on 6/30/17.
//  */

// object HueProtocol extends io.circe.generic.AutoDerivation {

//   //TODO(mshelton): Encode more powerful types for this
//   sealed trait Effect
//   case object colorloop extends Effect

//   sealed trait AlertMode
//   case object none extends AlertMode with Effect
//   case object lselect extends AlertMode
//   case object select extends AlertMode

//   sealed trait ColorMode
//   case object hs extends ColorMode
//   case object xy extends ColorMode
//   case object ct extends ColorMode

//   case class LightSearchRequest(deviceId: List[String])

//   case class CieColor(x: Float, y: Float) // Serialized as a list [x, y]
  
//   case class LightState(hue: Double,
//                         on: Boolean,
//                         effect: Effect,
//                         alert: AlertMode,
//                         bri: Int, // uint8 [1, 254]
//                         sat: Int, // uint16
//                         ct: Int, // uint8 [153, 500]
//                         xy: CieColor,
//                         reachable: Boolean,
//                         colormode: Option[ColorMode])

//   // TODO(mshelton): Make this an class that can make api calls to update it's state

//   case class Light(state: LightState,
//                    `type`: String,
//                    name: String,
//                    modelid: String,
//                    swversion: String,
//                    pointsymbol: Option[Map[String, String]])

//   case class SetLightState(on: Option[Boolean] = None,
//                           bri: Option[Int] = None, // [1, 254]
//                           hue: Option[Int] = None, // [0, 65535]
//                           sat: Option[Int] = None, // [0, 254]
//                           xy: Option[CieColor] = None,
//                           ct: Option[Int] = None, // [153, 500]
//                           alert: Option[AlertMode] = None,
//                           effect: Option[Effect] = None,
//                           transitiontime: Option[Int] = None, // uint16
//                           brt_inc: Option[Int] = None, // [-254, 254]
//                           sat_inc: Option[Int] = None, // [-254, 254]
//                           hue_inc: Option[Int] = None, // [-65534, 65534]
//                           ct_inc: Option[Int] = None, // [-65534, 65534]
//                           xy_inc: Option[CieColor] = None,
//                           scene: Option[String] = None // only for group state...xxx
//                           )



// }