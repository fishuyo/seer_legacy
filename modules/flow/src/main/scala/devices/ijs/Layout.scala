// package flow
// package ijs

// import collection.mutable.ListBuffer

// object Layout {
//   var padding = (0f,0f)
//   def V(_x:Float=0f, _y:Float=0f, _w:Float=1f, _h:Float=1f) = new VerticalLayout{x=_x; y=_y; w=_w; h=_h}
//   def H(_x:Float=0f, _y:Float=0f, _w:Float=1f, _h:Float=1f) = new HorizontalLayout{x=_x; y=_y; w=_w; h=_h}
//   def G(_x:Float=0f, _y:Float=0f, _w:Float=1f, _h:Float=1f, nx:Int=0, ny:Int=0) = new GridLayout(nx,ny){x=_x; y=_y; w=_w; h=_h}
// }
// sealed trait Layout {
//   var (x,y,w,h) = (0f,0f,1f,1f)
//   val layouts = ListBuffer[Layout]()

//   def +=(w:Widget) = layouts += new SingleLayout(w)
//   def +=(l:Layout) = layouts += l

//   def resizeChildren():Unit = {}
//   def addWidgets(widgets:ListBuffer[Widget]):Unit = {
//     layouts.foreach { 
//       case l:SingleLayout => widgets += l.widget
//       case l => l.addWidgets(widgets)
//     }
//   }
//   // = this match {
//   //   groups.foreach { 
//   //     case g:SGroup => 
//   //   }
//   // }
// }

// class SingleLayout(var widget:Widget) extends Layout {
//   override def resizeChildren() = widget match {
//     case w:Slider => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case w:Button => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case w:XY => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case w:Label => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case w:RangeSlider => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case w:Menu => widget = w.copy(x=this.x,y=this.y,w=this.w,h=this.h)
//     case _ => ()
//   }
// }
// class HorizontalLayout extends Layout {
//   override def resizeChildren() = {
//     val nw = w / layouts.size
//     layouts.zipWithIndex.foreach { case (l,i) =>
//       l.x = x + nw*i
//       l.y = y
//       l.w = nw
//       l.h = h
//       l.resizeChildren()
//     }
//   }
// }
// class VerticalLayout extends Layout {
//   override def resizeChildren() = {
//     val nh = h / layouts.size
//     layouts.zipWithIndex.foreach { case (l,i) =>
//       l.x = x
//       l.y = y + nh*i
//       l.w = w
//       l.h = nh
//       l.resizeChildren()
//     }
//   }
// }
// class GridLayout(var nx:Int=0, var ny:Int=0) extends Layout {
//   override def resizeChildren() = {
//     val n = layouts.size
//     if(nx == 0 && ny == 0){ 
//       ny = math.round(math.sqrt(n)).toInt
//       nx = math.ceil(n*1f/ny).toInt
//     }
//     val nw = w / nx
//     val nh = h / ny
//     layouts.zipWithIndex.foreach { case (l,i) =>
//       val ix = i % nx
//       val iy = i / nx
//       l.x = x + nw*ix
//       l.y = y + nh*iy
//       l.w = nw
//       l.h = nh
//       l.resizeChildren()
//     }
//   }
// }