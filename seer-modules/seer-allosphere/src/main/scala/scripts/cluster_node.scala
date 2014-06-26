import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import allosphere.livecluster.Node


import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import akka.actor._
import akka.contrib.pattern.DistributedPubSubExtension
import akka.contrib.pattern.DistributedPubSubMediator

object ClusterScript extends SeerScript{

  Node.mode = "omni"
  val c = Cube()
  val n = 3
  val cubes = for(z <- -n to n; y <- -n to n; x <- -n to n) yield Cube().translate(Vec3(x,y,z)*3.f)

  var scale = 1.f
	val actor = Node.systemm.actorOf(Props( new Listener()), name = "node_script")

	override def preUnload(){
		actor ! Kill
	}

  override def draw(){
    cubes.foreach(_.draw)
  }
  override def animate(dt:Float){
  	cubes.foreach(_.scale.set(scale))
  }
}


class Listener extends Actor {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("io", self)
 
  def receive = {
    case SubscribeAck(Subscribe("io", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case f:Float =>
      ClusterScript.scale = f
  }
}



ClusterScript