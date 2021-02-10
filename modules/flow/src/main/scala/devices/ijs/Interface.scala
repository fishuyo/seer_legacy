// package flow
// package ijs

// import protocol.IOPort

// import java.io.File
// import java.io.PrintWriter
// import java.io.FileOutputStream

// import akka.actor._
// import akka.stream._
// import akka.stream.scaladsl._
// import concurrent.ExecutionContext.Implicits.global

// import collection.mutable.ListBuffer
// import collection.mutable.HashMap
// import collection.mutable.Set

// sealed trait Widget {
//   def name: String
//   def x: Float
//   def y: Float
//   def w: Float
//   def h: Float
// }

// case class Slider(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f,
//     min: Float = 0f,
//     max: Float = 1f
// ) extends Widget
// case class Button(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f,
//     mode: String = "momentary"
// ) extends Widget
// case class XY(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f
// ) extends Widget
// case class Label(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f,
//     value: String = ""
// ) extends Widget
// case class RangeSlider(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f,
//     min: Float = 0f,
//     max: Float = 1f
// ) extends Widget
// case class Menu(
//     name: String,
//     x: Float = 0f,
//     y: Float = 0f,
//     w: Float = 1f,
//     h: Float = 1f,
//     options: Seq[Any]
// ) extends Widget

// object Interface {
//   val interfaces = HashMap[String, InterfaceBuilder]()

//   def apply(name: String) =
//     interfaces.getOrElseUpdate(name, new InterfaceBuilder(name))

//   def create(name: String) = {
//     val io = interfaces.getOrElseUpdate(name, new InterfaceBuilder(name))
//     WebsocketActor.sendInterfaceList()
//     io.widgets.clear
//     io.layouts.clear
//     io
//   }

//   def fromApp(app: AppIO) = {
//     val io = app.config.io
//     val ijs = interfaces.getOrElseUpdate(io.name, new InterfaceBuilder(io.name))
//     WebsocketActor.sendInterfaceList()
//     ijs.widgets.clear
//     ijs.layouts.clear
//     var sx = 0f
//     var bx = 0f
//     io.sinks.foreach {
//       case IOPort(name, "f") =>
//         ijs += Slider(name, sx, 0f, 0.1f, 0.5f); sx += 0.1f
//       case IOPort(name, "bool") =>
//         ijs += Button(name, bx, 0.55f, 0.1f, 0.1f); bx += 0.1f
//       case IOPort(name, "") =>
//         ijs += Button(name, bx, 0.55f, 0.1f, 0.1f); bx += 0.1f
//       case _ => ()
//     }
//     ijs.sync()
//     ijs
//   }

//   val template =
//     """
//   <html>
//   <head>
//     <script src="/assets/js/interface.js"></script>
//     <script src="/assets/js/interface.client.js"></script>
//   </head>
//   <body>
//     <script>
//       panel = new Interface.Panel({ useRelativeSizesAndPositions:true })
//       panel.background = 'black'
  
//       Interface.OSC.receive = function( address, typetags, parameters ) {
//         console.log( address, typetags, parameters );
//         if(address == "/_eval"){
//           eval(parameters[0])
//         }
//       }
//     </script>
//   </body>
//   </html>
//       """
// }

// class InterfaceBuilder(val name: String) extends IO {

//   val widgets = ListBuffer[Widget]()
//   val layouts = ListBuffer[Layout]()

//   def +=(w: Widget) = widgets += w
//   def +=(l: Layout) = layouts += l

//   var sourceActor: Option[ActorRef] = None
//   val _src = Source
//     .actorRef[(String, Any)](bufferSize = 0, OverflowStrategy.fail)
//     .mapMaterializedValue((a: ActorRef) => sourceActor = Some(a))
//   val broadcastSource: Source[(String, Any), akka.NotUsed] = _src
//     .toMat(BroadcastHub.sink)(Keep.right)
//     .run()
//     .buffer(1, OverflowStrategy.dropHead)
//     .watchTermination()((_, f) => {
//       f.onComplete { // for debugging
//         case t => println(s"Interface source terminated: $name: $t")
//       }; akka.NotUsed
//     })
//   // val sourceActors = HashMap[String,ActorRef]()
//   var sinkActors = ListBuffer[ActorRef]()

//   override def sources: Map[String, Source[Any, akka.NotUsed]] =
//     widgets.map {
//       case w =>
//         val src = broadcastSource.collect {
//           case (name, value) if name == w.name => value
//         }
//         // Source.actorRef[Float](bufferSize = 0, OverflowStrategy.fail)
//         // .mapMaterializedValue( (a:ActorRef) => { sourceActors(w.name) = a; akka.NotUsed } )
//         w.name -> src
//     }.toMap

//   override def sinks: Map[String, Sink[Any, akka.NotUsed]] =
//     widgets.map {
//       case w =>
//         val sink = Sink
//           .foreach((f: Any) => {
//             sinkActors.foreach(_ ! (w.name, f))
//             // try{ oscSend.send(s"/$name", f) }
//             // catch{ case e:Exception => AppManager.close(config.io.name) }
//           })
//           .mapMaterializedValue { case _ => akka.NotUsed }
//         w.name -> sink
//     }.toMap

//   override def sink(name: String) =
//     Some(
//       Sink
//         .foreach((f: Any) => {
//           sinkActors.foreach(_ ! (name, f))
//         })
//         .mapMaterializedValue { case _ => akka.NotUsed }
//     )

//   def addWidgetsFromLayouts() = {
//     layouts.foreach {
//       case l =>
//         l.resizeChildren()
//         l.addWidgets(widgets)
//     }
//   }

//   def sync() {
//     addWidgetsFromLayouts()
//     sinkActors.foreach {
//       case a =>
//         sync(a)
//     }
//   }
//   def sync(index: Int) {
//     if (sinkActors.isDefinedAt(index)) {
//       val a = sinkActors(index)
//       sync(a)
//     }
//   }
//   def sync(a: ActorRef) {
//     a ! ("_eval", "panel.clear()")
//     // a ! ("_eval","$('select').remove()")
//     // val code = widgets.flatMap{ case w => widget2String(w) + s"\npanel.add(${w.name})\n" }
//     // a ! ("_eval", code)
//     widgets.foreach {
//       case w => a ! ("_eval", widget2String(w) + s"\npanel.add(${w.name})")
//     }
//   }

//   def widget2String(w: Widget) = w match {
//     case Slider(name, x, y, w, h, min, max) =>
//       s"""$name = new Interface.Slider({ name:"$name", label:"$name", bounds: [$x,$y,$w,$h], min:$min, max:$max ${if (w > h)
//         ",isVertical:false"
//       else ""} })"""
//     case Button(name, x, y, w, h, mode) =>
//       s"""$name = new Interface.Button({ name:"$name", label:"$name", mode:"$mode", bounds: [$x,$y,$w,$h] })"""
//     case XY(name, x, y, w, h) =>
//       s"""$name = new Interface.XY({ name:"$name", label:"$name", childWidth:15, numChildren:1, usePhysics:false, bounds: [$x,$y,$w,$h] })"""
//     case Label(name, x, y, w, h, value) =>
//       s"""$name = new Interface.Label({ name:"$name", value:"$value", bounds: [$x,$y,$w,$h], vAlign:"middle", hAlign:"center" })"""
//     case RangeSlider(name, x, y, w, h, min, max) =>
//       s"""$name = new Interface.Range({ name:"$name", bounds: [$x,$y,$w,$h], min:$min, max:$max })"""
//     case Menu(name, x, y, w, h, opts) =>
//       s"""$name = new Interface.Menu({ name:"$name", bounds: [$x,$y,$w,$h], options:[${opts
//         .map { case s: String => s"'$s'"; case a => a }
//         .mkString(",")}] })"""
//   }

//   def save() {
//     val path = "server/public/interfaces/"
//     val pw = new PrintWriter(new FileOutputStream(path + name + ".html", false));
//     pw.write(toHtml)
//     pw.close
//   }

//   def toHtml() = {
//     htmlHeader +
//       "panel = new Interface.Panel({ useRelativeSizesAndPositions:true })\n" +
//       "panel.background = 'black'\n" +
//       widgets.map(widget2String(_)).mkString("\n") + "\n" +
//       s"panel.add( ${widgets.map(_.name).mkString(",")} )" +
//       htmlFooter
//   }

//   def htmlHeader() = """
// <html>
// <head>
//   <script src="/assets/js/interface.js"></script>
//   <script src="/assets/js/interface.client.js"></script>
// </head>
// <body>
//   <script>
//   """

//   def htmlFooter() =
//     """

//   Interface.OSC.receive = function( address, typetags, parameters ) {
//     console.log( address, typetags, parameters );
//   }
//   </script>
// </body>
// </html>
//   """

// }
