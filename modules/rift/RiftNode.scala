
package com.fishuyo.seer

import graphics._
import spatial._
import io._

import com.oculusvr.capi.Hmd
import com.oculusvr.capi.OvrLibrary
import com.oculusvr.capi.OvrLibrary.ovrDistortionCaps._
import com.oculusvr.capi.OvrLibrary.ovrTrackingCaps._
import com.oculusvr.capi.OvrLibrary.ovrHmdCaps._
import com.oculusvr.capi.OvrVector2i
import com.oculusvr.capi.OvrVector3f
import com.oculusvr.capi.Posef
import com.oculusvr.capi.RenderAPIConfig
import com.oculusvr.capi.GLTexture
import com.sun.jna.Structure

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11._

// example SeerApp usage
//
object RiftTest extends SeerApp {
  // Window.w0 = 960 //780
  // Window.h0 = 540 //438
  val models = for(i <- -3 to 3; j <- -3 to 3; k <- -3 to 3) yield {
    val m = Cube().translate(i,j,k).scale(0.1)
    m.material.specular()
    m
  }

  override def init(){
    // com.badlogic.gdx.Gdx.graphics.setVSync(true)
    // DesktopApp.app.resize(780,438)
    // println(Rift.hmd)

    // DesktopApp.toggleFullscreen

    RenderGraph.clear
    RenderGraph += new RiftNode
  }

  override def draw(){
    FPS.print
    models.foreach(_.draw)
  }
  override def animate(dt:Float){
    // Camera.step(dt)
    // RenderGraph.roots(0).renderer.camera.nav.set( Camera.nav )
    models.foreach(_.rotate(0.01,0.01,.02))
  }

}


object RiftNode extends RiftNode
class RiftNode extends RenderNode {
  // renderer.clear = false
  renderer.camera = new ManualCamera
  renderer.scene = Scene

  DesktopApp.toggleFullscreen

  val nav0 = Nav()
  val navMove = Nav()
  Keyboard.bindNav(nav0)
  Keyboard.bindNav(navMove)

  var hmd:Hmd = initHmd()
  hmd.configureTracking(ovrTrackingCap_Orientation | ovrTrackingCap_Position | ovrTrackingCap_MagYawCorrection, 0)

  // prepare fovports
  val fovPorts = Array.tabulate(2)(eye => hmd.DefaultEyeFov(eye))
  val projections = Array.tabulate(2)(eye => Hmd.getPerspectiveProjection(fovPorts(eye), 0.0001f, 10000f, true).M)  // row major
  
  val oversampling = 1.0f
  var numFrames = 0L
  
  val eyeTextures = new GLTexture().toArray(2).asInstanceOf[Array[GLTexture]]
  Range(0, 2).foreach{ eye =>
    val header = eyeTextures(eye).ogl.Header
    header.TextureSize = hmd.getFovTextureSize(eye, fovPorts(eye), oversampling)
    header.RenderViewport.Size = header.TextureSize
    header.RenderViewport.Pos = new OvrVector2i(0, 0)
    header.API = OvrLibrary.ovrRenderAPIType.ovrRenderAPI_OpenGL
  }
  checkContiguous(eyeTextures)
  
  var framebuffers = Array.tabulate(2){eye => 
    FrameBuffer(eyeTextures(eye).ogl.Header.TextureSize.w, eyeTextures(eye).ogl.Header.TextureSize.h)
    // new FloatFrameBuffer(eyeTextures(eye).ogl.Header.TextureSize.w, eyeTextures(eye).ogl.Header.TextureSize.h)
  }
  
  for (eye <- Range(0, 2)) {
    eyeTextures(eye).ogl.TexId = framebuffers(eye).getColorBufferTexture.getTextureObjectHandle
    // eyeTextures(eye).ogl.TexId = framebuffers(eye).colorTexture.handle 
    println(f"Texture ID of eye $eye: ${eyeTextures(eye).ogl.TexId}")
  }

  val rc = new RenderAPIConfig()
  rc.Header.API = OvrLibrary.ovrRenderAPIType.ovrRenderAPI_OpenGL
  rc.Header.BackBufferSize = hmd.Resolution
  rc.Header.Multisample = 1 // does not seem to have any effect
  
  val distortionCaps = 
    // ovrDistortionCap_NoSwapBuffers | // this flag is effectively hard coded into modified oculus library
    //ovrDistortionCap_FlipInput |
    ovrDistortionCap_TimeWarp |
    //ovrDistortionCap_Overdrive |
    //ovrDistortionCap_HqDistortion |
    ovrDistortionCap_Chromatic | 
    ovrDistortionCap_Vignette
  
  // configure rendering
  val eyeRenderDescs = hmd.configureRendering(rc, distortionCaps, fovPorts)
  
  // hmdToEyeViewOffset is an Array[OvrVector3f] and is needed in the GetEyePoses call
  // we can prepare this here. Note: must be a contiguous structure
  val hmdToEyeViewOffsets = new OvrVector3f().toArray(2).asInstanceOf[Array[OvrVector3f]]
  Range(0, 2).foreach { eye =>
    hmdToEyeViewOffsets(eye).x = eyeRenderDescs(eye).HmdToEyeViewOffset.x
    hmdToEyeViewOffsets(eye).y = eyeRenderDescs(eye).HmdToEyeViewOffset.y
    hmdToEyeViewOffsets(eye).z = eyeRenderDescs(eye).HmdToEyeViewOffset.z
  }
  checkContiguous(hmdToEyeViewOffsets)

  def initHmd(): Hmd = {
    Hmd.initialize()
    val hmd = Hmd.create(0) //Hmd.createDebug(ovrHmd_DK1)
    if (hmd == null) {
      println("Oculus Rift HMD not found.")
      System.exit(-1)
    }
    
    // set hmd caps
    val hmdCaps = ovrHmdCap_LowPersistence | 
                  //ovrHmdCap_NoVSync | 
                  ovrHmdCap_ExtendDesktop | 
                  ovrHmdCap_DynamicPrediction 
    hmd.setEnabledCaps(hmdCaps)
    hmd
  }
  def closeHmd(){
    if(hmd != null) hmd.destroy()
  }

  override def resize(vp:Viewport){
  //   // framebuffers = Array.tabulate(2){eye => 
  //   //   FrameBuffer(eyeTextures(eye).ogl.Header.TextureSize.w, eyeTextures(eye).ogl.Header.TextureSize.h)
  //   //   // new FloatFrameBuffer(eyeTextures(eye).ogl.Header.TextureSize.w, eyeTextures(eye).ogl.Header.TextureSize.h)
  //   // }
    
  //   // for (eye <- Range(0, 2)) {
  //   //   eyeTextures(eye).ogl.TexId = framebuffers(eye).getColorBufferTexture.getTextureObjectHandle
  //   //   // eyeTextures(eye).ogl.TexId = framebuffers(eye).colorTexture.handle 
  //   //   println(f"Texture ID of eye $eye: ${eyeTextures(eye).ogl.TexId}")
  //   // }
  }

  override def animate(dt:Float){
    val hswState = hmd.getHSWDisplayState()
    if (hswState.Displayed != 0) hmd.dismissHSWDisplay()

    navMove.step(dt)
    nav0.step(dt)
    renderer.animate(dt)
  }

  override def render(){
    // bindTarget()

    // inputs.zipWithIndex.foreach { case(input,idx) => 
    //   input.bindBuffer(idx) 
    //   renderer.shader.uniforms("u_texture"+idx) = idx
    // }

    // start rift frame timing
    val frameTiming = hmd.beginFrame(numFrames.toInt)

    // get tracking by getEyePoses
    val headPoses = hmd.getEyePoses(numFrames.toInt, hmdToEyeViewOffsets)
    checkContiguous(headPoses)

    val headPosesToUse = headPoses
    
    // val nextFrameDelta = (frameTiming.NextFrameSeconds-frameTiming.ThisFrameSeconds)*1000
    // val scanoutMidpointDelta = (frameTiming.ScanoutMidpointSeconds-frameTiming.ThisFrameSeconds)*1000
    // val timewarpDelta = (frameTiming.TimewarpPointSeconds-frameTiming.ThisFrameSeconds)*1000
    //println(f"delta = ${frameTiming.DeltaSeconds*1000}%9.3f thisFrame = ${frameTiming.ThisFrameSeconds*1000}%9.3f    nextFrameΔ = ${nextFrameDelta}%9.3f    timewarpΔ =  ${timewarpDelta}%9.3f    scanoutMidpointΔ = ${scanoutMidpointDelta}%9.3f")


    //for each eye
    for (i <- 0 until 2) {
      val eye = hmd.EyeRenderOrder(i)
      val P = projections(eye)
      val pose = headPosesToUse(eye)
      
      val matPos = Vec3(-pose.Position.x, -pose.Position.y, -pose.Position.z)
      val quat = Quat(pose.Orientation.w,pose.Orientation.x, pose.Orientation.y, pose.Orientation.z)// RH

      // Camera.nav.pos.set(matPos)
      // Camera.nav.quat.set(quat)
      navMove.quat.set(nav0.quat*quat)
      renderer.camera.nav.quat.set(nav0.quat*quat)
      renderer.camera.nav.pos.set(navMove.pos)
      renderer.camera.asInstanceOf[ManualCamera].projection.set(P).tra()
      renderer.camera.update
      
      framebuffers(eye).begin()
      renderer.render()
      framebuffers(eye).end()
    }
    
    //rift endframe
    hmd.endFrame(headPosesToUse, eyeTextures)

    numFrames += 1

    // unbindTarget()
  }


  def checkContiguous[T <: Structure](ts: Array[T]) {
    val first = ts(0).getPointer
    val size = ts(0).size
    val secondCalc = first.getPointer(size)
    val secondActual = ts(1).getPointer.getPointer(0)
    assert(secondCalc == secondActual, "array must be contiguous in memory.")
  }

}


      