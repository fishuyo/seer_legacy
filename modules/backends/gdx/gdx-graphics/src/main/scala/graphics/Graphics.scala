
package seer
package graphics

class GraphicsImpl extends Graphics {

  val gl:GLES30 = new GLES30Impl

}



object Run{
  def animate(f: =>Unit) = new AnimateOnce(f)
  def apply[T](f: =>T) = new DrawOnce(f)
}
class DrawOnce[T](f: =>T) extends Drawable{
  var ret:T = null.asInstanceOf[T]
  var ran = false
  Scene.push(this)
  override def draw(){
    try{
    if(!ran){ ret=f; ran = true}
    } catch{ case e:Exception => println(e)} finally{ Scene.drawable -= this }
  }
}
class AnimateOnce[T](f: =>T) extends Animatable {
  var ret:T = null.asInstanceOf[T]
  var ran = false
  Scene.push(this)
  override def animate(dt:Float){
    try{
    if(!ran){ ret=f; ran = true}
    } catch{ case e:Exception => println(s"AnimateOnce: $e")} finally{ Scene.remove(this) }
  }
}








