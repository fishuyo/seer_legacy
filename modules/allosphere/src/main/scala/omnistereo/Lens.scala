
package com.fishuyo.seer 
package allosphere


/// Stores optics settings important for rendering
class Lens(
		fovy0:Double=30.0,
		var near:Double=0.1,
		var far:Double=100.0,
		var focalLength:Double=6.0,
		var eyeSep:Double=0.02
	){

	var mTanFOV = 0.0				// Cached factor for computing frustum dimensions
	var fovy = fovy0

	setFovy(fovy0)

	// setters
	def setFovy(v:Double) = {
		val cDeg2Rad = math.Pi / 180.0;
		fovy = v;
		mTanFOV = math.tan(fovy * cDeg2Rad*0.5);
		this
	}							///< Set vertical field of view, in degrees
	def setFovx(v:Double, aspect:Double) = {
		setFovy(getFovyForFovX(v, aspect));
		this
	}				///< Set horizontal field of view, in degrees

	def eyeSepAuto() = { focalLength/30.0; } ///< Get automatic inter-ocular distance

	// void frustum(Frustumd& f, const Pose& p, double aspect) const;
	
	/// Returns half the height of the frustum at a given depth
	/// To get the half-width multiply the half-height by the viewport aspect
	/// ratio.
	// double heightAtDepth(double depth) const { return depth*mTanFOV; }
	
	/// Returns half the height of the frustum at the near plane
	// double heightAtNear() const { return heightAtDepth(near()); }
	
	// calculate desired fovy, given the Y height of the border at a specified Z depth:
	// static double getFovyForHeight(double height, double depth) {
		// return 2.0*M_RAD2DEG*atan(height/depth);
	// }

	/// Calculate required fovy to produce a specific fovx
	/// @param[fovx] field-of-view in X axis to recreate
	/// @param[aspect] aspect ratio of viewport
	/// @return field-of-view in Y axis, usable by Lens.fovy() 
	def getFovyForFovX(fovx:Double, aspect:Double) = {
		val farW = math.tan(0.5*fovx.toRadians);
		2.0*math.atan(farW/aspect).toDegrees
	}


	// // @param[in] isStereo		Whether scene is in stereo (widens near/far planes to fit both eyes)
	// void Lens::frustum(Frustumd& f, const Pose& p, double aspect) const {//, bool isStereo) const {

	// 	Vec3d ur, uu, uf;
	// 	p.directionVectors(ur, uu, uf);
	// 	const Vec3d& pos = p.pos();

	// 	double nh = heightAtDepth(near());
	// 	double fh = heightAtDepth(far());

	// 	double nw = nh * aspect;
	// 	double fw = fh * aspect;
		
	// //	// This effectively creates a union between the near/far planes of the 
	// //	// left and right eyes. The offsets are computed by using the law
	// //	// of similar triangles.
	// //	if(isStereo){
	// //		nw += fabs(0.5*eyeSep()*(focalLength()-near())/focalLength());
	// //		fw += fabs(0.5*eyeSep()*(focalLength()- far())/focalLength());
	// //	}

	// 	Vec3d nc = pos + uf * near();	// center point of near plane
	// 	Vec3d fc = pos + uf * far();	// center point of far plane

	// 	f.ntl = nc + uu * nh - ur * nw;
	// 	f.ntr = nc + uu * nh + ur * nw;
	// 	f.nbl = nc - uu * nh - ur * nw;
	// 	f.nbr = nc - uu * nh + ur * nw;

	// 	f.ftl = fc + uu * fh - ur * fw;
	// 	f.ftr = fc + uu * fh + ur * fw;
	// 	f.fbl = fc - uu * fh - ur * fw;
	// 	f.fbr = fc - uu * fh + ur * fw;

	// 	f.computePlanes();
	// }

}
