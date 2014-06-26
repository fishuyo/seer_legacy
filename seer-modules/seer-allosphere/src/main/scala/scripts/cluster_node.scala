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
	
	var sim = true

  Node.mode = "omni"
  val c = Cube()
  val n = 3
  val cubes = for(z <- -n to n; y <- -n to n; x <- -n to n) yield Cube().translate(Vec3(x,y,z)*3.f)

  var t = 0.f
  var scale = 1.f
	val actor = Node.systemm.actorOf(Props( new Listener()), name = "node_script")

	var publisher:ActorRef = _
	var subscriber:ActorRef = _
	Hostname() match {
		case "Thunder.local" => publisher = Node.systemm.actorOf(Props( new Simulator()), name = "simulator")
		case _ => sim = false; subscriber = Node.systemm.actorOf(Props( new Renderer), name = "renderer")
	}

	if(sim) println( "I am the Simulator!")
	else println( "I am a Renderer!")

	override def preUnload(){
		actor ! Kill
		if(publisher != null) publisher ! Kill
		if(subscriber != null) subscriber ! Kill
	}

  override def draw(){
    cubes.foreach(_.draw)
  }
  override def animate(dt:Float){
  	if(sim){
  		t += dt
  		scale = math.sin(t)
  		publisher ! scale
  	}
  	cubes.foreach(_.scale.set(scale))

  }
}

class Renderer extends Actor with ActorLogging {
  import DistributedPubSubMediator.{ Subscribe, SubscribeAck }
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
  // subscribe to the topic named "state"
  mediator ! Subscribe("state", self)
 
  def receive = {
    case SubscribeAck(Subscribe("state", None, `self`)) ⇒
      context become ready
  }
 
  def ready: Actor.Receive = {
    case f:Float =>
      ClusterScript.scale = f
  }
}

class Simulator extends Actor {
  import DistributedPubSubMediator.Publish
  // activate the extension
  val mediator = DistributedPubSubExtension(Node.systemm).mediator
 
  def receive = {
    case f:Float =>
      mediator ! Publish("state", f)
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
      // ClusterScript.scale = f
  }
}



ClusterScript