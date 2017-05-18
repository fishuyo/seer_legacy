
package com.fishuyo.seer
package interface

import io._
import actor._

import de.sciss.osc._

import com.typesafe.config._
import net.ceedubs.ficus.Ficus._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent._

import collection.mutable.HashMap

object OSCServer { 

  implicit val system = System()
  implicit val materializer = ActorMaterializer()

  val recv = new OSCRecv
  recv.listen(12000)
  recv.bindp {
    case Message("/handshake", appName:String, config:String) =>
      val conf = ConfigFactory.parseString(config).getConfig("app").resolve
      
      val name = conf.as[Option[String]]("name").getOrElse("default")
      val port = conf.as[Option[Int]]("osc.port").getOrElse(12001)
      val sinks = conf.as[Map[String,Config]]("sinks")
      val mappings = conf.as[List[Config]]("mappings")

      val app = new AppClient
      if(IO.ios.isDefinedAt(name)) IO(name).close
      IO(name) = app

      app.osc.connect(recv.client.toString, port)

      app.sinks ++= sinks.map { 
        case (name, config) => (name, Sink.foreach( (v:Float) => app.osc.send(s"/$name",v)))
      }

      // materialize streams
      mappings.map { case conf =>
        val src = conf.as[Config]("source")
        var io = src.as[String]("io")
        var name = src.as[String]("name")
        val source = IO(io).sources(name)
        
        val snk = conf.as[Config]("sink")
        io = snk.as[String]("io")
        name = snk.as[String]("name")
        val sink = IO(io).sinks(name)
        
        source.runWith(sink)
      }

    case msg => println(msg)
  }


}


class AppClient extends IO {

  val osc = new OSCSend
  // val sinks = HashMap[String,Sink[Float,Future[akka.Done]]]()
  // val mappings = HashMap[String,Sink]()


  // def load()

  override def close() = {
    osc.disconnect
  }

}