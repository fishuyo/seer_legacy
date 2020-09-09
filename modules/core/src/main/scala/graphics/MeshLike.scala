
package seer
package graphics

import spatial._

import collection.mutable.ArrayBuffer


/** MeshLike trait represents 3d mesh data interface */
class MeshLike extends Drawable with Serializable {

	val vertices = new ArrayBuffer[Vec3]
	val normals = new ArrayBuffer[Vec3]
	val texCoords = new ArrayBuffer[Vec2]
	val colors = new ArrayBuffer[RGBA]
	val indices = new ArrayBuffer[Int]
	val wireIndices = new ArrayBuffer[Int]

	var primitive = Triangles

	var isStatic = false
	var maxVertices = 0
	var maxIndices = 0

	var hasNormals = false
	var hasTexCoords = false
	var hasColors = false

	/** Update implementation vertices from buffers */
	def update(){}

	def clear(){
		vertices.clear
		normals.clear
		texCoords.clear
		colors.clear
		indices.clear
	}

	def normalize() = {
    var min = Vec3( java.lang.Double.MAX_VALUE )
    var max = Vec3( java.lang.Double.MIN_VALUE )
    vertices.foreach( {
      case Vec3( x,y,z ) => {
        min = Vec3( math.min( min.x, x ), math.min( min.y, y ), math.min( min.z, z ))
        max = Vec3( math.max( max.x, x ), math.max( max.y, y ), math.max( max.z, z ))
      }
    })

    val half = Vec3(.5)
    val diff = max - min
    val maxDiff = math.max( math.max( diff.x, diff.y ), diff.z)
    for (i <- 0 until vertices.length ) {
      vertices(i) -= min
      vertices(i) *= 1.0f / maxDiff
      vertices(i) -= half
    }
    // println( min + " " + max )
    this
  }

	def recalculateNormals(){
		// make sure normals same size as vertices
		if( normals.length < vertices.length){
			normals.clear()
			vertices.foreach( (v) => normals += Vec3())
		}

		// if indices present
		if( indices.length > 0 && indices.length % 3 == 0 ){ //primitive == Triangles ){

			val count = new Array[Float](vertices.length)
			// for each face (3 indices)
	  	val l = indices.grouped(3)
		  l.foreach( (xs) => {
		  	val vs = xs.map(vertices(_))
		  	val n = (vs(1)-vs(0) cross vs(2)-vs(0)).normalize
		  	xs.foreach( (x) => { // sum normals for vertex
		  		normals(x) += n 
		  		count(x) += 1 
		  	})
		  })

		  normals.zip(count).foreach{ case (n,c) =>
        if(c > 0) n /= c
        if( n.x.isNaN){
          // println("NAAN!") // XXX FIX THIS
          n.set(Vec3(0,0,1))
        }
      }

	  } else if( vertices.length > 0 && vertices.length % 3 == 0 ){

			val count = new Array[Float](vertices.length)
			// for each face (3 indices)
			var indx = 0
	  	val l = vertices.grouped(3)
		  l.foreach( (vs) => {
		  	val n = (vs(1)-vs(0) cross vs(2)-vs(0)).normalize
		  	for( i <- 0 until 3){
		  		normals(indx) += n 
		  		count(indx) += 1 
		  		indx += 1
		  	}
		  })

		  normals.zip(count).foreach{ case (n,c) => n /= c }

	  } else {
	  	println("calc normals not implemented")

	  	// val l = mesh.vertices.grouped(3)
		  
		  // l.foreach( (xs) => {
		  // })
	  }
	}

}