
package seer
package video.examples

import graphics._
import video._

// import uk.co.caprica.vlcj.binding.LibVlc;
// import uk.co.caprica.vlcj.discovery.NativeDiscovery;


object VideoPlayerExample extends SeerApp {

  val video = new VideoTexture("/Users/fishuyo/Downloads/entrance.mp4")

  override def draw() = video.draw()
  override def animate(dt:Float) = video.animate(dt)
}