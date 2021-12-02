package seer 

import javax.swing._
import org.bytedeco.javacv._
import org.bytedeco.opencv.global.opencv_core._
import org.bytedeco.opencv.global.opencv_imgcodecs._
import org.bytedeco.opencv.global.opencv_imgproc._
import org.bytedeco.opencv.opencv_core._

import org.bytedeco.openpose._
import org.bytedeco.openpose.global.openpose._

import graphics._ 

object OpenPoseTest extends SeerApp {

  // Configure OpenPose
  val opWrapper = new OpWrapper(ThreadManagerMode.Asynchronous);
  val structPose = new WrapperStructPose();
  structPose.modelFolder(new OpString("/Users/fishuyo/Desktop/models"));
  opWrapper.disableMultiThreading();
  opWrapper.configure(structPose);
  // if (doFace) {
  //     WrapperStructFace face = new WrapperStructFace();
  //     face.enable(true);
  //     opWrapper.configure(face);
  // }
  // if (doHands) {
  //     WrapperStructHand hand = new WrapperStructHand();
  //     hand.enable(true);
  //     opWrapper.configure(hand);
  // }

  // Start OpenPose
  opWrapper.start();
  val src:Mat = imread("/Users/fishuyo/_projects/2020_dissertation/media/03_terrarium/terrarium1.jpg");
  // cvtColor(src, src, COLOR_BGR2GRAY)
  val opIm:Matrix = OP_CV2OPCONSTMAT(src);
  var dat = new Datum();
  dat.cvInputData(opIm);
  val dats = new Datums();
  dats.put(dat);
  println("here0")

  opWrapper.emplaceAndPop(dats);
  println("here0.5")

  dat = dats.get(0);

  println("here1")

  val out:Mat = OP_OP2CVCONSTMAT(dat.cvOutputData());
  println("here2")

  // Read an image.
  // val src = imread("/Users/fishuyo/_projects/2020_dissertation/media/02_fragment/angle.png")
  // val src = imread("/Users/fishuyo/_projects/2020_dissertation/media/02_fragment/FullSizeRender_3 copy.jpg")
  // val src = imread("/Users/fishuyo/_projects/2020_dissertation/media/03_terrarium/terrarium1.jpg")
  println(s"${out.cols()} x ${out.rows()} . ${out.channels()}")





  val image = new Image(out.data().asBuffer, out.cols(), out.rows(), 3, 1)
  var tex:Texture = _

  val model = Plane()

  override def init() = {
    tex = Texture(image)
    tex.bgr()
    tex.update()
    model.scale(1,-image.h.toFloat/image.w,1)
    model.material.loadTexture(tex)
  }
  override def draw() = {
    model.draw
  }

}
