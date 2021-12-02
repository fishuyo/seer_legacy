
class Slides extends SeerActor {

	var fonts = List[Font]()

	case class Block(fontIndex:Int, text:String, pos:Vec2=Vec2(), size:Float=1f, align:Int=Text.Center)
	case class Slide(blocks:List[Block], pos:Vec3=Vec3(), scale:Float=1f)

	val slides:List[Slide] = title("embodied worldmaking", "MAT dissertation proposal", "timothy wood\n02.26.2020") :: List()

	def title(t:String, sub:String, footer:String) = {
		Slide(
			Block(0, t, Vec2(0,0.15f), 2.2f, Text.Center) ::
			Block(3, sub, Vec2(1.3,-0.45), 1f, Text.Right) ::
			Block(3, footer, Vec2(1.3, -0.7), 0.9f, Text.Right) :: 
			List()
		)
	}

	override def init(){
		val g = 0.129f
		Renderer().environment.backgroundColor.set(g,g,g,1)

		fonts = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New.ttf") ::
						new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New Bold.ttf") ::
						new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New Italic.ttf") ::
						new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New.ttf") ::
						List()

		// fonts(0).smoothing = 0.5f
		fonts(3).color = RGB(0.4f)
		fonts(3).lineHeight(1.25f)

		Camera.nav.quat.setIdentity
    Camera.nav.pos.set(0,0,2)
    Text.sb = null
	}

	override def draw(){
		val m = Material.basic
		m.transparent = true
		Renderer().setMaterialUniforms(m)
		slides.foreach { case s => drawSlide(s) }
	}

	def drawSlide(s:Slide){
		Text.begin()
		s.blocks.foreach { case b =>
			val font = fonts(b.fontIndex)
			font.size(b.size)
			Text.draw(font, b.text, b.pos, 0f, b.align)
		}
		Text.end()
	}

}

classOf[Slides]