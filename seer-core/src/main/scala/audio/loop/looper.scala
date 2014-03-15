
package com.fishuyo.seer
package audio

import graphics._
import maths.Vec3

import com.badlogic.gdx.Gdx


class Looper extends AudioSource with Drawable {
	
	var loops = List[Loop]()
	var plots = List[AudioDisplay]()
	var spects = List[Spectrogram]()
	
	var master = 0
	var mode = "free" // free, sync, sequenced

	def non()={}
	def sync()={}

	def setMode(s:String){
		s match {
			case "free" => 
			case "sync" =>
			case "sequence" =>
			case _ =>
		}
		mode = s
	}

	def newLoop() = {
		val l = new Loop(10.f)
		loops = l :: loops
		val p = new AudioDisplay(500)
		val pos = Vec3( -0.75f*(plots.size%4)+1.5f,  -0.75f+0.75f*(plots.size/4), 0.f)
		p.pose.pos = pos
		p.color = RGBA(0,0,0,1)
		p.cursorColor = RGBA(0,0,0,1)
		plots = p :: plots
		spects = new Spectrogram() :: spects
		spects(0).model.pose.pos = pos
	}

	def play(i:Int, times:Int=0) = {
		if( times == 0) loops(i).play
		else loops(i).play(times)
	}
	
	def stop(i:Int) = loops(i).stop

	def togglePlay(i:Int) = {
		if(!loops(i).playing){
    	loops(i).play
		}else{
      loops(i).stop()
		}
		loops(i).playing
	}

	def stack(i:Int) = {
		loops(i).stack
		loops(i).stacking
	}

	def reverse(i:Int) = loops(i).reverse
	def rewind(i:Int) = loops(i).rewind
	def clear(i:Int) = loops(i).clear

	def record(i:Int){
		loops(i).clear; loops(i).stop; loops(i).record
	}

	def toggleRecord(i:Int) = {
    if(!loops(i).recording){
    	record(i)
		}else{
      loops(i).stop()
      loops(i).play()
		}
		loops(i).recording
	}

	def setGain(i:Int,f:Float) = loops(i).gain = f
	def setDecay(i:Int,f:Float) = loops(i).decay = f
	def setPan(i:Int,f:Float) = loops(i).pan = f
	def setSpeed(i:Int,f:Float) = loops(i).b.speed = f
	def setBounds(i:Int,min:Float,max:Float) = {
		val b1 = min*loops(i).b.curSize
		val b2 = max*loops(i).b.curSize
		loops(i).reverse( min > max)
		loops(i).b.setBounds(b1.toInt,b2.toInt)
		loops(i).vocoder.setBounds(min,max)
		plots(i).setCursor(0,b1.toInt)
		plots(i).setCursor(1,b2.toInt)
	}

	def duplicate(i:Int,times:Int) = loops(i).duplicate(times)

	def switchTo(i:Int) = {
		play(master,1);
		loops(master).onDone = ()=>{loops(i).play; loops(master).onDone=non; master=i}
	}

	def setMaster(i:Int) = {
		mode match {
			case "sync" => 
				loops(master).onSync = non
				master = i
				loops(master).onSync = this.rewindAll
			case _ => ()
		}

	}

	def rewindAll(){ loops.foreach( _.rewind ) }

	override def init(){
		for( i<-(0 until 8)) newLoop
	}

	override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){

		loops.foreach( _.audioIO(in,out,numOut,numSamples) )
	}

	override def draw(){
		for( i<-(0 until plots.size)){
			if( loops(i).vocoderActive){
				if(loops(i).vocoder.update){
					spects(i).setData(loops(i).vocoder.spectrumData)
					loops(i).vocoder.update = false
				}
				spects(i).draw()
			} else{
				val p = plots(i)
				if( loops(i).recording || loops(i).stacking ) p.setSamplesSimple(loops(i).b.samples, 0, loops(i).b.curSize)
				else if( loops(i).dirty ){
					p.setSamples(loops(i).b.samples, 0, loops(i).b.curSize) //loops(i).b.rMin,loops(i).b.rMax)
					loops(i).dirty = false
				}
				p.setCursor(2,loops(i).b.rPos.toInt)
			}

			if( loops(i).vocoderActive){
				val s = loops(i).vocoder.nextWindow / loops(i).vocoder.numWindows.toFloat
				plots(i).setCursor(2,(loops(i).b.curSize * s).toInt)
			}
			plots(i).draw()
		}

	}


	def save(name:String){
		var project = "project-" + (new java.util.Date()).toLocaleString().replace(' ','-').replace(':','-')
    if( name != "") project = name
    var path = "LoopData/" + project
    var file = Gdx.files.external(path).file()
    file.mkdirs()

  	var map = Map[String,Any]()
    for( i <- (0 until loops.size)){
    	val b = loops(i).b
    	val l = loops(i)
    	map = map + (("loop"+i) -> List(b.curSize,b.rPos,b.wPos,b.rMin,b.rMax,b.speed,l.gain,l.pan,l.decay))
    	loops(i).save(path+"/"+i+".wav")
    }

    file = Gdx.files.external(path+"/loops.json").file()
  	val p = new java.io.PrintWriter(file)
	  p.write( scala.util.parsing.json.JSONObject(map).toString( (o) =>{
	  	o match {
	  		case o:List[Any] => s"""[${o.mkString(",")}]"""
	  		case s:String => s"""${'"'}${s}${'"'}"""
	  		case a:Any => a.toString()  
	  	}
	  }))
	  p.close

	}

	def load(name:String){
    val path = "LoopData/" + name
    val file = Gdx.files.external(path+"/loops.json").file()

    val sfile = scala.io.Source.fromFile(file)
  	val json_string = sfile.getLines.mkString
  	sfile.close

  	val parsed = scala.util.parsing.json.JSON.parseFull(json_string)
  	if( parsed.isEmpty ){
  		println(s"failed to parse: $path")
  		return
  	}

  	val map = parsed.get.asInstanceOf[Map[String,Any]]

    for( i <- (0 until loops.size)){
    	loops(i).load(path+"/"+i+".wav")

	  	val l = map("loop"+i).asInstanceOf[List[Double]]
	  	// println(l)
	  	val loop = loops(i)
	  	val b = loop.b

	  	b.curSize = l(0).toInt
	  	b.maxSize = b.curSize
	  	b.rPos = l(1).toFloat
	  	b.wPos = l(2).toInt
	  	b.rMin = l(3).toInt
	  	b.rMax = l(4).toInt
	  	b.speed = l(5).toFloat
	  	loop.gain = l(6).toFloat
	  	loop.pan = l(7).toFloat
	  	loop.decay = l(8).toFloat

    }
	}
}


