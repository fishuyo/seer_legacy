
package com.fishuyo.seer
package example.video

import graphics._
import video._
import io._

// import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

object VideoPlayerExample extends SeerApp {

  var inited = false

  val quad = Plane()
  var texture:GdxTexture = _
  var player:VideoPlayer2 = null 

  override def init(){

    // player = new VideoPlayer("/Users/fishuyo/Desktop/windmill.mov")
    // player = new VideoPlayer("/Users/fishuyo/Desktop/IMG_3056.MOV")
    // player = new VideoPlayer2("/Users/fishuyo/Desktop/em_trampoline.mp4")
    player = new VideoPlayer2("/Users/fishuyo/Desktop/water.mov")
    // player = new VideoPlayer2("/Users/fishuyo/Movies/trees/trees.mov")

    texture = Texture(player.pixmap)
    quad.material = Material.basic
    quad.material.texture = Some(texture)
    quad.material.textureMix = 1f
    inited = true

    quad.scale(1,-player.height*1f/player.width,1)
  }

  override def draw(){
    FPS.print

    quad.draw
  }

  override def animate(dt:Float){
    if(!inited) init()

    player.animate(dt)
    texture.gdxTexture.draw(player.pixmap,0,0)
  }

  Keyboard.clear
  Keyboard.use 
  Keyboard.bind("p", ()=>{ player.togglePlaying() })


}
