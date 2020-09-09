
package seer
package openni

import spatial.Vec3

import scodec._
import bits.{ BitVector, ByteVector }
import codecs._

// maybe use brekel --> https://github.com/unitycoder/UnityPointCloudViewer/wiki/Binary-File-Format-Structure

//UserLoop
// header ->
// data -> vector[points(n) :: skel(15)]

//User
// points -> vector[vec3/Float](n)
// skel -> vector[vec3](15 + 15)

case class UserLoopHeader(
  version:Int = 0,
  rate:Int = 0,         
  rgb:Boolean = false,
  skeleton:Boolean = true,
  frameCount:Int = 0,         
  // usersPerFrame:Vector[Int] = Vector(), //assume 1
  pointsPerFrame:Vector[Int] = Vector(),
  indexOfFrame:Vector[Int] = Vector()
)


case class UserLoopFile(
  header:UserLoopHeader,
  data:Vector[Float]
){
  def readFrame(index:Int):User = {
    val user = new User(0)
    val n = header.pointsPerFrame(index)
    val offset = header.indexOfFrame(index) + 1 // +1 codec reads extra int for vector length..
    user.points ++= data.drop(offset).take(n*3).grouped(3).map{ case Vector(x,y,z) => Vec3(x,y,z)}
    if(header.skeleton){
      val ps = data.drop(offset+n*3).take(15*3).grouped(3).map{ case Vector(x,y,z) => Vec3(x,y,z)}
      ps.toList.zip(Joint.strings).foreach { case (v,j) => user.skeleton.updateJoint(j,v) }
    }
    user
  }
}

object Codecs {
  implicit val pointCloudHeader: Codec[UserLoopHeader] = {
    uint8L :: uint8L :: bool(8) :: bool(8) ::
    (int32L >>:~ { case i:Int => 
      vectorOfN(provide(i), int32L) ::
      vectorOfN(provide(i), int32L)
    })
  }.as[UserLoopHeader]

  implicit val pointCloudFile: Codec[UserLoopFile] = {
    pointCloudHeader :: vector(floatL)
  }.as[UserLoopFile]

  
  def parseFile[T](path:String)(implicit codec:Codec[T]): T = {
    import java.nio.file.{Files, Paths}
    val byteArray = Files.readAllBytes(Paths.get(path))
    Codec.decode[T](ByteVector(byteArray).bits).require.value
  }

  def writeFile[T](path:String, data:T)(implicit codec:Codec[T]){
    import java.io._
    val byteArray: Array[Byte] = Codec.encode(data).require.toByteArray
    val bos = new BufferedOutputStream(new FileOutputStream(path, false))
    bos.write(byteArray)
    bos.close()
  }
}

