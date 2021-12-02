
class Visualize extends SeerActor {




  val w = Window() // get ref to default window
  val gui = GUI()
  w.parameters >> gui >> w

  val comp = R.composite()
  Sphere.draw >> comp
  Cube.draw >> comp >> w
  

  val xfade = Param("xfade")
  xfade.map(1 - _) >> comp.blend0  // who owns this graph.. how is it run, when does it stop.. when comp instance destroyed
  xfade >> comp.blend1


}

classOf[Visualize]

// everything an IO
// IO like a node in graph..
// IO has parameters..
// parameter also an IO?




// case study
// kinetrope designer...
// need the kinetrope code, how can it be built in this paradigm
// has parameters for growth algorithm
// has parameters for visualization

// growth takes point cloud, and has a async process inside -- either triggered on input of point cloud, or independent
// outputs branch


// graphics graph... 
// thread runs in a loop
// every iteration... iterates graphics graph, pull based..
// relies on something wired to a window...
// this is a synchronous execution graph..
// simulation separate?


// window events..
// keyboard, mouse... from a window....
// these are push based? and asynchronous.. message passing works here...



// composition events... scheduler and ecologies

