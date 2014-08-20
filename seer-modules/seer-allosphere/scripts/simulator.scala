
import com.fishuyo.seer._

import allosphere._
import allosphere.actor._

import graphics._
import dynamic._
import spatial._
import io._
import particle._

import allosphere.livecluster.Node

import collection.mutable.ArrayBuffer

import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

import ClusterSystem.{ system, system10g }
// import ClusterSystem.{ test1 => system, test1_10g => system10g }

var vel = Vec2()
var ray = Camera.ray(0,0)

object SimulatorScript extends SeerScript{
  
  var t = 0.f

  val controllerStateListener:ActorRef = system.actorOf(Props( new ControllerListener()), name = "controllerlistener")

  var statePublisher:ActorRef = system10g.actorOf(Props( new StatePublisher()), name = "statepublisher")


  val n = 40
  val mesh = Plane.generateMesh(30,30,n,n,Quat.up)
  mesh.primitive = Lines
  val model = Model(mesh)
  model.material = Material.specular
  model.material.color = RGB(0,0.5,0.7)
  // mesh.vertices.foreach{ case v => v.set(v.x,v.y+Random.float(-1,1).apply()*0.05*(v.x).abs,v.z) }
  mesh.vertices.foreach{ case v => v.set(v.x,v.y+math.sin(v.x*v.z)*0.1,v.z) }
  val fabricVertices0 = mesh.vertices.clone

  val fabric = new SpringMesh(mesh,1.f)
  fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles(n), fabric.particles(n).position)
  // fabric.pins += AbsoluteConstraint(fabric.particles(0), fabric.particles(0).position)
  fabric.pins += AbsoluteConstraint(fabric.particles.last, fabric.particles.last.position)
  Gravity.set(0,0,0)
  mesh.primitive = Triangles

  var doRay = false

  override def preUnload(){
    controllerStateListener ! PoisonPill
    statePublisher ! PoisonPill
  }

  override def animate(dt:Float){   
    t += dt

    if( doRay ){
      
      fabric.particles.foreach( (p) => {
        val hit = ray.intersectSphere(p.position, 0.25f)
        if(hit.isDefined){
          p.applyForce(Vec3(vel.x,vel.y,0)*150.f)
          // cursor.pose.pos.set(ray(hit.get))
        }
      })
      doRay = false
    }

    fabric.animate(dt)
    statePublisher ! mesh
    statePublisher ! Camera.nav
    statePublisher ! t
  }
}



class StatePublisher extends Actor {
  import DistributedPubSubMediator.Publish
  val mediator = DistributedPubSubExtension(system10g).mediator
 
  // val a = new Array[Float](30000)
  def receive = {
    case f:Float =>
      mediator ! Publish("state", f)
    case n:Nav => 
      mediator ! Publish("state", Array(n.pos.x,n.pos.y,n.pos.z,n.quat.w,n.quat.x,n.quat.y,n.quat.z) )
    case m:Mesh =>
      val numV = m.gdxMesh.get.getNumVertices
      val sizeV = m.gdxMesh.get.getVertexSize / 4 // number of floats per vertex
      val verts = new Array[Float](numV*sizeV)
      m.gdxMesh.get.getVertices(verts)
      mediator ! Publish("state", verts)
      // mediator ! Publish("state", m.vertices.flatMap( (v) => List(v.x,v.y,v.z)).toArray )
  }
}

class ControllerListener extends Actor {

  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(system).mediator

  mediator ! Subscribe("controllerState", self)
 
  def receive = {
    case SubscribeAck(Subscribe("controllerState", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case a:Array[Float] if a.length == 7 => 
      Camera.nav.pos.set(a(0),a(1),a(2))
      Camera.nav.quat.set(a(3),a(4),a(5),a(6))

    case a:Array[Float] if a.length == 8 =>
      SimulatorScript.doRay = true 
      vel.set(a(0),a(1))
      ray.o.set(a(2),a(3),a(4))
      ray.d.set(a(5),a(6),a(7))

  }
}




SimulatorScript
