
package com.fishuyo.seer
package examples.graphics

import graphics._


object GenerateTexture extends SeerApp {

  val numChannels = 4
  val image = Image(100,100,numChannels,1)
  var texture:Texture = _
  val quad = Plane()

  for( i <- 0 until numChannels*image.w*image.h){
    image.buffer.put(util.Random.int().toByte)
  }

  override def init(){
    texture = Texture(image)
    quad.material = Material.basic
    quad.material.loadTexture(texture)
  }

  override def draw(){
    quad.draw
  }


}