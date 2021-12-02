
import seer.video._

class SlideActor extends SeerActor {

  sealed abstract class Content { val text:String }
  case class H1(text:String) extends Content
  case class H2(text:String) extends Content
  case class H3(text:String) extends Content
  case class L1(text:String) extends Content
  case class L2(text:String) extends Content
  case class L3(text:String) extends Content
  case class Link(text:String) extends Content
  case class Txt(text:String) extends Content
  case class Template(text:String) extends Content
  case class LineHeight(text:String) extends Content
  case class Grid(text:String) extends Content
  case class HideProgress(text:String="") extends Content
  case class Comment(text:String) extends Content

  
  sealed abstract class FormattedContent {
    val pos:Vec2; val size:Float;
  }
  case class TextBlock(style:String, text:String, pos:Vec2=Vec2(), size:Float=1f, align:Int=Text.Center, width:Float=0f, accum:Boolean=false) extends FormattedContent
  case class ImageBlock(tex:Texture, pos:Vec2=Vec2(), size:Float=1f, scale:Vec2 = Vec2(1)) extends FormattedContent
  case class VideoBlock(tex:VideoTexture, pos:Vec2=Vec2(), size:Float=1f, scale:Vec2 = Vec2(1)) extends FormattedContent
  case class EmptyBlock(pos:Vec2=Vec2(), size:Float=1f) extends FormattedContent
  case class HideProgressBlock(pos:Vec2=Vec2(), size:Float=1f) extends FormattedContent
  
  case class Slide(val content:Seq[Content]) 

  def parseSlides(text:String):Array[Slide] = {
    text.split("___").map(_.trim).filter(!_.isEmpty).map{ case slide => 
      val content = slide.split('\n').map(_.trim).filter(!_.isEmpty).map{ 
        case s if s.startsWith("//") => Comment(s.substring(2).trim)
        case s if s.startsWith("$lh") => LineHeight(s.substring(3).trim)
        case s if s.startsWith("$grid") => Grid(s.substring(5).trim)
        case s if s.startsWith("$hide") => HideProgress()
        case s if s.startsWith("$") => Template(s.tail.trim)

        case s if s.startsWith("###") => H3(s.substring(3).trim)
        case s if s.startsWith("##") => H2(s.substring(2).trim)
        case s if s.startsWith("#") => H1(s.tail.trim)
        case s if s.startsWith("---") => L3(s.substring(3).trim)
        case s if s.startsWith("--") => L2(s.substring(2).trim)
        case s if s.startsWith("-") => L1(s.tail.trim)
        case s if s.startsWith("![](") => Link(s.substring(4).trim.dropRight(1))
        case s if s.startsWith("!") => Link(s.tail.trim)
        case s => Txt(s)
      }
      Slide(content)
    }
  }

  def writeSpeakerView(slides:Array[Slide]) = {
    val wd = os.pwd / "defense-web"
    val template = os.read(wd / "index-template.html")

    val notes = slides.collect { case s if s.content.collect { case c:Comment => c}.length > 0 => 
      val text = s.content.map {
        case H1(text) => s"<h3>$text</h3>\n"
        case Comment(text) => s"<li>$text</li>\n"
        case _ => ""
      }.mkString
      s"""
      <section>
        $text
      </section>
      """
    }.mkString

    val out = template.replace("$slides$", notes)
    os.write.over(wd / "index.html", out)
  }

  def detectTemplate(slide:Slide): String = {
    val list = slide.content
    
    val template = list.head match { 
      case Template(name) => name
      case _ => ""
    }

    val hasBody = list.collect { 
      case a:L1 => a
      case a:L2 => a
      case a:L3 => a
      case a:Txt => a
      case a:Link => a
    }.length > 0  

    if(!template.isEmpty) template
    else if(list.head.text.startsWith("\"")) "quote"
    else if(hasBody) "normal"
    else "title"  
  }

  def slide2blocks(slide:Slide):Seq[FormattedContent] = {
    var h = 0
    var l = 0f //list counters
    var lh = 0f //0.12f 

    var (gridx, gridy) = (1,1)
    var max = Vec2(3f,1.8f)
    var pad = Vec2(0f)

    val w = 3.5f
    val blocks:Seq[FormattedContent] = detectTemplate(slide) match {
      case "title" =>
        slide.content.map { 
          case H1(text) => TextBlock("none", text, Vec2(0,0.25f), 2.2f, Text.Center)
          case H2(text) => TextBlock("none", text, Vec2(1.3,-0.25), 0.7f, Text.Right)
          case H3(text) => h += 1; TextBlock("sub", text, Vec2(1.3,-0.7 - h*0.0f), 0.9f, Text.Right, accum=true)
          case c:Content => EmptyBlock() //TextBlock("none", "", Vec2(1,1), 1f)
        }
      
      case "quote" =>
        slide.content.map { 
          case Txt(text) => TextBlock("italic", text, Vec2(-w/2,0.7), 0.8f, Text.Left, w)
          case L1(text) => l += 1; TextBlock("none", s"-- $text", Vec2(-w/2, -0.2f ), 0.85f, Text.Left, w)
          case L2(text) => l += 1; TextBlock("none", s"-- $text", Vec2(-w/2, -0.2f ), 0.85f, Text.Left, w)
          case HideProgress(text) => HideProgressBlock()
          case c:Content => EmptyBlock() //TextBlock("none", "", Vec2(1,1), 1f)
        }
      
      case _ => 
        slide.content.map { 
          case HideProgress(text) => HideProgressBlock()
          case LineHeight(text) => lh = text.toFloat; EmptyBlock()
          case Grid(text) => 
            val vs = text.split(" ");
            if(vs.length > 0) gridx = vs.head.toInt
            if(vs.length > 1) gridy = vs(1).toInt
            if(vs.length > 2) pad.x = vs(2).toFloat
            if(vs.length > 3) pad.y = vs(3).toFloat
            if(vs.length > 4) max.x = vs(4).toFloat
            if(vs.length > 5) max.y = vs(5).toFloat 
            EmptyBlock() 
          case H1(text) => TextBlock("none", text, Vec2(0,1.2f), 1.5f, Text.Center)
          // case H2(text) => TextBlock("sub", text, Vec2(1.3,-0.45), 1f, Text.Right)
          // case H3(text) => h3 += 1; TextBlock("sub", text, Vec2(1.3,-0.7 - h3*0.1f), 0.9f, Text.Right)
          case Txt(text) => TextBlock("none", text, Vec2(-w/2,0.7), 0.8f, Text.Left, w)
          case L1(text) => l += 1; TextBlock("none", s"- $text", Vec2(-w/2, 0.8f - l*lh), 0.85f, Text.Left, w, true)
          case L2(text) => l += 1; TextBlock("none", s"  - $text", Vec2(-w/2, 0.8f - l*lh), 0.7f, Text.Left, w, true)
          case L3(text) => l += 1; TextBlock("none", s"    - $text", Vec2(-w/2, 0.8f - l*lh), 0.6f, Text.Left, w, true)

          case Link(text) => link2block(text)
            
          case c:Content => EmptyBlock() //TextBlock("none", "", Vec2(1,1), 1f)
        } 
    }
    if(gridx < 1) gridx = 1
    if(gridy < 1) gridy = 1
    scaleImages(blocks, gridx, gridy, max, pad)
  }

  def scaleImages(blocks:Seq[FormattedContent], nx:Int, ny:Int, max:Vec2, pad:Vec2):Seq[FormattedContent] = {
    val n = blocks.collect{ case b:ImageBlock => b; case b:VideoBlock => b }.length
    if(n == 0) return blocks

    val width = max.x / nx
    val height = max.y / ny
    val offX = -max.x/2 + width/2 - pad.x/2   
    val offY = max.y/2 - height/2 + pad.y/2 

    def pos(i:Int) = {
      val ix = i % nx; val iy = i / nx
      val x = width*ix + offX + pad.x * ix
      val y = -height*iy + offY - pad.y * iy
      Vec2(x,y) 
    }

    def size(w:Int,h:Int) = {
      val s = (width) / 2f
      // if(w > h) Vec3(1f,-h.toFloat/w,s) 
      // else Vec3(w.toFloat/h,-1f,s)
      Vec3(w.toFloat/h,-1f,s) 
    }

    var index = -1
    blocks.map { 
      case b:ImageBlock => 
        index += 1
        val p = pos(index)
        var s = size(b.tex.w, b.tex.h) 
        ImageBlock(b.tex,p,s.z,s.xy)
      case b:VideoBlock => 
        index += 1
        val p = pos(index)
        var s = size(b.tex.width, b.tex.height) 
        VideoBlock(b.tex,p,s.z,s.xy) 
      case b => b
    }
  }

  def cacheFile(url:String):String = {
    val wd = os.pwd / "_cache"
    val name = url.substring(url.lastIndexOf('/')+1)
    if(!os.exists(wd / name)){
      os.makeDir.all(wd)
      os.proc("curl","-Lo", name, url).call(wd)
    }
    wd.toString + "/" + name   // return path of cached file
  }

  def link2block(text:String):FormattedContent = {
    var path = text
    
    // download if url and not in _cache folder
    if(text.startsWith("http")) path = cacheFile(text)
    if(path.endsWith(".mp4") || path.endsWith(".mov")) video2block(path)
    else image2block(path)
  }

  def image2block(path:String):FormattedContent = {
    var tex:Option[Texture] = None
    val mtime = os.mtime(os.Path(path))
    if(imageCache().contains(path)){ // use tex if in cache
      val e = imageCache()(path)
      if(mtime == e._1) tex = e._2 // and not outdated
    }

    if(tex.isEmpty){ // otherwise load and cache
      tex = Texture.load(path)
      imageCache() = imageCache() + (path -> (mtime, tex))
    }

    val res = tex match {
      case Some(t) => ImageBlock(t, Vec2(), 1.5f)
      case None => TextBlock("none", "!!!", Vec2(), 1f)
    }
    res
  }
  
  def video2block(path:String):FormattedContent = {
    var tex:Option[VideoTexture] = None
    val mtime = os.mtime(os.Path(path))
    if(videoCache().contains(path)){ // use tex if in cache
      val e = videoCache()(path)
      if(mtime == e._1) tex = e._2 // and not outdated
    }

    if(tex.isEmpty){ // otherwise load and cache
      tex = Some(new VideoTexture(path))
      videoCache() = videoCache() + (path -> (mtime, tex))
    }

    val res = tex match {
      case Some(t) => VideoBlock(t, Vec2(), 1.5f)
      case None => TextBlock("none", "!!!", Vec2(), 1f)
    }
    res
  }

  type CacheEntry = (Long,Option[Texture]) //(mtime:Long, tex:Option[Texture])
  val imageCache = Var("imageCache", Map[String,CacheEntry]() )

  type CacheEntryV = (Long,Option[VideoTexture]) //(mtime:Long, tex:Option[Texture])
  val videoCache = Var("videoCache", Map[String,CacheEntryV]() )


  def readSlides():Array[Slide] = {
    val wd = os.pwd / "defense" / "slides"
    val files = os.list(wd, true)
    val segments = files.map { case f => parseSlides(os.read(f))}
    segmentIndices = segments.map(_.length).scanLeft(0)(_+_)
    segments.flatten
  }

  var segmentIndices = Array[Int]()
  var slides = readSlides()
  var blocks:Seq[Seq[FormattedContent]] = _
  val slideIndex = Var("slideIndex", 0)

  var fonts = HashMap[String,Font]() 

  writeSpeakerView(slides)

  Monitor.stop("defense/slides")
  Monitor("defense/slides"){ case f => 
    println("loading slides..") 
    slides = readSlides()
    writeSpeakerView(slides)
    if(slideIndex() >= slides.length) slideIndex() = 0
    Run.animate{ blocks = slides.map(slide2blocks(_)) }
  }

  def nextSlide() = {
    var i = slideIndex(); i += 1
    if(i >= slides.length) slideIndex() = 0 else slideIndex() = i
    stopVideos()
  }
  def previousSlide() = {
    var i = slideIndex(); i -= 1
    if(i < 0) slideIndex() = slides.length - 1 else slideIndex() = i
    stopVideos()
  }
  def stopVideos() = {
    videoCache().values.foreach { case (t,v) =>
      v.foreach(_.stop)
    }
  }
  

  Keyboard.listen {
    case 'j' => previousSlide()
    case 'k' => nextSlide()
  }

	override def init(){
    blocks = slides.map(slide2blocks(_))
 
		val g = 0.129f
		Renderer().environment.backgroundColor.set(g,g,g,1)

		fonts("none") = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New.ttf")
		fonts("bold") = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New Bold.ttf")
		fonts("italic") = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New Italic.ttf")
		fonts("sub") = new Font("/Users/fishuyo/_projects/2020_dissertation/proposal_presentation/assets/fonts/Courier New.ttf")

		// fonts(0).smoothing = 0.5f
		fonts("sub").color = RGB(0.4f)
		fonts("sub").lineHeight(1.25f)

		Camera.nav.quat.setIdentity
    Camera.nav.pos.set(0,0,2)
    Text.sb = null
	}

	override def draw() = {
		val m = Material.basic
		m.transparent = true
		Renderer().setMaterialUniforms(m)
    val bs = blocks(slideIndex())
    drawSlideBlocks(bs)
	}
 
	def drawSlideBlocks(blocks:Seq[FormattedContent]) = {
    val index = slideIndex()
    var drawProgress = index > 1
    var dh = 0f
    Text.begin()
		blocks.foreach { 
      case b:TextBlock =>
        val font = fonts(b.style)
        font.size(b.size)
        val g = Text.draw(font, b.text, b.pos + Vec2(0,-dh), b.width, b.align) 
        if(b.accum) dh += g.height / 1000f + 0.11f
      case b:HideProgressBlock => drawProgress = false
      case _ =>
    }
    
    // draw slide number + progress text
    if(drawProgress){
      val f = fonts("sub")
      f.size(0.6f)
      Text.draw(f, s"${index+1} / ${slides.length}", Vec2(1.9,-1.2), 0f, Text.Right)

      val segment = segmentIndices.tail.indexWhere(index < _)
      val labels = Array("Introduction", "Background", "Works", "Methods", "System Design", "Conclusion")
      labels.zipWithIndex.foreach{ 
        case (l,i) if i == segment => 
          val f = fonts("bold"); f.size(0.6f)  
          Text.draw(f,l, Vec2(-1.7f + i * 0.6f, -1.2), 0f, Text.Center)
        case (l,i) =>
          val f = fonts("sub"); f.size(0.6f)  
          Text.draw(f,l, Vec2(-1.7f + i * 0.6f, -1.2), 0f, Text.Center)
      }
    }
    
    Text.end()
    
    blocks.foreach {
      case ImageBlock(tex, p, s, scl) =>
        val q = Plane()
        q.material.loadTexture(tex)
        q.pose.pos.set(Vec3(p,0f))
        q.scale.set(Vec3(scl,1f) * s )
        shader("basic")
        q.draw
      case VideoBlock(vid, p, s, scl) =>
        val q = Plane()
        vid.setVolume(0f)
        vid.play
        vid.update
        q.material.loadTexture(vid.texture)
        q.pose.pos.set(Vec3(p,0f))
        q.scale.set(Vec3(scl,1f) * s )
        shader("basic")
        q.draw
      case _ => 
    } 
	}

}

classOf[SlideActor]