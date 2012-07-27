
/* 
 * Tim Wood
 *
 */

#include "alloutil/al_App.hpp"

#include <fstream>
#include <iostream>
#include <vector>

#include "tree.h"

float d2r= 0.0174532925f;

using namespace al;
using namespace std;

TreeNode::TreeNode( Vec3f p, float d) : pinned(false), mass(1.f), stiffness(1.f), damping(10.f), accel( Vec3f( 0.f,0.f,0.f)) {
  parent = NULL;
  pos = p;
  lpos = pos;
  ax = ay = 0.f;
  az = 90.f;

  distance = d;

  pinTo(p);
}

TreeNode::TreeNode( TreeNode *p, float angle, float d, float s=.5f) : pinned(false), mass(1.f), damping(10.f), accel( Vec3f( 0.f,0.f,0.f)) {
  distance = d;
  az = angle;
  stiffness = s;

  parent = p;
  float x = cos(angle * d2r);
  float y = sin(angle * d2r);

  pos = d * Vec3f( x, y, rnd::uniform(-.1f,.1f) ) + parent->pos;
  //cout << pos.x << " " << pos.y << " " << pos.z << endl;
  lpos = pos;
  
  w = 1.f;
  //float im1 = 1.f/pmass;
  //float im2 = 1.f/p2->mass;
  //wp1 = ( im1 / (im1+im2) ) * stiffness;
  //wp2 = ( im2 / (im1+im2) ) * stiffness;
  
}

void TreeNode::onAnimate( double dt ){
  accel += Vec3f( 0.f, gravity, 0.f );

  //Verlet Integration
  Vec3f v = pos - lpos;
  accel -= v * (damping / mass);

  lpos = pos;
  pos = (pos + v) + ( accel * (.5f * dt * dt));

  accel.zero();
}
  
Vec3f& TreeNode::applyForce( Vec3f f ){
  Vec3f r(0.f,0.f,0.f);
  r += f;
  accel += r / mass;
  
  for( int i=0; i < children.size(); i++){
    children[i]->applyForce( 2.f*f );
    //r += children[i]->distance * children[i]->applyForce( f );
  }

  return r;
}

void TreeNode::onDraw(Graphics &g){

  g.begin( Graphics::LINES );
  for( int i=0; i < children.size(); i++){
    TreeNode *n = children[i];
    glLineWidth( thick );
    g.vertex( pos.x, pos.y, pos.z );
    g.vertex( n->pos.x, n->pos.y, n->pos.z );
  }
  g.end();

  g.begin( Graphics::POINTS );
  g.vertex( pos.x, pos.y, pos.z );
  g.end();
  
  for( int j=0; j < children.size(); j++)
    children[j]->onDraw(g);
  
}

void TreeNode::solveConstraints(){

  //cout << "solveContraints links: " << links.size() << endl;
  
  for( int i=0; i < children.size(); i++){
    
    children[i]->solveConstraints();

    Vec3f dist = pos - children[i]->pos;
    float d = dist.mag();
    if( d == 0.f ) continue;
    float diff = (distance - d) / d; 
    
    //if( d > tearThreshold )
    //  p2 = NULL; //remove link

    pos += dist * w * diff;
    children[i]->pos -= dist * children[i]->w * diff;

    float theta = acos( dist.dot( Vec3f( 0.f, 1.f, 0.f) ) / d );
    //cout << theta << endl;

  }
/*if( pos.y < -3.f ) pos.y = 2 * (-3.f) - pos.y;
  if( pos.y > 3.f ) pos.y = 2 * (3.f) - pos.y;
  if( pos.x < -3.f ) pos.x = 2 * (-3.f) - pos.x;
  if( pos.x > 3.f ) pos.x = 2 * (3.f) - pos.x;
  */

  if( pinned ) pos = pinPos;
}

void TreeNode::pinTo( Vec3f p ){
  pinned = true;
  pinPos = p;
}


void TreeNode::branch(int depth, float angle=10.f, float ratio=.9f, int type=0){
 
  //if( rnd::prob(.3)) type = 1;
  thick = depth;
  if( depth == 0 ) return;

  switch( type ) {
    
    case 0: {
      //left
      TreeNode *n = new TreeNode(this, az - angle, distance * ratio );
      children.push_back( n );
      n->branch( depth - 1 );

      //right
      TreeNode *m = new TreeNode(this, az + angle, distance * ratio);
      children.push_back( m );
      m->branch( depth - 1 );
    }
    break;

    case 1: {
      //left
      TreeNode *n = new TreeNode(this, az - angle, .5f * distance * ratio );
      children.push_back( n );
      n->branch( depth - 1 );
      
      TreeNode *o = new TreeNode(this, az - 3*angle, .5f * distance * ratio );
      children.push_back( o );
      n->branch( depth - 1 );

      //right
      TreeNode *m = new TreeNode(this, az + angle, .5f * distance * ratio );
      children.push_back( m );
      m->branch( depth - 1 );
    }
    break;

    case 2: {
      //left
      TreeNode *n = new TreeNode(this, az - angle, .5f * distance * ratio );
      children.push_back( n );
      n->branch( depth - 1 );
      
      TreeNode *o = new TreeNode(this, az - 2*angle, distance * ratio );
      children.push_back( o );
      n->branch( depth - 1 );
      TreeNode *oo = new TreeNode(this, az + 2*angle, distance * ratio );
      children.push_back( o );
      n->branch( depth - 1 );

      //right
      TreeNode *m = new TreeNode(this, az + angle, .5f * distance * ratio );
      children.push_back( m );
      m->branch( depth - 1 );
    }
    break;

    default:
    break;
  }
  
}

void TreeNode::print(){
  
  for( int i=0; i < children.size(); i++){
    
    Vec3f tmp = pos;
    for( int j=0; j < 100; j++){ //(int)1000*distance; j++ ){

      float s = thick * .0015f;
      cout << tmp.x << " " << tmp.y << " " << tmp.z+s << " 0 0 1" << endl;
      cout << tmp.x << " " << tmp.y << " " << tmp.z-s << " 0 0 -1" << endl;
      cout << tmp.x << " " << tmp.y+s << " " << tmp.z << " 0 1 0" << endl;
      cout << tmp.x << " " << tmp.y-s << " " << tmp.z << " 0 -1 0" << endl;
      cout << tmp.x+s << " " << tmp.y << " " << tmp.z << " 1 0 0" << endl;
      cout << tmp.x-s << " " << tmp.y << " " << tmp.z << " -1 0 0" << endl;
      tmp.lerp( children[i]->pos + (children[i]->pos - pos)*.1 , .025f); //distance / 1000.0f );

    }
    children[i]->print();
  }

}

void TreeNode::outputPointCloud( ofstream& out ) {

  if( !out.is_open() ) return;
  
  for( int i=0; i < children.size(); i++){

    Vec3f tmp = pos;
    for( int j=0; j < 100; j++){ //(int)1000*distance; j++ ){

      float s = thick * .0015f;
      float s2 = s * sqrt(2.0);

      out << tmp.x << " " << tmp.y << " " << tmp.z+s << " 0 0 1" << endl;
      out << tmp.x << " " << tmp.y << " " << tmp.z-s << " 0 0 -1" << endl;
      out << tmp.x << " " << tmp.y+s << " " << tmp.z << " 0 1 0" << endl;
      out << tmp.x << " " << tmp.y-s << " " << tmp.z << " 0 -1 0" << endl;
      out << tmp.x+s << " " << tmp.y << " " << tmp.z << " 1 0 0" << endl;
      out << tmp.x-s << " " << tmp.y << " " << tmp.z << " -1 0 0" << endl;
      tmp.lerp( children[i]->pos + (children[i]->pos - pos)*.1 , .025f); //distance / 1000.0f );

    }
    children[i]->outputPointCloud( out );
  }

}
