package com.fishuyo.seer
package examples.trees
import maths._
import spatial._
import graphics._
import trees._
// import particle.structures._
import io._
import io.kinect._
import dynamic._
import audio._

import util._

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Matrix4
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.GL10

import collection.mutable.ListBuffer


object Main extends App with Animatable with AudioSource {

  SimpleAppRun.loadLibs()
  Scene.push(this)
  //Scene.push(Kinect)
  
  var ground:Model = _
  var tree = new Tree() 
  tree.branch()

  var wind:com.badlogic.gdx.audio.Music = _
  Audio.push(this) 

  // var niceView = new Pose(Vec3(0.7285795f,0.f, 1.4537674f), Quat(0.9765499f, 0.113466285f, 0.18053834f, -0.029700035f))
  var niceViews = new ListBuffer[Pose]()
  var niceTrees = new ListBuffer[Array[Float]]()
  niceViews += new Pose(Vec3(0.7285795f,0.f, 1.4537674f), Quat(0.9765499f, 0.113466285f, 0.18053834f, -0.029700035f))

  var rms = 0.f

  var volume = Vec3(0)
  var color = Vec3(68.0/255.0,122.0/256.0,222.0/256.0)

  var day = Vec3(1.f,0,1.f)
  var useLocalZ = Vec3(1.f)
  var dayUniforms = Vec3(0.f,4.1f,0.f)
  var nightUniforms = Vec3(0.f,2.f,1.f)

  var move = Vec3(0.f)
  var rot = Vec3(0.f)
  var nmove = Vec3(0.f)
  var nrot = Vec3(0.f)

  val live = new Ruby("trees.rb")

  var theta = 0.f
  var dw = 0.f
  var blurDist = 0.0f

  SimpleAppRun() 

  override def init(){
    ground = OBJ("res/landscapealien.obj") //new ObjLoader().loadObj(Gdx.files.internal("src/main/scala/drone/landscapealien.obj"))
    ground.pose.quat.set(0.42112392f,-0.09659095f, 0.18010217f, -0.8836787f)
    ground.pose.pos.set(0.f,-1.3f,-.0f)
    ground.scale.set(5.f,5.f,5.f)

    val f = Gdx.files.internal("res/wind.mp3")
    wind = Gdx.audio.newMusic(f)
    wind.setVolume(0.0f)
    wind.setLooping(true)
    wind.play

    var s = Shader.load("firstPass", Gdx.files.internal("res/shaders/firstPass.vert"), Gdx.files.internal("res/shaders/firstPass.frag"))
    s.monitor()
    s = Shader.load("secondPass", Gdx.files.internal("res/shaders/secondPass.vert"), Gdx.files.internal("res/shaders/secondPass.frag"))
    s.monitor()
    s = Shader.load("test", Gdx.files.internal("res/shaders/test.vert"), Gdx.files.internal("res/shaders/test.frag"))
    s.monitor()
    Shader.multiPass = true;

    SceneGraph.root.shader = "firstPass"

    println("trees init.")
    val node = new RenderNode
    node.shader = "secondPass"
    val quad = new Drawable {
      val m = Mesh(Primitive2D.quad)
      override def draw(){
        if( day.x == 1.f) Shader("secondPass").setUniformf("u_depth", 0.f)
        else Shader("secondPass").setUniformf("u_depth", 1.f)

        m.draw()
      }
    }
    node.scene.push( quad )
    SceneGraph.root.outputTo(node)

    val node2 = new RenderNode
    node2.shader = "test"
    val quad2 = new Drawable {
      val m = Mesh(Primitive2D.quad)
      override def draw(){
        Shader("test").setUniformf("u_dist", getBlurDist)
        m.draw()
      }
    }
    node2.scene.push( quad2 )
    node.outputTo(node2)

    // Kinect.init()
    tree.init()
  }
  override def draw(){

    theta += dw
    MatrixStack.rotate(0.f,theta,0.f)

    if(day.x == 1.f){
      Shader().setUniformf("u_useLocalZ", dayUniforms.z)
      Shader().setUniformf("u_near", dayUniforms.x)
      Shader().setUniformf("u_far", dayUniforms.y)
      Shader().setUniformf("u_useTexture", 0.f)
      ground.draw()
      Shader().setUniformf("u_useLocalZ", dayUniforms.z)
      Shader().setUniformf("u_near", dayUniforms.x)
      Shader().setUniformf("u_far", dayUniforms.y)
      Shader().setUniformf("u_useTexture", 1.f)
      // if(day.z == 1.f) Kinect.draw()
      tree.draw()
    }else{
      Shader().setUniformf("u_useLocalZ", 0.f)
      Shader().setUniformf("u_near", 0.f)
      Shader().setUniformf("u_far", 4.1f)
      Shader().setUniformf("u_useTexture", 0.f)
      ground.draw()
      Shader().setUniformf("u_useLocalZ", nightUniforms.z)
      Shader().setUniformf("u_near", nightUniforms.x)
      Shader().setUniformf("u_far", nightUniforms.y)
      Shader().setUniformf("u_useTexture", 1.f)
      //Kinect.draw()
      tree.draw()
    }

  }

  def rotateWorld(v:Float) = dw = v
  def blurDist(v:Float){ blurDist = v }
  def getBlurDist() = blurDist
  
  def draw2(){
      // Shader("secondPass").setUniformf("u_edge", 1.)
    if( day.x == 1.f) Shader("secondPass").setUniformf("u_depth", 0.f)
    else Shader("secondPass").setUniformf("u_depth", 1.f)
  }
  override def animate(dt:Float){

    live.animate(dt)
    // Kinect.animate(dt)
    // MatrixStack.worldPose.rotate(0,dw,0)
    tree.animate(dt)
  }

  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    var sum = 0.f
    for( i<-(0 until numSamples)){ //Audio.bufferSize)){
      sum += in(i)*in(i) //Audio.in(i)*Audio.in(i)
      rms = math.sqrt( sum / numSamples ).toFloat
    }
    //println( rms )
  }
}



