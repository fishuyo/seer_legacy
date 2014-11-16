
package com.fishuyo.seer
package allosphere

import graphics._
import dynamic._
import spatial._
import io._
import util._

import allosphere.actor._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20

import java.io._
import collection.mutable.ArrayBuffer
import collection.mutable.Map

import de.sciss.osc.Message

object OmniTest extends SeerApp { //extends OmniApp {

	val loader = ScriptLoader("scripts/omnitest.scala")
  // val script = loader.script //getScript()

  var node:OmniStereoRenderNode = _

  override def init(){
    node = new OmniStereoRenderNode
    node.scene = Scene
    node.camera = Camera
    RenderGraph.roots.clear
    RenderGraph.roots += node
  }

  override def draw(){
    // Cube().draw
  }
  
  // override def doOmniDraw(){
  //   // Shader("omni").begin
  //   // omni.uniforms(omniShader);

  //   // if(script.isDefined) script.get.draw()
  //   // omni.renderFace(0) = true
  //   // omni.renderFace(1) = false
  //   // omni.renderFace(2) = true
  //   // omni.renderFace(3) = false
  //   // omni.renderFace(4) = true
  //   // omni.renderFace(5) = false
  // Camera.nav.pos.set(0,0,0)

  //   Cube().draw
    
  //   // Shader("omni").end
  // }

  // override def init(){
    // super.init()
    // loader = new SeerScriptLoader("scripts/omnitest.scala")
  // }

  override def animate(dt:Float){
    // Scene.remove(this)
    // if(script.isDefined) script.get.animate(dt)
  }
}
