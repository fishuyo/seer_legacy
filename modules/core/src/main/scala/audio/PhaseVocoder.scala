
package com.fishuyo.seer
package audio

import spatial._
// import graphics._
// import util._

// import com.badlogic.gdx.graphics.Pixmap
// import com.badlogic.gdx.graphics.{Texture => GdxTexture}

import edu.emory.mathcs.jtransforms.fft._


object FFT {
	var length = Audio().bufferSize
	var rect = Array.fill(length)(1f)
	var hann = Array.fill(length)(0f)
	for(i<-(0 until length)) hann(i) = 0.5f*(1f-math.cos(2.0*math.Pi*i/(length-1.0)).toFloat)

	def window = hann

	var fft = new FloatFFT_1D(length)

	def resize(size:Int){
		length = size
		fft = new FloatFFT_1D(length)
		rect = Array.fill(length)(1f)
		hann = Array.fill(length)(0f)
		for(i<-(0 until length)) hann(i) = 0.5f*(1f-math.cos(2.0*math.Pi*i/(length-1.0)).toFloat)

	}

	def forward(a:Array[Float]) = fft.realForward(a)
	def reverse(a:Array[Float]) = fft.realInverse(a,true)
}




class PhaseVocoder extends AudioSource {

	var length = FFT.length
	var hopFactor = 1f/4f
	def hopSize = (length*hopFactor).toInt
	var numWindows = 0
	var spectrumData:Array[Array[Float]] = null
	var prevBuffer = new Array[Float](length)
	var prevPhase = new Array[Float](length/2+1)
	var phaseAccum = new Array[Float](length/2+1)

	var (minWin, maxWin) = (0,0)
	var nextWindow = 0f
	var timeShift = 1f
	var pitchShift = 1f
	var gain = 1f

	var update = false

	var convert = true
	def convert(b:Boolean){ convert = b }

	def timeShift(f:Float){ timeShift = f}
	def pitchShift(f:Float){ pitchShift = f}
	def gain(f:Float){ gain = f}

	def clear(){ spectrumData = null }

	def setBounds(b1:Float,b2:Float){
		var min = (if( b1 < b2) b1 else b2)
		var max = (if( b1 < b2) b2 else b1)
		minWin = (min*numWindows).toInt
		maxWin = (max*numWindows).toInt
	}

	def setSamples(buffer:Array[Float], size:Int){
		val samples = buffer.take(size)
		numWindows = samples.length / hopSize		
		// println( s"Analyzing ${samples.length} samples($hopSize) $numWindows windows")				

		// use sliding window over sample data
		spectrumData = samples.sliding(length,hopSize).map( s => {
			val windowed = s.padTo(length,0f) zip FFT.window map { case (a,b) => a*b } // apply window
			// val shiftedWin = Array.concat( windowed.takeRight(length/2), windowed.take(length/2)) // shift window
			FFT.forward(windowed) // do fft in place
			prevPhase = new Array[Float](length/2+1) // new empty phase buffer
			val data = convertMagPhase(windowed) // convert mag phase to mag freq
			data // return converted data
		}).toArray

		numWindows = spectrumData.length
		maxWin = numWindows
		// println(s"spectrumData.length: ${spectrumData.length}")
		update = true
	}

	def convertMagPhase(win:Array[Float]):Array[Float] = {

		// if(!convert) return win
		var (phase, phaseDiff) = (0f,0f)
		var expPhaseDiff = (2.0*math.Pi*hopFactor).toFloat
		var freqPerBin = 44100f / length

		val out = new Array[Float](length+2)

		//BERNSEE METHOD
		for(i <-(0 to length/2)){		//Go from 0 to N/2 because fftw places (N/2)+1 complex values in spectral array using r2c fft

			var (re,im) = (0f,0f)
			if(i == 0){  
				re = win(0)
				im = 0f
			} else if( i < length/2){
				re = win(2*i)
				im = win(2*i+1)
			} else {
				re = win(1)
				im = 0f
			}

			//compute magnitude from real and imaginary components
			out(2*i) = math.sqrt(re*re + im*im).toFloat

			// compute phase from real and imaginary components
			phase = math.atan2(im,re).toFloat

			// get phase difference
			phaseDiff = phase - prevPhase(i)

			// store current bin phase for next window
			prevPhase(i) = phase

			// subtract expected phase difference according to hop size --> expPhaseDiff = TWOPI*(window_step/fft_length);
			phaseDiff = phaseDiff - (i*expPhaseDiff)

			// unwrap phase difference
			while(phaseDiff > math.Pi) phaseDiff -= 2.0f * math.Pi.toFloat
			while (phaseDiff < -math.Pi) phaseDiff += 2.0f * math.Pi.toFloat

			//Get deviation from bin frequency from the +/- Pi interval
			phaseDiff = phaseDiff/expPhaseDiff  // 2.0*math.Pi*hopFactor

			//compute the i-th partials' true frequency
			if( !convert ){
				out(2*i+1) = phase
			} else {
				out(2*i+1) = phaseDiff*freqPerBin + i*freqPerBin
			}
		}
		out
	}

	def unconvertMagPhase(win:Array[Float]):Array[Float] = {

		// if(!convert) return win
		var (phase, phaseDiff) = (0f,0f)
		var expPhaseDiff = (2.0*math.Pi*hopFactor).toFloat
		var freqPerBin = 44100f / length

		val out = new Array[Float](length)

		//BERNSEE METHOD
		for(i <-(0 to length/2)){		

			/// get frequency from synthesis array
			phaseDiff = win(2*i+1)

			// subtract bin mid frequency
			phaseDiff -= i*freqPerBin

			// get bin deviation from freq deviation
			phaseDiff /= freqPerBin

			// take hopFactor into account
			phaseDiff = phaseDiff*expPhaseDiff

			// add the overlap phase advance back in
			phaseDiff += i*expPhaseDiff

			// accumulate delta phase to get bin phase
			phaseAccum(i) += phaseDiff 

			if(!convert) phase = win(2*i+1)
			else phase = phaseAccum(i)

			// get real and imag part
			if( i < length/2){
				out(2*i) = win(2*i)*math.cos(phase).toFloat
				out(2*i+1) = win(2*i)*math.sin(phase).toFloat
				// if( i == 0) println(s"bin 0 im: ${out(1)}, phasediff: $phase")
			} else {
				out(1) = win(2*i)*math.cos(phase).toFloat
				// println(s"bin l/2 im: ${win(2*i)*math.sin(phase)}, phasediff: $phase")
			}
		}
		out
	}

	def shiftPitch(data:Array[Float]): Array[Float] = {

		val out = new Array[Float](length+2)

		// for each bin shift bin indices up by pitch scale amount
		// if converting to actually frequency also scale the frequency
		for (i <- (0 to length/2)){ 
			var index = math.max((i*pitchShift).toInt,0)
			if (index <= length/2) { 
				out(2*index) += data(2*i)
				if(!convert) out(2*index+1) =  data(2*i+1)
				else out(2*index+1) =  data(2*i+1) * pitchShift
			} 
		}
		out
	}

	def resynth(index:Float) = {

		//Copy samples from last buffer
		val out = prevBuffer
		prevBuffer = new Array[Float](length)

		// for each overlapping window accumulate resynthesis
		for( overlap <- (0 until length/hopSize)){

			var win = (index.toInt + overlap) % numWindows
			if( win == 0 ) phaseAccum = new Array[Float](length/2+1)

			var data:Array[Float] = null
			try{ data = spectrumData(win).clone }
			catch { case e:Exception => println(e); println(s"fft win index: $win")}

			val shifted = shiftPitch(data)
			val spect = unconvertMagPhase(shifted)
			FFT.reverse(spect)

			// val shifted = Array.concat( spect.takeRight(length/2), spect.take(length/2))
			val windowed = spect zip FFT.window map { case (a,b) => a*b }

			// accumulate overlapped windows
			var c = 0
			for( i<-(overlap * hopSize until length )){
				out(i) += windowed(c)
				c += 1
			}

			c = 0
			for( i<-(length - (overlap * hopSize) until length )){
				prevBuffer(c) += windowed(i)
				c += 1
			}
		}
		out

		// for( int k = 0; k < totOverlap; k++){
			
		// 	if(startWin + k >= fft.numWin) startWin = -k;		//loop first window to follow last
		// 	if(startWin + k == 0) zeromem( phaseAccum, (fft.length/2+1) * sizeof(float)); //if first window zero phaseAccum

		// 	pitchShift(spectrumData[startWin+k]);		//applies pitch shift and puts in outSpectrum

		// 	//Convert back to complex format and execute ifft
		// 	unconvertMagPhase(outSpectrum);
		// 	fftwf_execute(fft.rplan);

		// 	//Shift data back around and apply window for overlap-add
		// 	shiftWindow(fft.in);
		// 	applyWindow(fft.in);

		// 	//Add correct portion of resynthesis to output
		// 	int counter = 0;
		// 	for(int i = k*fft.hopSize; i < fft.length; i++)
		// 		output[0][i] += scale*fft.in[counter++];

		// 	counter = 0;
		// 	for(int i = (totOverlap-k)*fft.hopSize; i < fft.length; i++)
		// 		prevBuffer[counter++] += scale*fft.in[i];

		// }
	}

  override def audioIO( io:AudioIOBuffer ){ 
  	// in:Array[Float], out:Array[Array[Float]], numOut:Int, numSamples:Int){
		val in = io.inputSamples(0)
		val out = io.outputSamples
		val numSamples = io.bufferSize

  	if( spectrumData == null ) return
  	val o = resynth(nextWindow)
	  for( i <- (0 until numSamples)){
	  	val s = gain*o(i)
      out(0)(i) += s
      out(1)(i) += s
    }

		nextWindow += timeShift * (length/hopSize)
		if(nextWindow < minWin){ 
			nextWindow = maxWin + nextWindow
			if(nextWindow < minWin) nextWindow = minWin
			phaseAccum = new Array[Float](length/2+1)
		}
		if( nextWindow >= maxWin){
			nextWindow = nextWindow - maxWin + minWin
			if(nextWindow < minWin) nextWindow = minWin
			if(nextWindow >= maxWin) nextWindow = maxWin-1
			phaseAccum = new Array[Float](length/2+1)
		}
  }
}

// class Spectrogram extends Drawable {

// 	var numWin = 0
// 	var numBins = 0

// 	var pix:Pixmap = null
// 	var texture:GdxTexture = null

// 	var tID = 0
// 	val model = Plane()
// 	model.scale.set(0.25)
// 	model.material = new BasicMaterial()
// 	model.material.textureMix = 1f

// 	def setData(data:Array[Array[Float]], complex:Boolean=false){
// 		numWin = data.length
// 		numBins = data(0).length / 2

// 		pix = new Pixmap(numWin,numBins, Pixmap.Format.RGBA8888)
// 		pix.setColor(1,1,1,1)
// 		// pix.fill()
// 		for( x <- (0 until numWin); y <- (0 until numBins)){
// 			val (re,im) = (data(x)(2*y),data(x)(2*y+1)) // re/im or mag/phase
// 			val mag = (if(complex) math.sqrt(re*re+im*im) else re)
// 			val c = math.max(1.0 - mag,0f)
// 			// val c = clamp(1f-(10*math.log10(mag)),0f,1f)
// 			// val f = math.min((im / 22050f * numBins),numBins-1)
// 			pix.setColor(c,c,c,c)
// 			if(complex) pix.drawPixel(x,y) //f.toInt)
// 			else pix.drawPixel(x,y) //f.toInt)
// 		}

// 		texture = new GdxTexture(pix)
// 		texture.setFilter( GdxTexture.TextureFilter.Linear, GdxTexture.TextureFilter.Linear)
// 		model.material.texture = Some(texture)
// 	}

//   override def init(){
//   }
// 	override def draw(){
// 		model.draw()
// 	}

// }