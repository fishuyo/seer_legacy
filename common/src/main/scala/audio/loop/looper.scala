
package com.fishuyo
package audio

class Looper extends AudioSource {
	var loops = List[Loop]()
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

	def newLoop() = loops = new Loop(10.f) :: loops
	def play(i:Int, times:Int=0) = {
		if( times == 0) loops(i).play
		else loops(i).play(times)
	}
	def stop(i:Int) = loops(i).stop
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

	def switchTo(i:Int) = {
		play(master,1);
		loops(master).onDone = ()=>{loops(i).play; loops(master).onDone=non; master=i}
	}

	override def audioIO( in:Array[Float], out:Array[Float], numSamples:Int){

		loops.foreach( _.audioIO(in,out,numSamples) )
	}
}