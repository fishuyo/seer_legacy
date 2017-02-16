
package com.fishuyo.seer
package example.video

import graphics._
import video._
import io._

object VlcPlayerExample extends SeerApp {

  var inited = false

  // val quad = Plane()
  // val quad2 = Plane()
  // var texture:Texture = _
  // var texture2:Texture = _
  // var player:VlcPlayer = new VlcPlayer("/Users/fishuyo/Desktop/fire.mov") 
  // var player2:VlcPlayer = new VlcPlayer("/Users/fishuyo/Desktop/water.mov") 
  var video:VideoTexture = _ //new VideoTexture("/Users/fishuyo/Desktop/water.mov")

  override def init(){

    video = new VideoTexture("/Users/fishuyo/Desktop/water.mov")

    // video.init()
    inited = true


  }

  override def draw(){
    FPS.print

    video.draw
    // quad.draw
    // quad2.draw
  }

  override def animate(dt:Float){
    if(!inited) init()
    else{
      // video.setRate(Mouse.x())
      // texture.data = player.frame
      // texture.update()
      // texture2.data = player2.frame
      // texture2.update()
      video.update()
    }
  }

  Keyboard.clear
  Keyboard.use 
  // Keyboard.bind("p", ()=>{ video.togglePlaying() })


}
