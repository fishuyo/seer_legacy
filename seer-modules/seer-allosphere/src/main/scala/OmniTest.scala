
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

  var renderer:OmniCapture = _ //StereoRenderer = _

  override def init(){
    RenderGraph.roots.clear
    renderer = new OmniCapture //new OmniStereoRenderer
    renderer.scene = Scene
    renderer.camera = Camera
    val node = new RenderNode(renderer)
    RenderGraph.roots += node

    val node2 = new RenderNode(new OmniRender(renderer.omni))
    node.outputTo(node2)

    // val comp = new CompositeNode
    // node2.outputTo(comp)
    // RenderGraph.roots(0).outputTo(comp)
    // comp.outputTo(ScreenNode)
    
    // val fb = new FeedbackNode(0.98, 0.2)
    // node2.outputTo(fb)
    // fb.outputTo(ScreenNode)
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
