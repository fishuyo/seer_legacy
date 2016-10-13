

class TerrainBody extends SeerActor {

  val model = Terrain()        // generate fractal terrain mesh

  override def init(){
    OpenNI.listen { 
      case user :: users => 
        // torso position amplified and offset
        val to = user.torso
        val data = Vec3(-to.x,to.y,-to.z) + Vec3(0,0.2,-1)   
        // smoothly move camera position toward torso position
        camera.nav.pos.lerpTo(data*50, 0.005)                  
        // send tuple as message to all actors
        // System.broadcast(("pos",data))                 

      case _ => ()
    }
  }

  override def draw(){
    model.draw
  }
}

classOf[TerrainBody]