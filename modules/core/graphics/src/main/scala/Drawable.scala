
package seer
package graphics

trait Drawable {
  var initialized = false
  var _draw = ()=>{}
  var _step = (dt:Float)=>{}

  def init(){}
  def draw(){_draw()}
  def step(dt: Float){_step(dt)}
  
  @deprecated("use step instead", "")
  def animate(dt: Float){_step(dt)}
}

@deprecated("use Drawable", "")
trait Animatable extends Drawable {
}
