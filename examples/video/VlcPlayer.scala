
package com.fishuyo.seer
package example.video

import graphics._
import video._
import io._

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

object VlcPlayerExample extends SeerApp {

  var inited = false

  val quad = Plane()
  val quad2 = Plane()
  var texture:GdxTexture = _
  var texture2:GdxTexture = _
  var player:VlcPlayer = new VlcPlayer("/Users/fishuyo/Desktop/fire.mov") 
  var player2:VlcPlayer = new VlcPlayer("/Users/fishuyo/Desktop/water.mov") 

  override def init(){
    if( player == null || player2 == null) return
    if(player.pixmap != null){
      texture = new GdxTexture(player.pixmap)
      quad.material = Material.basic
      quad.material.texture = Some(texture)
      quad.material.textureMix = 1f
      quad.scale(1,-player.height*1f/player.width,1)

      inited = true
    }
    if(player2.pixmap != null){
      texture2 = new GdxTexture(player2.pixmap)
      quad2.material = Material.basic
      quad2.material.texture = Some(texture2)
      quad2.material.textureMix = 1f
      quad2.scale(1,-player2.height*1f/player2.width,1)
      quad2.translate(2,0,0)
    }
  }

  override def draw(){
    FPS.print

    quad.draw
    quad2.draw
  }

  override def animate(dt:Float){
    if(!inited) init()
    else{
      player.setRate(Mouse.x())
      // player.animate(dt)
      texture.draw(player.pixmap,0,0)
      texture2.draw(player2.pixmap,0,0)
    }
  }

  Keyboard.clear
  Keyboard.use 
  Keyboard.bind("p", ()=>{ player.togglePlaying() })


}
