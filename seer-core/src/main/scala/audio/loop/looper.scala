
package com.fishuyo
package audio

import graphics._
import maths.Vec3

class Looper extends AudioSource with GLDrawable {
	var loops = List[Loop]()
	var plots = List[AudioDisplay]()
	for( i<-(0 until 8)) newLoop
	
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
		p.pose.pos = Vec3( -0.75f*(plots.size%4)+1.5f,  -0.75f+0.75f*(plots.size/4), 0.f)
		p.color = Vec3(0,1.f,0)
		plots = p :: plots
	}

	def play(i:Int, times:Int=0) = {
		if( times == 0) loops(i).play
		else loops(i).play(times)
	}
	def stop(i:Int) = loops(i).stop
	def togglePlay(i:Int){
		if(!loops(i).playing){
    	loops(i).play
		}else{
      loops(i).stop()
		}
	}
	def stack(i:Int) = loops(i).stack
	def reverse(i:Int) = loops(i).reverse
	def rewind(i:Int) = loops(i).rewind
	def clear(i:Int) = loops(i).clear
	def record(i:Int){
		loops(i).clear; loops(i).stop; loops(i).record
	}
	def toggleRecord(i:Int) {
    if(!loops(i).recording){
    	record(i)
		}else{
      loops(i).stop()
      loops(i).play()
		}
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

	override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){

		loops.foreach( _.audioIO(in,out,numOut,numSamples) )
	}

	override def draw(){
		for( i<-(0 until plots.size)){
			val p = plots(i)
			p.setSamples(loops(i).b.samples, 0, loops(i).b.curSize) //loops(i).b.rMin,loops(i).b.rMax)
			p.setCursor(2,loops(i).b.rPos.toInt)
			p.draw()
		}
	}
}