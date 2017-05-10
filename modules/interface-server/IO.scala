
package com.fishuyo.seer
package interface

import com.typesafe.config._
import net.ceedubs.ficus.Ficus._

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import scala.concurrent._

import collection.mutable.HashMap



object IO {

  val ios = HashMap[String,IO]()
  def apply(name:String) = ios(name)
  def update(name:String, io:IO) = ios(name) = io

}


class IO(val name:String) {
  val sources = HashMap[String,Source[Float,Future[akka.Done]]]()
  val sinks = HashMap[String,Sink[Float,Future[akka.Done]]]()

  def close(){}
}