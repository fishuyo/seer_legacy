
package com.fishuyo.seer
package vr

import graphics._
import spatial._
import io._

import com.badlogic.gdx.vr._
import com.badlogic.gdx.vr.VRContext._ //Eye;
// import com.badlogic.gdx.vr.VRContext.Space;
// import com.badlogic.gdx.vr.VRContext.VRControllerButtons;
// import com.badlogic.gdx.vr.VRContext.VRDevice;
// import com.badlogic.gdx.vr.VRContext.VRDeviceListener;
// import com.badlogic.gdx.vr.VRContext.VRDeviceType;

// example SeerApp usage
//
object GdxVRTest extends SeerApp {
  Window.w0 = 1280 //960 //780
  Window.h0 = 720  //540 //438
  val models = for(i <- -2 to 2; j <- -2 to 2; k <- -2 to 2) yield {
    val m = Cube().translate(i,j,k).scale(0.1)
    m.material.specular()
    m
  }

  override def init(){
    // com.badlogic.gdx.Gdx.graphics.setVSync(true)
    // DesktopApp.app.resize(780,438)
    // println(Rift.hmd)

    // DesktopApp.toggleFullscreen

    // RenderGraph.clear
    RenderGraph += GdxVRNode
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


// object GdxVRNode extends GdxVRNode
object GdxVRNode extends RenderNode with VRDeviceListener {

  val context = new VRContext()

  // renderer.clear = false
  renderer.camera = new ManualCamera
  renderer.scene = Scene

  // DesktopApp.toggleFullscreen


  // val nav0 = Nav()
  // val navMove = Nav()
  // Keyboard.bindNav(nav0)
  // Keyboard.bindNav(navMove)
 
  context.addListener(this)

  var forward = false
  var backward = false
  var ray = false
  var vel = 0f
  def controllerR = new Device(VRDeviceType.Controller,0) //:VRContext#VRDevice = controller(0)
  def controllerL = new Device(VRDeviceType.Controller,1) //:VRContext#VRDevice = controller(1)
  def hmd = new Device(VRDeviceType.HeadMountedDisplay,0) //:VRContext#VRDevice = controller(1)
  def controllerRReal = new Device(VRDeviceType.Controller,0,Space.Tracker) //:VRContext#VRDevice = controller(0)
  def controllerLReal = new Device(VRDeviceType.Controller,1,Space.Tracker) //:VRContext#VRDevice = controller(1)
  def hmdReal = new Device(VRDeviceType.HeadMountedDisplay,0,Space.Tracker) //:VRContext#VRDevice = controller(1)
  // def controller(indx:Int) = {
  //   val cs = context.getDevicesByType(VRDeviceType.Controller)
  //   get(indx)
  // }

  def setWorldOffset(v:Vec3){
    val o = new com.badlogic.gdx.math.Vector3(v.x,v.y,v.z)
    context.getTrackerSpaceOriginToWorldSpaceTranslationOffset().set(o);
  }

  def getLeftTrigger() = forward
  def getRightTrigger() = ray

  override def animate(dt:Float){

    // navMove.step(dt)
    // nav0.step(dt)
    renderer.animate(dt)

    if(controllerL.device != null){
      val dir = controllerL.dir

      val p = hmd.pos
      // Camera.nav.pos.set(p.x,p.y,p.z)

      if(controllerL.button("trigger")) vel += 0.0007f
      else if(controllerL.button("touchpad")) vel -= 0.0007f
      else vel *= 0.96f

      if(vel > 0.09f) vel = 0.09f
      if(vel < -0.09f) vel = -0.09f

      val np =  dir * vel //+ Vec3(p.x,p.y,p.z)
      val o = new com.badlogic.gdx.math.Vector3(np.x,np.y,np.z)
      // context.getTrackerSpaceOriginToWorldSpaceTranslationOffset().add(o);
    }

  }

  override def render(){
   
    context.pollEvents()
    context.begin()

    context.beginEye(Eye.Left)
    var cam = context.getEyeData(Eye.Left).camera
    renderer.camera.combined.set(cam.combined)
    renderer.camera.view.set(cam.view)
    renderer.render()
    context.endEye()

    context.beginEye(Eye.Right)
    cam = context.getEyeData(Eye.Right).camera
    renderer.camera.combined.set(cam.combined)
    renderer.camera.view.set(cam.view)
    renderer.render()
    context.endEye()

    context.end()




      // val eyePose = vrProvider.vrState.getEyePose(i);
      // val matView = new Matrix4f(eyePose).invert();
      // val eyeProjection = vrProvider.vrState.getEyeProjectionMatrix(i);
      // val matMVP = eyeProjection.mul(matView);

      // val mat4 = new Array[Float](16)      
      // eyeProjection.get(mat4)

      // val pos = new Vector3f
      // eyePose.getTranslation(pos)

      // val q = new Quaternionf
      // eyePose.getNormalizedRotation(q)

      // shader.setUniformMatrix("MVP", false, matMVP);
      
      // val matPos = Vec3(-pos.x, -pos.y, -pos.z)
      // val quat = Quat(q.w, q.x, q.y, q.z)// RH

      // navMove.quat.set(nav0.quat*quat)
      // renderer.camera.nav.quat.set(nav0.quat*quat)
      // renderer.camera.nav.pos.set(matPos) //navMove.pos)
      // renderer.camera.asInstanceOf[ManualCamera].projection.set(mat4) //.tra()
      // renderer.camera.update
      
      // framebuffers(eye).begin()
      // renderer.render()
      // glBindFramebufferEXT(GL_FRAMEBUFFER_EXT, 0)
      // framebuffers(eye).end()
    // }
    
    // context.end()
    // numFrames += 1

    // unbindTarget()
  }

  override def dispose(){
    // context.dispose
  }


  // VRDeviceListener methods
  override def connected(device:VRContext#VRDevice) {
    println(device + " connected");
    // if (device.getType() == VRDeviceType.Controller && device.getModelInstance() != null)
    //   modelInstances.add(device.getModelInstance());
  }


  override def disconnected(device:VRContext#VRDevice) {
    println(device + " disconnected");
    // if (device.getType() == VRDeviceType.Controller && device.getModelInstance() != null)
    //   modelInstances.removeValue(device.getModelInstance(), true);
  }


  override def buttonPressed(device:VRContext#VRDevice, button:Int) {
    println(device + " button pressed: " + button);

    // If the trigger button on the first controller was
    // pressed, setup teleporting
    // mode.
    if (device == context.getDeviceByType(VRDeviceType.Controller)) {
    // if (device == context.getControllerByRole(VRControllerRole.LeftHand)) {
      button match {
        // case VRControllerButtons.SteamVR_Trigger => forward = true
        // case VRControllerButtons.SteamVR_Touchpad => backward = true
        case _ => ()
      }
      // controllerL = device       
    } else { //if (device == context.getControllerByRole(VRControllerRole.RightHand)) {
      button match {
        // case VRControllerButtons.SteamVR_Trigger => ray = true
        // case VRControllerButtons.SteamVR_Touchpad => backward = true
        case _ => ()
      }
      // controllerR = device       
    }
  }


  override def buttonReleased(device:VRContext#VRDevice, button:Int) {
    println(device + " button released: " + button);

    if (device == context.getDeviceByType(VRDeviceType.Controller)) {
    // if (device == context.getControllerByRole(VRControllerRole.LeftHand)) {
      button match {
        // case VRControllerButtons.SteamVR_Trigger => forward = false
        // case VRControllerButtons.SteamVR_Touchpad => backward = false
        case _ => ()
      }
    } else { //if (device == context.getControllerByRole(VRControllerRole.RightHand)) {
      button match {
        // case VRControllerButtons.SteamVR_Trigger => ray = false
        // case VRControllerButtons.SteamVR_Touchpad => backward = true
        case _ => ()
      }
    }

    
  }

}


class Device(val kind:VRDeviceType, val index:Int=0, val space:Space=Space.World){

  def context = GdxVRNode.context

  def device = {
    val ds = context.getDevicesByType(kind)
    if(index < ds.size) ds.get(index)
    else null
  }

  def connected = (device != null)

  def pos = {
    var p = device.getPosition(space)
    Vec3(p.x,p.y,p.z)
  }
  def dir = {
    var p = device.getDirection(space).nor
    Vec3(p.x,p.y,p.z)
  }
  def up = {
    var p = device.getUp(space).nor
    Vec3(p.x,p.y,p.z)
  }
  def right = {
    var p = device.getRight(space).nor
    Vec3(p.x,p.y,p.z)
  }
  def ray = Ray(pos,dir)

  def axis(indx:Int=0) = Vec2(device.getAxisX(indx),device.getAxisY(indx))

  def button(name:String) = name match {
    case "trigger" => device.isButtonPressed(VRControllerButtons.SteamVR_Trigger)
    case "touchpad" => device.isButtonPressed(VRControllerButtons.SteamVR_Touchpad)
    case _ => false
  }

  def pulse(duration:Short) = device.triggerHapticPulse(duration)

}
