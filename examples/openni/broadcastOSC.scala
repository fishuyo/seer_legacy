

package com.fishuyo.seer
package examples.openni

import graphics._
import spatial._
import io._
import util._
import openni._

import de.sciss.osc._

import org.lwjgl.opengl.GL11

import collection.mutable.Map

object BroadcastOSC extends SeerApp {

  var inPort = 8001
  var outPort = 8000
  var address = "localhost"

  val clients = Map[String,OSCSend]()

  override def parseArgs(args:Array[String]){
    if(args.length > 0) address = args(0)
    if(args.length > 1) outPort = args(1).toInt
    if(args.length > 2) inPort = args(2).toInt
  }

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true
  // OpenNI.pointCloudEach = true
  OpenNI.makeDebugImage = false

  OSC.clear()
  OSC.disconnect()
  OSC.listen(inPort)
  // OSC.connect(address, outPort)



  OSC.bindp {
    case Message("/clear") => println("clearing client list"); clients.values.foreach( _.disconnect); clients.clear
    case Message("/handshake") => 
      val ip = OSC.client.asInstanceOf[java.net.InetSocketAddress].getHostString
      println(s"Received /handshake from $ip")
      if(clients.contains(ip)) clients(ip).disconnect
      val send = new OSCSend 
      send.connect(ip, outPort)
      clients(ip) = send
    case m => println(m)
    case _ => ()
  }

  // point cloud mesh
  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480 

  val meshes = for(i <- 0 until 4) yield new Mesh()
  meshes.foreach { case m =>
    m.primitive = Points 
    m.maxVertices = 640*480
  }

  val model = Model(mesh)
  val models = meshes.map(Model(_))

  val torsos = for(i <- 0 until 6) yield Sphere().scale(0.1)
  val hands = for(i <- 0 until 6) yield (Sphere().scale(0.05),Sphere().scale(0.05))
  val colors = Array(RGB(1,0,0),RGB(0,1,0),RGB(0,0,1),RGB(1,1,0),RGB(0,1,1),RGB(1,0,1))
  for(i <- 0 until 6){
    torsos(i).material.color = colors(i)
    hands(i)._1.material.color = colors(i)
    hands(i)._2.material.color = colors(i)
  }


  override def draw(){
    com.badlogic.gdx.Gdx.gl.glEnable( 0x8642 ); // enable gl_PointSize ???
    GL11.glEnable(GL11.GL_LINE_SMOOTH);
    GL11.glEnable(GL11.GL_POINT_SMOOTH);

    FPS.print
    model.draw()
    torsos.foreach(_.draw())
    hands.foreach { case (l,r) => l.draw; r.draw }
    // models.foreach(_.draw)
  }

  override def animate(dt:Float){
    try{
      val senders = clients.values
      senders.foreach(_.startBundle())
      // OSC.startBundle()
      var count = 0
      for( id <- 0 until 6){
        torsos(id).pose.pos.set(0,0,100)
        hands(id)._1.pose.pos.set(0,0,100)
        hands(id)._2.pose.pos.set(0,0,100)
      }
      for( id <- 0 until 6){
        val s = OpenNI.getSkeleton(id)
        if(s.tracking){

          s.updateJoints()
          s.updateBones()
          // s.joints.foreach { case (j,v) => OSC.send(s"/${count+1}/pos/$j",v.x,v.y,v.z)}
          val v = s.joints("torso")
          senders.foreach(_.send(s"/${count+1}/pos/torso", v.x,v.y,v.z))
          val lyaw = s.bones("l_radius").quat.toEulerVec().y
          val ryaw = s.bones("r_radius").quat.toEulerVec().y
          senders.foreach(_.send(s"/${count+1}/yaw/l_arm", lyaw))
          senders.foreach(_.send(s"/${count+1}/yaw/r_arm", ryaw))

          // s.joints.foreach { case (j,v) => senders.foreach(_.send(s"/${count+1}/pos/$j",v.x,v.y,v.z))}
          // s.vel.foreach { case (j,v) => senders.foreach(_.send(s"/${count+1}/vel/$j",v.mag))}
          torsos(count).pose.pos.set(s.joints("torso"))
          hands(count)._1.pose.pos.set(s.joints("l_hand"))
          hands(count)._2.pose.pos.set(s.joints("r_hand"))
          count += 1
        } 
      }
      senders.foreach(_.endBundle())
      // OSC.endBundle()

      // update point cloud
      mesh.clear
      mesh.vertices ++= OpenNI.pointMesh.vertices
      mesh.update

    } catch { case e:Exception => println(e) }
  }

}

