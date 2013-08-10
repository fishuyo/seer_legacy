
/* 
 * Tim Wood
 *
 */

#ifndef TREE_H
#define TREE_H

#include "alloutil/al_App.hpp"

#include <iostream>
#include <fstream>
#include <string>
#include <vector>

using namespace al;
using namespace std;

extern float gravity;

struct TreeNode {
  
  Vec3f pos;
  Vec3f lpos;

  Vec3f accel;
  float mass;
  float damping;
  bool pinned;
  Vec3f pinPos;
  
  float distance;
  float stiffness;
  float w;
  float tearThreshold;
  float ax, ay, az;
  float thick;

  TreeNode *parent;
  vector< TreeNode *> children;
  
  TreeNode( Vec3f p, float d);
  TreeNode( TreeNode *parent, float angle, float d, float s);

  void onAnimate( double dt );
  void onDraw(Graphics &g);
  
  Vec3f& applyForce( Vec3f f );
  void solveConstraints();
  void pinTo( Vec3f p );

  void branch(int depth, float angle, float ratio, int type);
  
  void print();
  void outputPointCloud( ofstream& out );

};



#endif
