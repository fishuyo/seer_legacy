

package seer
package audio 

import spatial._

import collection.mutable.ListBuffer
import collection.mutable.ArrayBuffer

case class Speaker(index:Int, position:Vec3, direction:Vec3)

object AudioSpatializer {
  def stereo() = new AudioSpatializerStereo()

}

trait AudioSpatializer extends AudioSource {

  val speakers = ArrayBuffer[Speaker]()
  val sources = ListBuffer[AudioSource]()
  val listener = Pose()
  var listenerS = Pose()

  def +=(src:AudioSource) = sources += src 

  def preRender(io: AudioIOBuffer) = {}
  def render(io: AudioIOBuffer, listener:Pose): Unit = ???
  def postRender(io: AudioIOBuffer) = {}

  override def audioIO(io: AudioIOBuffer): Unit = {
    listenerS = listenerS.lerp(listener, 0.05f)
    preRender(io)
    render(io, listenerS)
    postRender(io)
  }

}