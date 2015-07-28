
package com.fishuyo.seer
package examples.actor.state

import graphics._
import spatial._
import actor._
import dynamic._
import io._
import particle._

import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import concurrent.duration._
import concurrent.Await

object AkkaConfig {
  def config(host:String="localhost", port:String="2552") = ConfigFactory.parseString(s"""
    akka {
      actor {
        provider = "akka.remote.RemoteActorRefProvider"
      }
      remote {
        enabled-transports = ["akka.remote.netty.tcp"]
        netty.tcp {
          hostname = "${host}"
          port = ${port}
        }
        compression-scheme = "zlib"
        zlib-compression-level = 1
     }
    }
    akka.actor.serializers {
      kryo = "com.twitter.chill.akka.AkkaSerializer"
    }
    akka.actor.serialization-bindings {
      "com.fishuyo.seer.examples.actor.state.State" = kryo
    }
  """)
}

class State {
  var time = 0f
  var lpos = Vec2()
  // var v = for(i <- 0 until 100) yield Vec3(0)
  var mesh:Mesh = _
}
object State extends State

object StateSyncTest extends SeerApp {
  var myhost = ""
  var myport = "2552"
  var desthost = ""
  var destport = "2553"
  if(args.length > 0) myport = args(0)
  if(args.length > 1) destport = args(1)
  if(args.length > 2) myhost = args(2)
  if(args.length > 3) desthost = args(3)
  if(myhost == "") myhost = "localhost" //Hostname()
  if(desthost == "") desthost = "localhost" //Hostname()

  val sys = ActorSystem("seer", ConfigFactory.load(AkkaConfig.config(myhost,myport)))
  System() = sys


  var remote:ActorRef = _
  val stateListener:ActorRef = sys.actorOf(Props( new StateListener()), name = "state")

  val (nx,ny) = (50,50)
  State.mesh = Plane.generateMesh(4,4,nx,ny)
  val mesh = State.mesh 
  mesh.primitive = Lines
  val spring = new SpringMesh(mesh,1f)
  for(p <- spring.particles.takeRight(nx)){  
    spring.pins += AbsoluteConstraint(p,p.position * Vec3(1,1,1) + Vec3(0,4,util.Random.float()*0.01f))
  }
  spring.updateNormals = true  
  val model = Model(spring)

  // modify material
  // model.material = new SpecularMaterial
  // model.material.color = RGBA(0f,.6f,.6f,1f)
  // model.material.loadTexture("CassowaryJack.jpg")

  var lpos = Vec2()
  var vel = Vec2()

  var initd = false

  Gravity.set(0,-5,0)
  
  Keyboard.bind("g", ()=>{ 
    if(Gravity.y == 0f) Gravity.set(0,-5,0)
    else Gravity.zero()
  })

  override def draw(){
    model.draw()
  }

  override def animate(dt:Float){

    State.time += dt 

    implicit def f2i(f:Float) = f.toInt

    if( Mouse.status() == "drag"){
      vel = (Mouse.xy() - lpos)/dt
      // println(vel)
      // s.applyForce( Vec3(vel.x,vel.y,0)*10.f)
      val r = Camera.ray(Mouse.x()*Window.width, (1f-Mouse.y()) * Window.height)
      spring.particles.foreach( (p) => {
        val t = r.intersectSphere(p.position, 0.25f)
        if(t.isDefined){
          // val p = r(t.get)
          p.applyForce(Vec3(vel.x,vel.y,0)*150f)
          // cursor.pose.pos.set(r(t.get))
        }
      })
    }
    lpos = Mouse.xy()
    State.lpos = lpos
    // simulate the spring mesh
    // this automatically recalculates normals, and updates the mesh
    if(State.time > 1){
      if(!initd){
        // model.material.loadTexture("CassowaryJack.jpg")
        initd = true
      }

      spring.animate(dt)
    }

    if( remote == null) remote = System().actorFor(s"akka.tcp://seer@${desthost}:${destport}/user/state")
    try{ remote ! State } catch { case e:Exception => print(".") }
  }


}

class StateListener extends Actor with ActorLogging {
  // import DistributedPubSubMediator.{ Subscribe, SubscribeAck }

  // val mediator = DistributedPubSubExtension(system10g).mediator
  // mediator ! Subscribe("state", self)
 
  def receive = {
    case state:State =>
      println(state.lpos)
  }
}




