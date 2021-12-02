

// pickable textures render RenderNode's frame
// eventually towards a sort of visual graph programming dsl...

class Visualize extends SeerActor {

  var node:RenderNode = _
  var node2:RenderNode = _
  var comp:CompositeNode = _
  var tex:Texture = _
  var tex2:Texture = _
  var tex3:Texture = _

  val quads = 0 until 3 map { case i => Plane().translate(i,0,0).scale(0.1f) }

  override def init() = {
    RenderGraph.reset()
    node = new RenderNode
    node2 = new RenderNode
    comp = new CompositeNode

    node >> comp 
    node2 >> comp
    comp.createBuffer()

    tex = Texture(node)
    tex2 = Texture(node2)
    tex3 = Texture(comp)

    node.renderer.scene += Sphere().translate(-0.5,0,0)
    node2.renderer.scene += Cube().scale(1f).translate(0.5,0,0)

    RenderGraph += node 
    RenderGraph += node2

  }
  
  override def draw() = {
    quads.zipWithIndex.foreach{ 
      case (q,0) => q.material.loadTexture(tex)
      case (q,1) => q.material.loadTexture(tex2)
      case (q,2) => q.material.loadTexture(tex3)
      case _ =>
    }
    quads.foreach(_.draw())
  }
}

classOf[Visualize]