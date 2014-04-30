package com.fishuyo.seer
package trees2

import maths._
import spatial._
import graphics._
import trees._
// import particle.structures._
import io._
import dynamic._
import audio._
import util._

import collection.mutable.ListBuffer


object Main extends SeerApp with AudioSource {

  // DesktopApp.loadLibs()
  // Scene.push(this)
  //Scene.push(Kinect)
  
  var ground = Plane.generateMesh( 100.f, 100.f, 100, 100, Quat.up)
  val gModel = Model(ground)

  // var groundA:Drawable = null
  var tree = new Tree() 
  var trees = List(tree) //, new Tree(), new Tree(), new Tree())

  trees.foreach( _.branch() )

  val atmossphere = Sphere()
  val sphere = Sphere()
  atmossphere.scale.set(30.f)

  val cube = Cube().translate(-10,1,0)
  val cube2 = Cube().translate(15,1,0)
  cube2.addChild( Cube().scale(.5).translate(0,1.5,0))

  var rms = 0.f

  // Audio.push(this) 

  var live:Ruby = _
  // DesktopApp() 


  override def init(){

    //val s = Shader.load("basic",File("res/shaders/basic.vert"),File("res/shaders/basic.frag"))
    //s.monitor

    val node = new RenderNode
    node.shader = "sky"
    node.depth = false
    node.scene.push( Plane() )
    SceneGraph.prependNode(node)

    val tID = Texture("res/mond.png")
    gModel.material = new SpecularMaterial()
    gModel.material.texture = Some(Texture(tID))
    gModel.material.textureMix = 1.f
    // gModel.shader = "sky"

    TreeNode.model.material = new ShaderMaterial("sky") //Some(Texture(tID))
    TreeNode.model.shader = "sky"
    // TreeNode.model.material.textureMix = 1.f


    // groundA = OBJ("res/landscapealien.obj") //new ObjLoader().loadObj(Gdx.files.internal("src/main/scala/drone/landscapealien.obj"))
    tree.init()
    live = new Ruby("trees2.rb")
  }

  override def draw(){
    gModel.draw()
    trees.foreach(_.draw())
    sphere.draw()
    // atmossphere.draw()
    cube.draw()
    cube2.draw()
  }

  override def animate(dt:Float){

    sphere.pose.pos = Shader.lightPosition

    live.animate(dt)
    trees.foreach(_.animate(dt))
  }

  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
    var sum = 0.f
    for( i<-(0 until numSamples)){ //Audio.bufferSize)){
      sum += in(i)*in(i) //Audio.in(i)*Audio.in(i)
      rms = math.sqrt( sum / numSamples ).toFloat
    }
  }
}



