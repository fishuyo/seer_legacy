
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

object OmniTest extends OmniApp {

	var loader:SeerScriptLoader = null

  override def onDrawOmni(){
    Shader("omni").begin
    omni.uniforms(omniShader);

    if(loader.script != null) loader.script.draw()
    
    Shader("omni").end
  }

  override def init(){
    super.init()
    loader = new SeerScriptLoader("scripts/omnitest.scala")
  }

  override def animate(dt:Float){
    if(loader.script != null) loader.script.animate(dt)
  }
}
