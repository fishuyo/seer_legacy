
package com.fishuyo.seer
package graphics

import spatial._

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
class Mesh extends MeshLike {

	var gdxMesh:Option[GdxMesh] = None 

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

		if(maxVertices == 0) maxVertices = math.max(vertices.length * 2, 100)
		if(maxIndices == 0) maxIndices = math.max(indices.length * 2, 100)
	  if( gdxMesh.isDefined ) gdxMesh.get.dispose
	  gdxMesh = Some(new GdxMesh(isStatic, maxVertices, maxIndices, attrs:_*))
	  update()
	}

	/** Update vertices from buffers */
	override def update(){
		if( gdxMesh.isEmpty ) init()

		var vert:Array[Float] = null
		if( hasNormals || hasTexCoords || hasColors){
			var buffer = Vector(vertices.map(v => Seq(v.x,v.y,v.z)))
			if(hasNormals) buffer = buffer :+ normals.map(n => Seq(n.x,n.y,n.z))
			if(hasTexCoords) buffer = buffer :+ texCoords.map(t => Seq(t.x,t.y))
			if(hasColors) buffer = buffer :+ colors.map(c => Seq(c.r,c.g,c.b,c.a))
			vert = buffer.transpose.flatten.flatten.toArray
		}else vert = vertices.map(v => List(v.x,v.y,v.z)).flatten.toArray

		gdxMesh.get.setVertices(vert)
		if( primitive == Lines && wireIndices.length > 0) gdxMesh.get.setIndices(wireIndices.map(_.toShort).toArray)
		else if(indices.length > 0) gdxMesh.get.setIndices(indices.map(_.toShort).toArray)
	}

	/** draw the mesh */
	override def draw(){
		if( gdxMesh.isEmpty ) init()

		var count = math.min(vertices.length, maxVertices)
		if( primitive == Lines && wireIndices.length > 0) count = math.min(wireIndices.length,maxIndices)
		else if(indices.length > 0) count = math.min(indices.length,maxIndices)

		if( hasColors ) Shader.shader.get.uniforms("u_hasColor") = 1
    gdxMesh.get.render(Shader(), primitive, 0, count )
	}

	def dispose(){gdxMesh.foreach(_.dispose); gdxMesh = None}

	def getVerticesFromGdxMesh(){
		if( gdxMesh.isEmpty ) return
		clear()

		val numI = gdxMesh.get.getNumIndices
		val numV = gdxMesh.get.getNumVertices
		val sizeV = gdxMesh.get.getVertexSize / 4 // number of floats per vertex

		println(s"extracting vertex data $numV ($sizeV), $numI")

		val verts = new Array[Float](numV*sizeV)
		val indxs = new Array[Short](numI)

		if(numI > 0){
			gdxMesh.get.getIndices(indxs)
			indices ++= indxs.map(_.toInt)
		}

		gdxMesh.get.getVertices(verts)

		val attrs = gdxMesh.get.getVertexAttributes
		for( i<-(0 until numV)){
			for( a<-(0 until attrs.size)){
				import VertexAttributes._
				val attr = attrs.get(a)
				val off = attr.offset / 4
				attr.usage match {
					case Usage.Position => vertices += Vec3(verts(sizeV*i+off),verts(sizeV*i+off+1),verts(sizeV*i+off+2))
					case Usage.Normal => normals += Vec3(verts(sizeV*i+off),verts(sizeV*i+off+1),verts(sizeV*i+off+2))
					case Usage.TextureCoordinates => texCoords += Vec2(verts(sizeV*i+off),verts(sizeV*i+off+1))
					case Usage.Color => colors += RGBA(verts(sizeV*i+off),verts(sizeV*i+off+1),verts(sizeV*i+off+2),verts(sizeV*i+off+3))
				}
			} 
		}
	}

}