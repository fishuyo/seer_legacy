
import seer.ui._

class ParamTest extends SeerActor {
  
  val sphere = Sphere()

  val scale = Parameter(0.1f, "scale")
  val pos = Parameter(Vec2(0), "pos")

  val panel = UI.panel(0,0,1,1).layoutX()
  // panel += UI.xy().range(-1,1).bind(pos)
  panel += UI.slider(0.01f,0.01f,0.2f,0.98f).range(0.01f,0.3f).bind(scale)
  panel += UI.xy(0.3f,0.01f,0.5f,0.5f).bind(pos)

  // val panel = UI.slider().range(0, 0.1f).bind(scale)
  panel.positionChildren()

  override def animate(dt:Float) = {}

  Mouse.listen { case m =>
    m.event match {
      case "move" => rayEvent(Point, m.x, m.y)
      case "down" => rayEvent(Pick, m.x, m.y)
      case "drag" => rayEvent(Drag, m.x, m.y)
      case "up" => rayEvent(Unpick, m.x, m.y)
    }
  }

  def rayEvent(evt:PickEventType, x:Float, y:Float) = {
    val ray = Camera.ray(x * Window.width, (1f - y) * Window.height)
    val e = PickEvent(evt, ray)
    panel.event(e)
  }

  override def draw() = {
    MatrixStack.push
    MatrixStack.translate(Vec3(pos(),0))
    MatrixStack.scale(scale()+0.01f)
    sphere.draw
    MatrixStack.pop()

    // UIRenderer.draw(panel)
    WidgetRenderer.draw(panel)
  }

}

classOf[ParamTest]
