

// package flow

// import akka.actor._
// import akka.stream._
// import akka.stream.scaladsl._

// import seer.spatial.Vec3
// import seer.spatial.Ray
// // import phasespace.Glove

// import concurrent.ExecutionContext.Implicits.global

// class PhasespaceIO extends IO {

//   def state = Phasespace.source
//   def markers = state.map(_.markers)
//   def marker(id:Int) = state.map(_.markers(id))
//   def leftGlove = state.map(_.leftGlove)
//   def rightGlove = state.map(_.rightGlove)
//   def headPosition = state.map(_.headPosition)

//   override def sources:Map[String,Source[Any,akka.NotUsed]] = Map(
//     "state" -> state,
//     "markers" -> markers,
//     "leftGlove" ->  leftGlove,
//     "rightGlove" -> rightGlove
//   )
// }


// class PhasespaceState {
//   val markers = Array.fill(phasespace.Phasespace.maxMarkerCount)(Vec3())
//   val rightGlove = new Glove(0)
//   val leftGlove = new Glove(8)
//   val headPosition = Vec3()

//   def gloves = Seq(rightGlove,leftGlove)
// }

// object Phasespace {

//   implicit val system = System()
//   implicit val materializer = ActorMaterializer()

//   var streamActor:Option[ActorRef] = None
//   private val streamSource = Source.actorRef[PhasespaceState](bufferSize = 0, OverflowStrategy.fail)
//                                     .mapMaterializedValue( (a:ActorRef) => streamActor = Some(a) )
  
//   // materialize BroadcastHub for dynamic usage as source, which drops previous frame
//   val source:Source[PhasespaceState,akka.NotUsed] = streamSource.toMat(BroadcastHub.sink)(Keep.right).run().buffer(1,OverflowStrategy.dropHead) 
//   .watchTermination()((_, f) => {f.onComplete {  // for debugging
//     case t => println(s"Phasespace source terminated: $t")
//   }; akka.NotUsed })

  
//   val state = new PhasespaceState
//   var connected = false

//   private var scheduled:Option[Cancellable] = None

//   connect()
  
//   def connect(){
//     if( connected ) return
//     phasespace.Phasespace.connect("192.168.0.99")
//     // phasespace.Phasespace.openPlaybackFile("../phasespaceJNI/core/gloves.txt")
//     scheduleUpdate()
//     connected = true
//   }

//   def disconnect(){
//     if(!connected) return
//     stopUpdate()
//     phasespace.Phasespace.disconnect()
//     connected = false
//   }

//   def update(){
//     phasespace.Phasespace.update()
//     // phasespace.Phasespace.updatePlay()
//     phasespace.Phasespace.getMarkers(state.markers)
//     state.leftGlove.update(0.015f)
//     state.rightGlove.update(0.015f)
//     state.headPosition.set(state.markers(17)) //
    
//     streamActor.foreach(_ ! state)
//   }

//   def scheduleUpdate(){
//     import concurrent.duration._
//     import concurrent.ExecutionContext.Implicits.global
//     if(scheduled.isDefined) return
//     scheduled = Some( system.scheduler.schedule(0 seconds, 15 millis)(update) )
//   }

//   def stopUpdate(){
//     scheduled.foreach(_.cancel)
//     scheduled = None
//   }



// }

// object Led extends Enumeration {
//   val Pinky, Ring, Middle, Index,
//     DorsalPinky, DorsalIndex, ThumbProximal, Thumb = Value

//   def fingerTips = this.values.take(4)
// }

// class Glove(val markerOffset:Int) {
//   import Led._

//   val seen = Array.fill(8)(false)
//   val pos = Array.fill(8)(Vec3())
//   val oldPos = Array.fill(8)(Vec3())
//   val centroid = Vec3()
//   val thumbDir = Vec3()
//   val backDir = Vec3()

//   val pinchPos = Array.fill(4)(Vec3())
//   val pinchVel = Array.fill(4)(Vec3())
//   val wasPinched = Array.fill(4)(false)
//   val pinched = Array.fill(4)(false)
//   val pinchOn = Array.fill(4)(false)
//   val pinchOff = Array.fill(4)(false)

//   val pinches = Array.fill(4)(0)
//   val dtLastPinch = Array.fill(4)(0f)

//   var pinchThresh = 0.04f
//   var doublePinchSpeed = 0.5f

//   def isPinchOn(led:Led.Value):Boolean = pinchOn(led.id)
//   def isPinched(led:Led.Value):Boolean = pinched(led.id)
//   def isPinchOff(led:Led.Value):Boolean = pinchOff(led.id)

//   def getPinchTranslation(led:Led.Value):Vec3 = {
//     if(led.id >= 4 || !pinched(led.id)) return Vec3()
//     ((pos(led.id) + pos(Thumb.id)) * 0.5) - pinchPos(led.id)
//   }

//   def update(dt:Float) = {
//     val sum = Vec3()
//     var numSeen = 0
    
//     for(i <- 0 until 8){
//       oldPos(i).set(pos(i))
//       seen(i) = phasespace.Phasespace.getMarker(i + markerOffset, pos(i))
//       if(seen(i)){
//         sum += pos(i)
//         numSeen += 1
//       }
//     }
//     if(numSeen > 0) centroid.set(sum / numSeen)

//     // update thumb and back of hand vectors
//     if(seen(Thumb.id) && seen(ThumbProximal.id)) 
//       thumbDir.set((pos(Thumb.id) - pos(ThumbProximal.id)).normalize)

//     if( seen(DorsalIndex.id) && seen(DorsalPinky.id) )
//       backDir.set((pos(DorsalPinky.id) - pos(DorsalIndex.id)).normalize)

//     // update pinched gestures
//     for(i <- 0 until 4){

//       pinchOn(i) = false
//       pinchOff(i) = false

//       // only if thumb and finger both seen
//       if( seen(Thumb.id) && seen(i)){
//         wasPinched(i) = pinched(i)
//         pinched(i) = (pos(i) - pos(Thumb.id)).mag() < pinchThresh
        
//         // new pinch
//         if(!wasPinched(i) && pinched(i)){
//           pinchPos(i) = (pos(i)+pos(Thumb.id)) * 0.5
//           pinchOn(i) = true

//           if( dtLastPinch(i) < doublePinchSpeed) pinches(i) += 1
//           else pinches(i) = 1
//           dtLastPinch(i) = 0.0f

//         }else if( pinched(i) ){  // currently pinched
//             pinchVel(i).lerpTo( (pos(i) - oldPos(i)) / dt, 0.2f )

//         }else if( wasPinched(i) ){ // no longer pinched
//             pinchOff(i) = true
//         }
//       }
//       if( dtLastPinch(i) >= doublePinchSpeed) pinches(i) = 0
//       dtLastPinch(i) += dt

//     }

//   }
// }

// object Intersect {

//   def ray2Allosphere(ray:Ray) = {
//     val t = allosphere(ray)
//     Ray(Vec3(),ray(t.get).normalized)
//   }

//   // intersect cylinder positioned at origin oriented with Z axis
//   def cylinderXY(ray:Ray, radius:Float):Float = {
//     val d = ray.d
//     val o = ray.o

//       val A = d.x*d.x + d.y*d.y;
//       val B = 2.0f * (d.x*o.x + d.y*o.y);
//       val C = (o.x*o.x + o.y*o.y) - radius*radius;
//       val det = B*B - 4*A*C;

//       if( det > 0.0f ){
//         val t1 = (-B - Math.sqrt(det).toFloat ) / (2.0f*A);
//         if ( t1 > 0.0f ) return t1;
//         val t2 = (-B + Math.sqrt(det).toFloat ) / (2.0f*A);
//         if ( t2 > 0.0f ) return t2;

//       } else if ( det == 0.0f ){
//         val t = -B / (2.0f*A);
//         if ( t > 0.0f ) return t;
//       }
//     return -1.0f; 
//   }

//   // intersect with the capsule shape of the AlloSphere
//   // assumes the ray is originating near the center of the sphere
//   // check this..
//   def allosphere(ray:Ray):Option[Float] = {
//     val radius = 4.842f;
//     val bridgeWidth2 = 2.09f / 2.0f;

//     // intersect with bridge cylinder
//     val t = cylinderXY( ray, radius );

//     // if no intersection intersect with appropriate hemisphere
//     if( t == -1.0f){
//       if(ray.d.z < 0.0f) return ray.intersectSphere( Vec3(0,0,-bridgeWidth2), radius);
//       else return ray.intersectSphere( Vec3(0,0,bridgeWidth2), radius);
//     }

//     val p = ray(t);
//     if( p.z < -bridgeWidth2){
//       return ray.intersectSphere( Vec3(0,0,-bridgeWidth2), radius);
//     } else if( p.z > bridgeWidth2 ){
//       return ray.intersectSphere( Vec3(0,0,bridgeWidth2), radius);
//     } else return Some(t);
//   }

// }

