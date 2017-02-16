
package com.fishuyo.seer
package cv

import spatial.Vec3

import org.opencv.core._
import org.opencv.imgproc.Imgproc
import org.opencv.highgui.Highgui
import org.opencv.calib3d.Calib3d
import org.opencv.video._

import scala.collection.JavaConversions._
import scala.collection.mutable.ListBuffer


//Camera Calibration using a chessboard pattern

class CalibratedCamera {


  //member data
  var init = false
  var intrinsic = new MatOfDouble(0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0)
  var distortion = new MatOfDouble(0.0,0.0,0.0,0.0,0.0)
  var imageSize = new Size()
  var (fovx, fovy, focalLength) = (0.0,0.0,0.0);
  var near = 0.1
  var far = 1000.0
  var aspect = 0.0

  var pp = new Point()
  var error = 0.0

  var tvec = new Mat()
  var rvec = new Mat() //stores current extrinsic parameters from calculateExtrinsics function

  //Contructors
  def this(path:String, size:Size ){
  	this()
  	if( loadParams( path ) ){
	  	imageSize = size
	  	init = true
  	}
  }


  //member functions
  def calibrateFromBoardImages( images:Array[Mat], boardSize:Size, squareSize:Double ){
  	val corners = new java.util.ArrayList[MatOfPoint2f]()
  	val points = new java.util.ArrayList[Mat]()
  	corners.add(new MatOfPoint2f())

	  imageSize = images(0).size()

	  //find corners for each image
	  var success=0;
	  for( i <- (0 until images.length)){
	    
	    println( s"processing image $i of ${images.length}")

	    val found = Calib3d.findChessboardCorners(images(i), boardSize, corners(success), Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_NORMALIZE_IMAGE); // Calib3d.CALIB_CB_FILTER_QUADS ); //CALIB_CB_NORMALIZE_IMAGE + CALIB_CB_FAST_CHECK);
	    if( !found ){
	      println("Couldn't find full board.. removing image " + i + " from calibration..")
	      if( i == images.length-1) corners.remove(corners.length-1)
	    } else {

	    	val gray = new Mat()
	      Imgproc.cvtColor(images(i), gray, Imgproc.COLOR_BGR2GRAY);
	      Imgproc.cornerSubPix( gray, corners(success), new Size(11,11), new Size(-1,-1), new TermCriteria( TermCriteria.EPS+TermCriteria.MAX_ITER, 30, 0.1 ));

		    //generate world coordinates
		    val p = new java.util.ArrayList[Point3]()
		    // println( s" !!!!!!!!  check this rows: ${corners(success).rows()} cols: ${corners(success).cols()}")
		    for( i<-(0 until boardSize.height.toInt); j <- ( 0 until boardSize.width.toInt ))
		      p.add( new Point3( j*squareSize, i*squareSize, 0.0 ));

		    val mop3f = new MatOfPoint3f()
		    mop3f.fromList(p)
		    points.add( mop3f )

		    if( i < images.length-1) corners.add(new MatOfPoint2f())
		    success += 1
	  	}
	  }
	  println(s"corners: ${corners.size()} points: ${points.size()}")

	  if( success == 0){
	  	println("no valid calibration images, calibration failed!")
	  	return
	  }

	  //intrinsic camera matrix
	  val intrInit = Array(1.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,1.0)
	  intrinsic.put(0,0, intrInit: _*)
	  
	  val rvecs = new java.util.ArrayList[Mat]()
	  val tvecs = new java.util.ArrayList[Mat]()
	 
	  val flags = Calib3d.CALIB_FIX_PRINCIPAL_POINT | Calib3d.CALIB_FIX_ASPECT_RATIO | Calib3d.CALIB_ZERO_TANGENT_DIST | Calib3d.CALIB_FIX_K4 | Calib3d.CALIB_FIX_K5
	
	  error = Calib3d.calibrateCamera( points, corners.asInstanceOf[java.util.List[Mat]], imageSize, intrinsic, distortion, rvecs, tvecs, flags )
	  
	  val dat = Array[Array[Double]](Array(0.0),Array(0.0),Array(0.0),Array(0.0))
	  Calib3d.calibrationMatrixValues( intrinsic, imageSize, 1.0, 1.0, dat(0), dat(1), dat(2), pp, dat(3) );

	  fovx = dat(0)(0)
	  fovy = dat(1)(0)
	  focalLength = dat(2)(0)
	  aspect = dat(3)(0)

	  init = true
  }

  // def calculateExtrinsicsFromBoard( Mat& image, Size board ) = {
		// vector<Point3f> points;
	 //  vector<Point2f> corners;

	 //  bool found = findChessboardCorners( image, board, corners, CALIB_CB_ADAPTIVE_THRESH | CALIB_CB_NORMALIZE_IMAGE | CALIB_CB_FAST_CHECK);
	 //  if( !found ){
	 //    cout << "!";
	 //    return false;
	 //  }

	 //  points.resize( corners.size() );
	 //  for( int i=0; i < corners.size(); i++)
	 //    points[i] = Point3f( i / board.width, i % board.width, 0.0f );
	   
	 //  Mat gray;
	 //  cvtColor(image, gray, CV_BGR2GRAY);
	 //  cornerSubPix(gray, corners, Size(11, 11), Size(-1, -1), TermCriteria(CV_TERMCRIT_EPS + CV_TERMCRIT_ITER, 30, 0.1));
	  
	 //  cv::solvePnP( points, corners, intrinsic, distortion, rvec, tvec); //, true);
	 //  return true;
  // }
  
  def undistortImage( image:Mat, result:Mat ){
	  if( !init ) return
  	Imgproc.undistort( image, result, intrinsic, distortion )
  }
  
  def recalculateFOV(){
  	val dat = Array[Array[Double]](Array(0.0),Array(0.0),Array(0.0),Array(0.0))
	  Calib3d.calibrationMatrixValues( intrinsic, imageSize, 1.0, 1.0, dat(0), dat(1), dat(2), pp, dat(3) )

	  fovx = dat(0)(0)
	  fovy = dat(1)(0)
	  focalLength = dat(2)(0)
	  aspect = dat(3)(0)
  }

  // def getGLViewport( int v[4] ){
  // 	if(!init) return;
	 //  v[0] = (int)(intrinsic.at<double>(0,2) - image_size.width/2.0);
	 //  v[1] = (int)(image_size.height/2.0 - intrinsic.at<double>(1,2));
	 //  v[2] = image_size.width;
	 //  v[3] = image_size.height;
  // }

  // def getGluPerspective( double v[4] ){
	 //  if(!init) return;  
	 //  v[0] = fovy;
	 //  v[1] = (double)image_size.width/image_size.height;
	 //  v[2] = near;
	 //  v[3] = far;
  // }

  // def getGLModelView( double v[16] ){
  // 	if(!init) return;

	 //  Mat rot;
	 //  Rodrigues(rvec, rot);

	 //  v[0] = rot.at<double>(0,0);
	 //  v[1] = rot.at<double>(1,0);
	 //  v[2] = rot.at<double>(2,0);
	 //  v[3] = 0.0;
	 //  v[4] = rot.at<double>(0,1);
	 //  v[5] = rot.at<double>(1,1);
	 //  v[6] = rot.at<double>(2,1);
	 //  v[7] = 0.0;
	 //  v[8] = rot.at<double>(0,2);
	 //  v[9] = rot.at<double>(1,2);
	 //  v[10] = rot.at<double>(2,2);
	 //  v[11] = 0.0;
	 //  v[12] = tvec.at<double>(0);
	 //  v[13] = tvec.at<double>(1);
	 //  v[14] = tvec.at<double>(2);
	 //  v[15] = 1.0;
  // }

  def unprojectRect( x:Int, y:Int, height:Int, worldHeight:Float ) = {

		  val intr = new Array[Double](9)
		  intrinsic.get(0,0,intr)

		  val fy = intr(4) //(1,1);
		  val fx = intr(0) //(0,0);
		  val pp = new Point( intr(6) /*at(0,2)*/, intr(7) /*at(1,2)*/ )

		  val z = fy * worldHeight / (1.0 * height);
		  val yy = z * (pp.y - y ) / fy;
		  val xx = z * (x - pp.x) / fx;
		  Vec3(xx,yy,z)
  }



  //util functions
  def loadImageDirectory( path:String ) = {
  
  	val images = new ListBuffer[Mat]()
  	val files = (new java.io.File(path)).listFiles.foreach( (f) => {
  		val img = Highgui.imread(f.getAbsolutePath(), 1)
			if( !img.empty() ) images += img
  	})

  	images.toArray
  }


  def loadParams( path:String ):Boolean = {

  	val file = scala.io.Source.fromFile(path)
  	val json_string = file.getLines.mkString
  	file.close

  	val parsed = scala.util.parsing.json.JSON.parseFull(json_string)
  	if( parsed.isEmpty ){
  		println(s"failed to parse: $path")
  		return false
  	}

  	val map = parsed.get.asInstanceOf[Map[String,Any]]

	  val intr = map("CameraMatrix").asInstanceOf[List[Double]]
	  val dist = map("DistortionCoefficients").asInstanceOf[List[Double]]
	  fovx = map("hFOV").asInstanceOf[Double]
	  fovy = map("vFOV").asInstanceOf[Double]
	  focalLength = map("FocalLength").asInstanceOf[Double]
	  aspect = map("AspectRatio").asInstanceOf[Double]
	  error = map("ReprojectionError").asInstanceOf[Double]

	  intrinsic.put(0,0,intr.toArray: _*)
	  distortion.put(0,0,dist.toArray: _*)

	  true
  }

  def writeParams( path:String ){

  	val intr = new Array[Double](9)
  	val dist = new Array[Double](5)
  	intrinsic.get(0,0,intr)
  	distortion.get(0,0,dist)

  	val map = Map(
	  	"CameraMatrix" -> intr.toList,
	  	"DistortionCoefficients" -> dist.toList,
	  	"hFOV" -> fovx,
	  	"vFOV" -> fovy,
	  	"FocalLength" -> focalLength,
	  	"AspectRatio" -> aspect,
	  	"ReprojectionError" -> error
	  )
  	val p = new java.io.PrintWriter(path)
	  p.write( scala.util.parsing.json.JSONObject(map).toString( (o) =>{
	  	o match {
	  		case o:List[Any] => s"""[${o.mkString(",")}]"""
	  		case s:String => s"""${'"'}${s}${'"'}"""
	  		case a:Any => a.toString()  
	  	}
	  }))
	  p.close

  }

  def printParams(){
	  println( s"CameraMatrix:  ${intrinsic.dump} ")
	  println( s"DistortionCoefficients:  ${distortion.dump} ")
	  println( s"hFOV:  $fovx ")
	  println( s"vFOV:  $fovy ")
	  println( s"FocalLength:  $focalLength ")
	  println( s"AspectRatio:  $aspect ")
	  println( s"ReprojectionError:  $error ")
  }

}



