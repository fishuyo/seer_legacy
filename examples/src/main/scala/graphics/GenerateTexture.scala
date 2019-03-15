
package com.fishuyo.seer
package examples.graphics

import graphics._

import fastnoise._

object GenerateTexture extends SeerApp {
  val numChannels = 1
  val image = Image(1000,1000,numChannels,1)
  var texture:Texture = _
  val quad = Plane()

  val noise = new FastNoise
  noise.SetNoiseType(FastNoise.NoiseType.Cellular)

  for( y <- 0 until image.h; x <- 0 until image.w){
    image.buffer.put(((noise.GetNoise(x,y)+1f)*128).toByte)
  }
  // for( i <- 0 until numChannels*image.w*image.h){
  //   image.buffer.put((util.Random.float()*255).toByte)
  // }

  override def init(){
    texture = Texture(image)
    quad.material = Material.basic
    quad.material.loadTexture(texture)
  }

  override def draw(){
    quad.draw
  }


}