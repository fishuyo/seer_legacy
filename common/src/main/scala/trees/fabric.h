

#ifndef FABRIC_H
#define FABRIC_H

#include "alloutil/al_App.hpp"

#include <iostream>
#include <vector>

using namespace al;
using namespace std;

extern float gravity;

struct Link;

struct Particle {
  
  Vec3f pos;
  Vec3f lpos;

  Vec3f accel;
  float mass;
  float damping;
  bool pinned;
  Vec3f pinPos;

  vector<Link*> links;
  
  Particle(Vec3f p);

  void onAnimate( double dt );
  void onDraw(Graphics &g);
  
  void applyForce( Vec3f f );
  void solveConstraints();
  void addLink( Particle *p, float d, float stiff);
  void pinTo( Vec3f p );

};


struct Link {

  float distance;
  float stiffness;
  float tearThreshold;
  float ax, ay, az;

  Particle *p1, *p2;
  float wp1, wp2; //tug weights

  bool draw;

  Link( Particle *u, Particle *v, float dist, float stiff, float tear);


  void solve();

  void onDraw( Graphics &g );

};


struct Fabric {
  
  vector<Particle*> particles;
  Vec3f pos;
  float w, h, d, s;

  Fabric( Vec3f p, float width, float height, float dist, float stiff, int mode );
  void onAnimate( double dt );
  void onDraw( Graphics &g );
  void applyForce( Vec3f f );

  void outputPointCloud( const char* file );
};

#endif
