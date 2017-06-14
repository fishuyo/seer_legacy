
// package com.fishuyo.seer
// package cv


// import org.opencv.core._
// import org.opencv.imgproc.Imgproc
// import org.opencv.highgui.Highgui
// import org.opencv.calib3d.Calib3d
// import org.opencv.objdetect._

// import scala.collection.JavaConversions._
// import scala.collection.mutable.ListBuffer


// //Camera Calibration using a chessboard pattern

// class FaceDetector(var faceHeight:Double)(implicit camera:CalibratedCamera) {

// 	var faceFile = "haarcascade_frontalface_default.xml"
// 	var eyeFile = "haarcascade_eye.xml"

//   val small = new Mat() //downscaled input image for later
//   val faceClassifier = new CascadeClassifier()
//   val eyeClassifier = new CascadeClassifier()
//   faceClassifier.load(faceFile)
//   eyeClassifier.load(eyeFile)

//   var face = new Rect()
//   var eyes = new Rect()

//   var (z,l,r,b,t) = (0.0,0.0,0.0,0.0,0.0) //position of face rectangle in world coords;

//   def load(f:String,e:String){
// 		var faceFile = f
// 		var eyeFile = e

// 	  faceClassifier.load(faceFile)
// 	  eyeClassifier.load(eyeFile)
//   }

//   //detect
//   def apply( image:Mat ) = {

// 	  val faces = new MatOfRect()
// 	  val gray = new Mat()
// 	  Imgproc.cvtColor( image, gray, Imgproc.COLOR_BGR2GRAY )
// 	  Imgproc.resize( gray, small, new Size(), .5, .5, 0 )

// 	  //equalizeHist( small, small );

// 	  faceClassifier.detectMultiScale( small, faces, 1.1, 3, Objdetect.CASCADE_SCALE_IMAGE | Objdetect.CASCADE_FIND_BIGGEST_OBJECT, new Size(30,30), new Size(1000,1000) );

// 		val list = faces.toList()
// 	  if( list.size() > 0){
// 	    face = list.get(0)
// 	    face.x *= 2;
// 	    face.y *= 2;
// 	    face.width *= 2;
// 	    face.height *= 2; 

// 		  val intr = new Array[Double](9)
// 		  camera.intrinsic.get(0,0,intr)

// 		  val fy = intr(4) //(1,1);
// 		  val fx = intr(0) //(0,0);
// 		  val pp = new Point( intr(6) /*at(0,2)*/, intr(7) /*at(1,2)*/ )

// 		  val rect = face;
// 		  z = fy * faceHeight / (1.0 * rect.height);
// 		  b = z * (pp.y - rect.y ) / fy;
// 		  t = z * (pp.y - (rect.y + rect.height) ) / fy;
// 		  l = z * (rect.x - pp.x) / fx;
// 		  r = z * (rect.x + rect.width - pp.x) / fx;
// 	  }                   

// 	  list.size();
//   }

// }