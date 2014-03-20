
package com.fishuyo.seer
package graphics

import maths._

import collection.mutable.ArrayBuffer

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.{Mesh => GdxMesh}

import com.badlogic.gdx.graphics.g3d.loader._


object Mesh {
	def apply() = new Mesh()
	def apply(m:GdxMesh) = new Mesh(){ gdxMesh = Some(m); getVerticesFromGdxMesh() }
}


/** Mesh class wraps Gdx mesh class for some additional flexibility */
class Mesh extends Drawable {

	val vertices = new ArrayBuffer[Vec3]
	val normals = new ArrayBuffer[Vec3]
	val texCoords = new ArrayBuffer[Vec2]
	val colors = new ArrayBuffer[RGBA]
	val indices = new ArrayBuffer[Short]
	val wireIndices = new ArrayBuffer[Short]

	var primitive = Triangles

	var gdxMesh:Option[GdxMesh] = None 
	private var isStatic = false
	private var maxVertices = 0
	private var maxIndices = 0

	private var hasNormals = false
	private var hasTexCoords = false
	private var hasColors = false

	/** Initialize gdx mesh component */
	override def init(){
		if( gdxMesh.isDefined ) return
		
		var attrs = Vector(VertexAttribute.Position)
		if(normals.length > 0){
			hasNormals = true
			attrs = attrs :+ VertexAttribute.Normal
		}
		if(texCoords.length > 0){
			hasTexCoords = true
			attrs = attrs :+ VertexAttribute.TexCoords(0)
		}
		if(colors.length > 0){
			hasColors = true
			attrs = attrs :+ VertexAttribute.ColorUnpacked
		}

		maxVertices = vertices.length * 2
		maxIndices = indices.length * 2
	  if( gdxMesh.isDefined ) gdxMesh.get.dispose
	  gdxMesh = Some(new GdxMesh(isStatic, maxVertices, maxIndices, attrs:_*))
	  update()
	}

	/** Update vertices from buffers */
	def update(){
		if( gdxMesh.isEmpty ) init()

		var buffer = Vector(vertices.map(v => Seq(v.x,v.y,v.z)))
		if(hasNormals) buffer = buffer :+ normals.map(n => Seq(n.x,n.y,n.z))
		if(hasTexCoords) buffer = buffer :+ texCoords.map(t => Seq(t.x,t.y))
		if(hasColors) buffer = buffer :+ colors.map(c => Seq(c.r,c.g,c.b,c.a))

		gdxMesh.get.setVertices(buffer.transpose.flatten.flatten.toArray)
		if( primitive == Lines && wireIndices.length > 0) gdxMesh.get.setIndices(wireIndices.toArray)
		else if(indices.length > 0) gdxMesh.get.setIndices(indices.toArray)
	}

	/** draw the mesh */
	override def draw(){
		if( gdxMesh.isEmpty ) init()

		var count = vertices.length
		if( primitive == Lines && wireIndices.length > 0) count = wireIndices.length
		else if(indices.length > 0) count = indices.length

		if( hasColors ) Shader.shader.get.uniforms("u_hasColor") = 1
    gdxMesh.get.render(Shader(), primitive, 0, count )
	}


	def clear(){
		vertices.clear
		normals.clear
		texCoords.clear
		colors.clear
		indices.clear
	}

	def dispose(){gdxMesh.foreach(_.dispose); gdxMesh = None}

	def getVerticesFromGdxMesh(){
		if( gdxMesh.isEmpty ) return
		clear()

		val numI = gdxMesh.get.getNumIndices
		val numV = gdxMesh.get.getNumVertices
		val sizeV = gdxMesh.get.getVertexSize / 4 // number of floats per vertex

		val verts = new Array[Float](numV*sizeV)
		val indxs = new Array[Short](numI)

		if(numI > 0){
			gdxMesh.get.getIndices(indxs)
			indices ++= indxs
		}
		// TODO parse out attributes
		gdxMesh.get.getVertices(verts)
		vertices ++= new Array[Vec3](numV)
	}

	def recalculateNormals(){
		// make sure normals same size as vertices
		if( normals.length < vertices.length){
			normals.clear()
			vertices.foreach( (v) => normals += Vec3())
		}

		// if indices present
		if( indices.length > 0 && primitive == Triangles ){

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

		  normals.zip(count).foreach{ case (n,c) => n /= c }

	  } else {
	  	println("calc normals not implemented")

	  	// val l = mesh.vertices.grouped(3)
		  
		  // l.foreach( (xs) => {
		  // })
	  }
	}

}