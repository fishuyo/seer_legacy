
// package seer
// package video

// import scala.collection.mutable.Queue

// import akka.actor.Actor
// import akka.actor.Props
// import akka.event.Logging
// import akka.actor.ActorSystem

// // import com.xuggle.xuggler.Global;
// // import com.xuggle.xuggler.IContainer;
// // import com.xuggle.xuggler.IPacket;
// // import com.xuggle.xuggler.IPixelFormat;
// // import com.xuggle.xuggler.IStream;
// // import com.xuggle.xuggler.IStreamCoder;
// // import com.xuggle.xuggler.ICodec;
// // import com.xuggle.xuggler.IVideoPicture;
// // import com.xuggle.xuggler.IVideoResampler;
// // import com.xuggle.xuggler.Utils;

// import com.badlogic.gdx.graphics.Pixmap



// // package io.humble.video.demos;

// import _root_.io.humble.video.Decoder;
// import _root_.io.humble.video.Demuxer;
// import _root_.io.humble.video.DemuxerStream;
// import _root_.io.humble.video.Global;
// import _root_.io.humble.video.Media;
// import _root_.io.humble.video.MediaDescriptor;
// import _root_.io.humble.video.MediaPacket;
// import _root_.io.humble.video.MediaPicture;
// import _root_.io.humble.video.MediaPictureResampler;
// import _root_.io.humble.video.PixelFormat;
// import _root_.io.humble.video.Rational;
// // import _root_.io.humble.video.awt.ImageFrame;
// // import _root_.io.humble.video.awt.MediaPictureConverter;
// // import _root_.io.humble.video.awt.MediaPictureConverterFactory;



// import java.awt.image.BufferedImage;
// import java.io.IOException;

// import org.apache.commons.cli.CommandLine;
// import org.apache.commons.cli.CommandLineParser;
// import org.apache.commons.cli.HelpFormatter;
// import org.apache.commons.cli.Options;
// import org.apache.commons.cli.ParseException;



// class VideoPlayer2(val filename:String) {

//   var playing = false
//   var queue = new Queue[MediaPicture]
//   var frame:MediaPicture = _

//   var demuxer = Demuxer.make();
//   demuxer.open(filename, null, false, true, null, null);

//   val numStreams = demuxer.getNumStreams();

//   var dtAccum = 0.0

//   /*
//    * Iterate through the streams to find the first video stream
//    */
//   var videoStreamId = -1;
//   var streamStartTime:Long = Global.NO_PTS;
//   var videoDecoder:Decoder = null;
//   for( i <- ( 0 until numStreams)){
//     val stream = demuxer.getStream(i);
//     streamStartTime = stream.getStartTime();
//     val decoder = stream.getDecoder();
//     if (decoder != null && decoder.getCodecType() == MediaDescriptor.Type.MEDIA_VIDEO) {
//       videoStreamId = i;
//       videoDecoder = decoder;
//     }
//   }
//   if (videoStreamId == -1)
//     throw new RuntimeException("could not find video stream in container: "+filename);

    
//   videoDecoder.open(null, null);

//   var width = videoDecoder.getWidth()
//   var height = videoDecoder.getHeight()
    
//   var picture = MediaPicture.make(
//       videoDecoder.getWidth(),
//       videoDecoder.getHeight(),
//       videoDecoder.getPixelFormat());

//   /** A converter object we'll use to convert the picture in the video to a BGR_24 format that Java Swing
//    * can work with. You can still access the data directly in the MediaPicture if you prefer, but this
//    * abstracts away from this demo most of that byte-conversion work. Go read the source code for the
//    * converters if you're a glutton for punishment.
//    */
//   // val converter = 
//   //     MediaPictureConverterFactory.createConverter(
//   //         MediaPictureConverterFactory.HUMBLE_BGR_24,
//   //         picture);

//   var resampler:MediaPictureResampler = null;
//   if (videoDecoder.getPixelFormat() != PixelFormat.Type.PIX_FMT_RGB24){
//     // if this stream is not in BGR24, we're going to need to
//     // convert it.  The VideoResampler does that for us.
//     resampler = MediaPictureResampler.make(videoDecoder.getWidth(), 
//         videoDecoder.getHeight(), PixelFormat.Type.PIX_FMT_RGB24,
//         videoDecoder.getWidth(), videoDecoder.getHeight(), videoDecoder.getPixelFormat(), 0);
//     if (resampler == null)
//       throw new RuntimeException("could not create color space " +
//           "resampler for: " + filename);
//     resampler.open()
//   }


//   var packet:MediaPacket = MediaPacket.make();

//   // val firstTimestampInStream:Long = Global.NO_PTS;
//   // var systemClockStartTime:Long = 0;

//   // Calculate the time BEFORE we start playing.
//   var systemStartTime:Long = System.nanoTime();
//   // Set units for the system time, which because we used System.nanoTime will be in nanoseconds.
//   val systemTimeBase = Rational.make(1, 1000000000);
//   // All the MediaPicture objects decoded from the videoDecoder will share this timebase.
//   val streamTimebase = videoDecoder.getTimeBase();


//   // Make pixmap to hold texture data
//   var pixmap = new Pixmap(videoDecoder.getWidth(), videoDecoder.getHeight(), Pixmap.Format.RGB888)

//   val reader = Video.system.actorOf(Props( new VideoBufferingActor2(this) ), name = "reader")
//   reader ! "decodeFrame"


//   def togglePlaying(){ playing = !playing }
//   def playing(b:Boolean){ playing = b }

//   def animate(dt:Float){

//     if( !playing ) return

//     // val framerate = 30f //videoDecoder.getTimeBase().getDouble()
//     val framerate = 1f/videoDecoder.getTimeBase().getDouble()
//     val timeStep = 1f/framerate
//     dtAccum += dt.toDouble
//     var newFrame = false
//     if( dtAccum > timeStep ){
//       dtAccum -= timeStep

//       if( !queue.isEmpty){
//         frame = queue.dequeue
//         newFrame = true
//       }
//     }

//     if( newFrame && frame != null){
//       val bb = pixmap.getPixels()
//       if( bb == null) return

//       bb.put(frame.getData(0).getByteArray(0,width*height*3)) //.getByteBuffer())
//       bb.rewind()
//     }
//   }

//   def close(){
//     // if (videoDecoder != null)
//     // {
//       // videoDecoder.close();
//       // videoDecoder = null;
//     // }
//     if (demuxer !=null)
//     {
//       demuxer.close();
//       demuxer = null;
//     }
//   }

// }

// class VideoBufferingActor2(val player:VideoPlayer2) extends Actor {

//   import player._

//   // override def preStart(){
//   // }

//   def receive = {
//     case "decodeFrame" => {


//       // while(demuxer.read(packet) >= 0) {
//       //   if (packet.getStreamIndex() == videoStreamId)
//       //   {
//       //     int offset = 0;
//       //     int bytesRead = 0;
//       //     do {
//       //       bytesRead += videoDecoder.decode(picture, packet, offset);
//       //       if (picture.isComplete()) {
//       //         image = displayVideoAtCorrectTime(streamStartTime, picture,
//       //             converter, image, window, systemStartTime, systemTimeBase,
//       //             streamTimebase);
//       //       }
//       //       offset += bytesRead;
//       //     } while (offset < packet.getSize());
//       //   }
//       // }
//       var newFrame = false


//       if( demuxer.read(packet) >= 0){

//         if (packet.getStreamIndex() == videoStreamId){

//           // val picture = IVideoPicture.make(videoCoder.getPixelType(),
//               // videoCoder.getWidth(), videoCoder.getHeight());

//           var offset = 0;
//           while(offset < packet.getSize()){

//             val bytesDecoded = videoDecoder.decode(picture, packet, offset);
//             if (bytesDecoded < 0)
//               throw new RuntimeException("got error decoding video in: "
//                   + filename);
//             offset += bytesDecoded;

//             if (picture.isComplete()){
//               var newPic = picture;

//               if (resampler != null){
//                 // we must resample
//                 newPic = MediaPicture.make(picture.getWidth(), picture.getHeight(), resampler.getOutputFormat());
//                 if (resampler.resample(newPic, picture) < 0)
//                   throw new RuntimeException("could not resample video from: "
//                       + filename);
//               }
//               if (newPic.getFormat() != PixelFormat.Type.PIX_FMT_RGB24)
//                 throw new RuntimeException("could not decode video" +
//                     " as BGR 24 bit data in: " + filename);

//               queue += newPic
//               newFrame = true
//             }
//           }
//         }
//       } else { demuxer.seek(videoStreamId,0,0,0,0)}
      
//       // do{Thread.sleep(10)}
//       if(newFrame && queue.size > 10) Thread.sleep(10)
//       while( queue.size > 20) Thread.sleep(10)
//       println(queue.size)
//       // while( queue.size > 60) Thread.sleep(10)
//       self ! "decodeFrame"
//     }
//     case "close" => ()
//   }

// }






// // public class DecodeAndPlayVideo {

// //   /**
// //    * Opens a file, and plays the video from it on a screen at the right rate.
// //    * @param filename The file or URL to play.
// //    */
// //   private static void playVideo(String filename) throws InterruptedException, IOException {
// //     /*
// //      * Start by creating a container object, in this case a demuxer since
// //      * we are reading, to get video data from.
// //      */
    
// //   }

// //   /**
// //    * Takes the video picture and displays it at the right time.
// //    */
// //   private static BufferedImage displayVideoAtCorrectTime(long streamStartTime,
// //       final MediaPicture picture, final MediaPictureConverter converter,
// //       BufferedImage image, final ImageFrame window, long systemStartTime,
// //       final Rational systemTimeBase, final Rational streamTimebase)
// //       throws InterruptedException {
// //     long streamTimestamp = picture.getTimeStamp();
// //     // convert streamTimestamp into system units (i.e. nano-seconds)
// //     streamTimestamp = systemTimeBase.rescale(streamTimestamp-streamStartTime, streamTimebase);
// //     // get the current clock time, with our most accurate clock
// //     long systemTimestamp = System.nanoTime();
// //     // loop in a sleeping loop until we're within 1 ms of the time for that video frame.
// //     // a real video player needs to be much more sophisticated than this.
// //     while (streamTimestamp > (systemTimestamp - systemStartTime + 1000000)) {
// //       Thread.sleep(1);
// //       systemTimestamp = System.nanoTime();
// //     }
// //     // finally, convert the image from Humble format into Java images.
// //     image = converter.toImage(image, picture);
// //     // And ask the UI thread to repaint with the new image.
// //     window.setImage(image);
// //     return image;
// //   }
  



// // }
