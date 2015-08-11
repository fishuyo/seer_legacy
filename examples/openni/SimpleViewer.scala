

package com.fishuyo.seer
package examples.openni

import graphics._
import openni._

import com.fishuyo.seer.openni._

import com.badlogic.gdx.graphics.{Texture => GdxTexture}
import com.badlogic.gdx.graphics.Pixmap

import java.nio.ByteBuffer

object SimpleViewer extends SeerApp {

  var inited = false

  OpenNI.initAll()
  OpenNI.start()
  OpenNI.pointCloud = true

  // val stickman = new StickMan(OpenNI.getSkeleton(1))

  var quad1:Model = _ 
  var quad2:Model = _
  // val dpix = new Pixmap(640,480, Pixmap.Format.RGB888)
  // val vpix = new Pixmap(640,480, Pixmap.Format.RGB888)
  var tex1:Texture = _
  var tex2:Texture = _

  val mesh = new Mesh()
  mesh.primitive = Points 
  mesh.maxVertices = 640*480
  val model = Model(mesh)

  override def init(){
    // loadShaders()

    tex1 = Texture(640,480)
    tex2 = Texture(640,480)

    quad1 = Plane().scale(1,-480f/640f,1).translate(-1.5f,0,0)
    quad2 = Plane().scale(1,-480f/640f,1).translate(1,0,0)
    quad1.material = Material.basic
    quad1.material.texture = Some(tex1)
    quad1.material.textureMix = 1f
    quad2.material = Material.basic
    quad2.material.texture = Some(tex2)
    quad2.material.textureMix = 1f

    inited = true
  }

  override def draw(){
    FPS.print

    // OpenNI.updateDepth()

    // stickman.draw

    quad1.draw
    quad2.draw

    model.draw

  }

  override def animate(dt:Float){
    if(!inited) init()

    // stickman.animate(dt)
    
   // val bb = dpix.getPixels
    tex1.data.asInstanceOf[ByteBuffer].put(OpenNI.depthBytes)
    tex1.update
    // tex1.draw(dpix,0,0)

    // val bb2 = vpix.getPixels
    tex2.data.asInstanceOf[ByteBuffer].put(OpenNI.rgbImage.buffer)
    // tex2.data.rewind
    // tex2.data = OpenNI.rgbBuffer
    tex2.update
    // tex2.draw(vpix,0,0)

    try{
      // OpenNI.updatePoints()
      mesh.clear
      mesh.vertices ++= OpenNI.pointMesh.vertices
      mesh.update
    } catch { case e:Exception => println(e) }
  }

  def loadShaders(){
    // Shader.load("rd", File("shaders/basic.vert"), File("shaders/rd_img.frag")).monitor
    // Shader.load("colorize", File("shaders/basic.vert"), File("shaders/colorize.frag")).monitor
  }

  // override def onUnload(){
    // OpenNI.disconnect
  // }

}

