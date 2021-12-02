
import seer.time._

class Study00 extends SeerActor {

  // This study is to progress seer composition api
  // The idea is to create a composition in order to
  // refine static timeline api / interface for compositional usability..

  // Begin:
  // Sequencing growing visual bands of grey tone bars
  // timeline must be flexible for trial and error as well as interactive guidance..


  val t = new Timeline()
  t.at(1.second)(()=>{ println("hi 1") })
  t.at(2.second)(()=>{ println("hi 2") })

  t.start
  // t at 2.seconds ()=>{ println("hi 2") }
  // t at 3.seconds ()=>{ println("hi 3") }
  // t at 3 seconds at 1 second { println("hi 3.1") }
  // t at 3 seconds at 2 second { println("hi 3.2") }
  // t at 3 seconds at 3 second { println("hi 3.3") }
  // t at 2 minutes { doSomething() }
  // t every 1 second { repeatSomething() }
  // t at(2 minutes).over(10 seconds){ case t => changeSomething(t) }

  // t.bpm = 60
  // t every 4 beats {"xxx xxx"}

  override def animate(dt:Float){ t.step(dt) }

  // override def draw(){
  // // _draw = () => {
  //   val bar = Plane().scale(0.01f,2f,1f)     //Mesh(Geometry.Quad)

  //   for(i <- 0 until (20*time).toInt){

  //     MatrixStack.push
  //     MatrixStack.translate(-5f + i * 0.1f, -1*(i%2) * 2, 0f)
  //     bar.draw

  //     MatrixStack.pop

  //   }


  // }

  // Keyboard.listen {
    // case '1' => s.updateDuration(1 second)
  // }

}

classOf[Study00]