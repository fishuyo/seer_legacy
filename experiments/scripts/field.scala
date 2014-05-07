
import com.fishuyo.seer._
import graphics._
import dynamic._
import maths._
import io._
import util._

object Script extends SeerScript {
	implicit def f2i(f:Float) = f.toInt

  val fv = new FieldViewer(100,100)
  // val fieldViewer = new ReactDiffuseFV

  val mesh = Plane.generateMesh(10,10,50,50, Quat.up)
  val model = Model(mesh).translate(0,-4,0)
  model.material = Material.specular

	Mouse.clear()
	Mouse.use()

	Mouse.bind("drag", (i) => {
	    var x = i(0)
	    var y = i(1)

		  //convert click to position in field
	    val r = Camera.ray(x,y)
	    val hit = Quad().intersect(r)
	    val v = hit.get
	    v.set( (v + Vec3(1,1,0)) * 0.5 ) 

	    x = (v.x*fv.w)
	    y = (v.y*fv.h)

	    fv.field.set(x,y,1.0)
	    RD.chemA.set(x,y,1.0)
	})

	Keyboard.clear
	Keyboard.use
  Keyboard.bind("g", fv.toggleRunning )
	Keyboard.bind("f", ()=>{
    for( y <- ( 0 until 50 ); x <- ( 0 until 50 )){
      val i = y*50+x
      var a:Float = RD.chemA(x*2,y*2)
      mesh.vertices(i).y = a
      mesh.recalculateNormals
      mesh.update
    }
  })

	override def draw(){
		fv.draw
    model.draw
	}

	override def animate(dt:Float){
		fv.animate(dt)

    FPS.print
	}

}

class FieldViewer(var w:Int, var h:Int) extends Animatable {

  var running = true;
  var field = new Field2D(w,h)
  var next = new Field2D(w,h)

  def resize(width:Int,height:Int){
    w = width
    h = height
    field.resize(w,h)
    next.resize(w,h)
  }

  def toggleRunning() = running = !running

  override def init() = runOnce()
  override def draw() = {
    Shader.textureMix = 1.f
    field.draw
  }
  override def animate(dt:Float) = if(running) runEverytime(dt)


  def runOnce(){

  }

  def runEverytime(dt:Float){
  	// conway(this,dt)
  	RD.step(this,dt)
  }
}




def conway(fv:FieldViewer, dt:Float) = {
  for( y <- (0 until fv.h); x <- (0 until fv.w)){  
    var count = 0;
    for( j <- (-1 to 1); i <- (-1 to 1)){
      count += fv.field.getToroidal(x+i,y+j).r.toInt
    }
    
    //was alive
    if( fv.field(x,y).r > 0.f ){
      count -= 1
      if( count == 2 || count == 3) fv.next.set(x,y,1.f)
      else {
      fv.next.set(x,y,0.f)
      //println( x + " " + y + " dieing")
      }
    }else if( fv.field(x,y).r == 0.f) { //was dead
      if( count == 3 ){
        fv.next.set(x,y,1.f)
        //println( x + " " + y + " born")
      }
      else fv.next.set(x,y,0.f)
    }
  }
  fv.field.set( fv.next )
}


object RD {
	var alphaA = .75f
  var alphaB = 0f
  var eps = .02f
  val dx = 165.f / 200.f //Main.n
  var dtt = .02f
  var A = .75f
  var B = .01f
  
  type RA = List[Float]
  val ra = (a: Float, b:Float ) => (1.f/eps)*(a*(1.f-a)*(a-(b+B)/A))
  val rb = (a: Float, b:Float ) => a-b

	class ChemField extends Field2D(100,100){
		val next = new Field2D(100,100)

		override def resize(x:Int,y:Int){
			next.resize(x,y)
			super.resize(x,y)
		}
	}

  val chemA = new ChemField
  val chemB = new ChemField
	chemB.set( A * .5f )

	def step(fv:FieldViewer, dt:Float){
		val s = 5;
    for( j <- (-s to s); i <- (-s to s) ) chemA.set( fv.w/2+i, fv.h/2+j, 1.f )

    // diffusion
    for( y <- ( 1 to fv.h-2 ); x <- ( 1 to fv.w-2 )){
      var a =  -4.f * chemA(x,y) + chemA(x+1,y) + chemA(x-1,y) + chemA(x,y+1) + chemA(x,y-1)
      var b =  -4.f * chemB(x,y) + chemB(x+1,y) + chemB(x-1,y) + chemB(x,y+1) + chemB(x,y-1)
      a = chemA(x,y) + a * dtt * alphaA / (dx*dx)
      b = chemB(x,y) + b * dtt * alphaB / (dx*dx)
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)
    }

    // reaction
    for( y <- ( 1 to fv.h-2 ); x <- ( 1 to fv.w-2 )){
      
      var a = chemA.next(x,y) + dtt * ra( chemA.next(x,y), chemB.next(x,y))
      var b = chemB.next(x,y) + dtt * rb( chemA.next(x,y), chemB.next(x,y))
      chemA.next.set(x,y,a)
      chemB.next.set(x,y,b)

      val color = RGBA(0,0,1,1) * a + RGBA(.9f,.0f,0.f,1) * b
      fv.field.set(x,y, color )
    }
    
    chemA.set(chemA.next)
    chemB.set(chemB.next)
	}

}


  

Script