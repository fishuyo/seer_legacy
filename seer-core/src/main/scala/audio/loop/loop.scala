package com.fishuyo
package audio

class LoopBuffer( var maxSize:Int = 0) {
  
  var samples = new Array[Float](maxSize)
  var curSize = 0
  var (rPos, wPos) = (0.f,0)
  var (rMin, rMax) = (0,0) //read limiters
  var times = 0
  var speed = 1.f

  //resize sample buffer, default doubles current buffer size
  def resize(size:Int){
    if(size <= maxSize) return
    var b = new Array[Float](size)
    for( i <- (0 until curSize)) b(i) = samples(i)
    maxSize = size; 
    samples = b;
  } 

  //append sample
  def apply( s:Float ){
    if(curSize+1 > maxSize) resize(2*maxSize)
    curSize += 1
    samples(curSize) = s
    rMax += 1
  }
  //read one sample
  def apply() = {
    if(rPos >= rMax){ 
       rPos = rMin;
       times+=1;
    }
    val s = readSampleAt(rPos)
    rPos += 1
    s
  }
  //read one sample reverse
  def r() = {
    if(rPos < rMin){ 
       rPos = rMax-1;
       times+=1;
    }
    val s = readSampleAt(rPos)
    rPos -= 1
    s
  }
  
  //write sample data, appended to buffer
  def append( in:Array[Float], numSamples:Int ){
    if( maxSize == 0 ) return
    else if( curSize + numSamples >= maxSize ) resize(2*maxSize)
    for( i <- (0 until numSamples)) samples(curSize+i) = in(i)
    if( rMax == curSize ) rMax += numSamples;
    curSize += numSamples;
  }

  def readSampleAt(s:Float) = {
    val i = s.toInt
    val i2 = if( i == rMax-1) 0 else i+1
    val f = s - i
    samples(i)*(1.f-f) + samples(i2)*f
  }

  def addSampleAt(s:Float,v:Float) = {
    val i = s.toInt
    val i2 = if( i == rMax-1) 0 else i+1
    val f = s - i
    samples(i) += v*(1.f-f)
    samples(i2) += v*f
  }

  //read sample data at r_head, between r_min and r_max
  def read( out:Array[Float], numSamples:Int, gain:Float=1.f){
    if( rPos < rMin || rPos >= rMax){ rPos = rMin; times+=1; }

    var read = 0
    while( read < numSamples){
      while( rPos < rMax ){
        out(read) = readSampleAt(rPos) * gain
        rPos += speed
        read += 1
        if( read == numSamples ) return
      }
      times += 1
      rPos = rPos - rMax + rMin
    }

    // val overlap = rPos + numSamples - rMax;

    // if( overlap >= (rMax-rMin) ) return;
    // if(overlap > 0){
    //   var i = 0
    //   while( i < (numSamples - overlap)){
    //     out(i) = samples(rPos+i) * gain
    //     i += 1
    //   }
    //   for( j <- (0 until overlap)) out(i+j) = samples(rMin+j) * gain
    
    //   rPos = overlap
    //   times+=1
      
    // }else{
    //   for( i <- (0 until numSamples)) out(i) = samples(rPos+i) * gain
    //   rPos += numSamples
    // }
  }

  def readR( out:Array[Float], numSamples:Int, gain:Float=1.f ){
    this.synchronized{
      if( rPos < rMin || rPos >= rMax){ rPos = rMax-1; times+=1; }

      var read = 0
      while( read < numSamples){
        while( rPos >= rMin ){
          out(read) = readSampleAt(rPos) * gain
          rPos -= speed
          read += 1
          if( read == numSamples ) return
        }
        times += 1
        rPos = rPos + rMax - rMin
      }
    //   val underlap = rPos + 1 - numSamples - rMin
      
    //   if( underlap <= -(rMax-rMin) ) return
    //   var idx = rPos
      
    //   println( underlap + " " + rPos + " " + rMin + " " + rMax)
    //   if(underlap < 0){
    //     for( i <- (0 until numSamples + underlap)){
    //       out(i) = samples(idx) * gain
    //       idx -= 1
    //     }
    //     idx = rMax - 1
    //     for( i <- (numSamples+underlap until numSamples)){
    //       out(i) = samples(idx) * gain
    //       idx -= 1
    //     }
        
    //     rPos = rMax - 1 + underlap
    //     times+=1;
        
    //   }else{
    //     for( i <- (0 until numSamples)){
    //       out(i) = samples(idx) * gain
    //       idx -= 1
    //     }
    //     rPos -= numSamples
    //   }
    }
  }
  
  def addFrom( from:Array[Float], numSamples:Int, off:Float=0.f ){
    
    var offset = off
    if( offset < rMin || offset >= rMax) offset = rMin

    var read = 0
    while( read < numSamples){
      while( offset < rMax ){
        addSampleAt(offset, from(read) )
        offset += speed
        read += 1
        if( read == numSamples ) return
      }
      offset = offset - rMax + rMin
    }


    // val overlap = offset + numSamples - rMax
    
    // //assert( overlap < (int)(rMax-rMin) );
    // if(overlap > 0){
    //   var i = 0
    //   while( i < numSamples - overlap){
    //     samples(offset+i) += from(i)
    //     i += 1
    //   }
    //   for( j <- (0 until overlap)){
    //     samples(rMin+j) += from(i+j)
    //   }
      
    // }else{
    //   for( i <- (0 until numSamples)){
    //     samples(offset+i) += from(i)
    //   }
    // }
  }
  def addFromR( from:Array[Float], numSamples:Int, off:Float ){

    var offset = off
    if( offset < rMin || offset >= rMax) offset = rMax-1;

    var read = 0
    while( read < numSamples){
      while( offset >= rMin ){
        addSampleAt(offset, from(read) )
        offset -= speed
        read += 1
        if( read == numSamples ) return
      }
      offset = offset + rMax - rMin
    }

    // val underlap = offset + 1 - numSamples - rMin;
    
    // if( underlap <= -(rMax-rMin) ) return
    // var idx = offset
    
    // if(underlap < 0){
    //   for( i <- (0 until numSamples + underlap)){
    //     if( idx < 0) println("!!!underlap: " + underlap + " i: " + i + " off: " + offset + " rpos: " + rPos)
    //     samples(idx) += from(i)
    //     idx -= 1
    //   }
    //   idx = rMax-1
    //   for( i <- (numSamples + underlap until numSamples)){
    //     samples(idx) += from(i)
    //     idx -= 1
    //   }
    // }else{
    //   for( i <- (0 until numSamples)){
    //     samples(idx) += from(i)
    //     idx -= 1
    //   }
    // }
  }
  
  def applyGain( gain:Float, numSamples:Int, offset:Float ){
    var num = numSamples//*speed
    var off = offset
    if( offset < rMin ) off = rMin
    while( num > 0 ){
      if(off >= rMax) off = rMin
      samples(off.toInt) *= gain 
      num -= 1
      off += 1   
    }
  }
  
  //get root mean square of numSamples starting at offset
  def getRMS( numSamples:Int, offset:Int){

  }
  //get root mean square of numSamples ago
  def getRMSR( numSamples:Int ){

  }

  //read between b1 and b2, uses smaller value as min (in samples)
  def setBounds( b1:Int, b2:Int){
    this.synchronized{
      var (min,max) = (0,0)
      if(b1 < b2){
        min = b1
        max = b2+1
      }else{
        min = b2
        max = b1+1
      }
      if( min < 0 ) min = 0
      if( max > curSize ) max = curSize-1
      rMin = min; rMax = max
    }
  }

}


class Loop( var seconds:Float=0.f, var sampleRate:Int=44100) extends Gen {
  
  var b = new LoopBuffer()
  var numSamples = (seconds * sampleRate).toInt
  var times = 0
  var sync = 0

  var (gain, pan, decay, rms) = (1.f,0.5f,0.5f,0.f)
  var (recording,playing,stacking,reversing,undoing) = (false,false,false,false,false)
  var recOut = false
  var iobuffer = new Array[Float](2048)

  var dirty = true

  allocate(numSamples)

  gen = ()=>{
    if( playing && numSamples > 0){
      if( reversing ){
        b.r()
      }else{
        b()
      }
    }
    0.f
  }
  
  def allocate( n:Int ){
    b.resize(n)
    numSamples = n
  }
  
  def play(){ playing = true; recording = false}
  def play(t:Int){ b.times=0; times = t; play() }
  def stop(){ playing = false; recording = false}
  def rewind(){ b.rPos = b.rMin }
  def record(){ recording = true; playing = false; dirty = true }
  def stack() = { stacking = !stacking; dirty = true }
  def reverse() = reversing = !reversing
  def reverse(b:Boolean) = reversing = b
  def undo() = {}
  def clear() = {
    b.rMin = 0
    b.rMax = 0
    b.rPos = 0
    b.curSize = 0
  }

  def duplicate(times:Int){
    val size = b.curSize
    for( i <- (0 until times)) b.append( b.samples, size)
    dirty = true
  }

  var onDone = ()=>{}
  var onSync = ()=>{}

  
  override def audioIO( in:Array[Float], out:Array[Array[Float]], numOut:Int, count:Int ) = {
    var lPos = 0.f
    val l = (1.f - pan )
    val r = pan
    
    if(recording){ //fresh loop

      b.append( in, count )
      
    }else if(playing && numSamples > 0){ //playback and stack
      
      lPos = b.rPos
      
      if(reversing){
        
        b.readR( iobuffer, count, gain )
        
        if(stacking){ 
          b.applyGain( decay, count, b.rPos+1 )
          b.addFromR( in, count, lPos )
        }
        
      }else {
        
        b.read( iobuffer, count, gain )
        
        if(stacking){
            b.applyGain( decay, count, lPos)
            b.addFrom( in, count, lPos )
        }     
      }
      
      //up mix to 2 channels
      for( i <- (0 until count)){
        out(0)(i) += iobuffer(i)*l
        out(1)(i) += iobuffer(i)*r
      }
      
      if( sync != b.times){
        sync = b.times
        onSync()
      }
      if( times > 0 && b.times >= times ){
        b.times = 0; times = 0;
        stop()
        onDone()
      }
      
    }//end else if(playing)
  }
  //int load( const char* filename );
  //int save( const char* filename );

}



// #define RW_SIZE 4096

// float LoopBuffer::getRMS(unsigned int numSamples, unsigned int offset){
//   if( curSize == 0 ) return 0.f;
//   double sum = 0.0;
//   if( offset < rMin ) offset = rMin;
//   unsigned int i = numSamples;
//   while( i-- ){
//     if(offset >= rMax) offset = rMin;
//     float s = samples[offset++];
//     sum += s*s;    
//   }
//   //return sum / numSamples;
//   return (float) sqrt (sum / numSamples); 
// }

// float LoopBuffer::getRMSR(unsigned int numSamples){
//   unsigned int offset = rPos - numSamples;
//   while( offset < 0 ) offset += rMax;
  
//   return getRMS(numSamples, offset); 
// }

// void LoopBuffer::setBounds( unsigned int b1, unsigned int b2=0){
//   rMin = b1 < b2 ? b1 : b2;
//   rMax = b1 < b2 ? b2 : b1;
//   if( rMax > curSize ) rMax = curSize;
// }


/*
int Loop::load( const char* filename ){

  SNDFILE *file;
  SF_INFO sfinfo;
  float buffer[RW_SIZE];
  sf_count_t read;

  if( !(file = sf_open(filename, SFM_READ, &sfinfo))){
    //std::cout << "Error loading file " << filename << std::endl;
    return -1;
  }

  sampleRate = sfinfo.samplerate;
  allocate(sfinfo.frames);

  while( (read = sf_readf_float( file, buffer, RW_SIZE/sfinfo.channels )) > 0 ){
    switch( sfinfo.channels ){
      case 1: 
        b[0].append( buffer, read ); break;
      case 2:
        //mix to single channel
        for( int i=0; i < read/2; i++) buffer[i] = (buffer[2*i]+buffer[2*i+1]) / 2.f;
        b[0].append( buffer, read/2 ); break;
      default: return -2; break;
    }
  }
  sf_close(file);
  return 0;
}

int Loop::save( const char* filename ){
 
  SNDFILE *file;
  SF_INFO sfinfo;
  sf_count_t writ;

  sfinfo.samplerate = sampleRate;
  sfinfo.frames = numSamples;
  sfinfo.channels = 1;
  sfinfo.format = SF_FORMAT_WAV | SF_FORMAT_PCM_16;

  if( !(file = sf_open(filename, SFM_WRITE, &sfinfo))){
    //std::cout << "Error opening file " << filename << std::endl;
    return -1;
  }

  writ = sf_writef_float( file, b[0].samples, numSamples );
  sf_close( file );
  return 0;
}*/

