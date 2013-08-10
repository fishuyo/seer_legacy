
/* 
 * Tim Wood
 * Based on Curtain processing sketch by BlueThen
 *  www.bluethen.com
 *
 */

#include "alloutil/al_App.hpp"

#include <iostream>
#include <fstream>
#include <vector>

#include "fabric.h"

float gravity = -5.0f; 

using namespace al;
using namespace std;


  Link::Link( Particle *u, Particle *v, float dist, float stiff=1.f, float tear=.5f) : p1(u), p2(v), distance(dist), stiffness(stiff), tearThreshold(tear), draw(true) {
    float im1 = 1.f/p1->mass;
    float im2 = 1.f/p2->mass;
    wp1 = ( im1 / (im1+im2) ) * stiffness;
    wp2 = ( im2 / (im1+im2) ) * stiffness;
  }


  void Link::solve(){
    //cout << "solve " << p2 << endl;
    if( p2 == NULL ) return;
    //cout << p1->pos.x << " " << p1->pos.y << " " << p1->pos.z << endl;
    //cout << p2->pos.x << " " << p2->pos.y << " " << p2->pos.z << endl;
    
    Vec3f dist = p1->pos - p2->pos;
    float d = dist.mag();
    if( d == 0.f ) return;
    float diff = (distance - d) / d; 

    
    //if( d > tearThreshold )
    //  p2 = NULL; //remove link

    p1->pos += dist * wp1 * diff;
    p2->pos -= dist * wp2 * diff;

    //float theta = acos( dist.dot( Vec3f( 0.f, 1.f, 0.f) ) / d );
    //cout << theta << endl;

  }

  void Link::onDraw( Graphics &g ){
    if( !draw || p2 == NULL ) return;
    //g.begin( Graphics::LINES );
    g.vertex( p1->pos.x, p1->pos.y, p1->pos.z );
    g.vertex( p2->pos.x, p2->pos.y, p2->pos.z );
    //g.end();
  }


///////////////////////////////////////////

Particle::Particle(Vec3f p=Vec3f(0.f,0.f,0.f) ) : pinned(false), mass(1.f), damping(10.f), accel( Vec3f( 0.f,0.f,0.f)) {
  pos = p;
  lpos = pos;
  pinPos = pos;
}

void Particle::onAnimate( double dt ){
  accel += Vec3f( 0.f, gravity, 0.f );

  //Verlet Integration
  Vec3f v = pos - lpos;
  accel -= v * (damping / mass);

  lpos = pos;
  pos = (pos + v) + ( accel * (.5f * dt * dt));

  accel.zero();
}
  
void Particle::applyForce( Vec3f f ){
  accel += f / mass;
}

void Particle::onDraw(Graphics &g){
 
  g.begin( Graphics::LINES );
  for( int i=0; i < links.size(); i++)
    links[i]->onDraw(g);
  g.end();

  g.begin( Graphics::POINTS );
  g.vertex( pos.x, pos.y, pos.z );
  g.end();
  
  
}

void Particle::solveConstraints(){

  //cout << "solveContraints links: " << links.size() << endl;
  
  for( int i=0; i < links.size(); i++)
    links[i]->solve();

  /*if( pos.y < -3.f ) pos.y = 2 * (-3.f) - pos.y;
  if( pos.y > 3.f ) pos.y = 2 * (3.f) - pos.y;
  if( pos.x < -3.f ) pos.x = 2 * (-3.f) - pos.x;
  if( pos.x > 3.f ) pos.x = 2 * (3.f) - pos.x;
  */

  if( pinned ) pos = pinPos;
}

void Particle::addLink( Particle *p, float d, float stiff){
  links.push_back( new Link( this, p, d, stiff) );
}

void Particle::pinTo( Vec3f p ){
  pinned = true;
  pinPos = p;
}

//////////////////////////////////////////////////////////////////

  Fabric::Fabric( Vec3f p, float width=1.f, float height=1.f, float dist=.03f, float stiff=1.f, int mode=0 ): w(width), h(height), d(dist), s(stiff), pos(p) {
   
    //mode 0: construct curtain in xy plane
    //mode 1: construct in xz plane
    //mode 2: consturct in yz plane

    int nx = (int) (width / dist);
    int ny = (int) (height / dist);
    //float left;// = pos.x - width / 2;
    //float top;// = pos.y + height / 2;
    float xx, yy;

    //cout << "creating fabric " << nx << " by " << ny << endl;
    //cout << left << " " << top << endl;

    for( int y=0; y < ny; y++ ){
      for( int x=0; x < nx; x++ ){
     
        
        Particle *p;

        switch ( mode ){
          case 0:
          xx = (pos.x - width / 2) + x * d;
          yy = (pos.y + height/ 2) - y * d;
          p =  new Particle( Vec3f( xx, yy * .9f, pos.z ));
          break;
          case 1:
          xx = (pos.x - width / 2) + x * d;
          yy = (pos.z + height/ 2) - y * d;
          p =  new Particle( Vec3f( xx, pos.y , yy ));
          break;
          case 2:
          //not impl
          xx = (pos.x - width / 2) + x * d;
          yy = (pos.y + height/ 2) - y * d;
          p =  new Particle( Vec3f( pos.x, yy * .9f, xx ));
          break;
          default:
          break;
        }

        particles.push_back( p );

       

        if( x != 0 ){
          //cout << "addleft: " << x << " " << y << endl;
          p->addLink( particles[particles.size()-2], d, s);  
        }
        if( y != 0 ){
          //cout << "addup: " << x << " " << y << endl;
          p->addLink( particles[((y-1) * nx + x)], d, s);  
          if( mode != 1 ) p->pos.z += rnd::uniform(-1.f,1.f);
        }

        if( mode == 1 && y > ny/2 - 5 && y < ny/2 + 5 && x > nx/2 - 5 && x < nx/2 + 5 ) p->pinTo( p->pos );
        if( mode != 1 && y == 0 ) p->pinTo( p->pos );
  
        //cout << p->pos.x << " " << p->pos.y << " " << p->pos.z << endl;
        //cout << "links " << p->links.size() << endl;
        
      }
    }
    //cout << "num particles: " << particles.size() << endl;

    /*Particle *p = new Particle( Vec3f( 0.f, 0.f, -3.f) );
    Particle *q = new Particle( Vec3f( -.1f, 0.f, -3.f) ); 
    particles.push_back(p);
    particles.push_back(q);
    p->pinTo( p->pos );
    q->addLink( p, .1f, 1.f );
    */
  }

  void Fabric::onAnimate( double dt ){

    double ts = .015;

    static int l=0;
    static double xt=0;
    int timeSteps = (int)( (dt + xt)/ ts );
    xt += dt - timeSteps * ts; 

    //if( l++ % 60 == 0 ) cout << "num particles: " << particles.size() << endl;

    for( int t=0; t < timeSteps; t++) {
      for( int solve=0; solve < 3; solve++){
        for( int i=0; i < particles.size(); i++){
          //if( l % 60 == 0 ) cout << "particle " << i << " links: " << particles[i].links.size() << endl;  
          particles[i]->solveConstraints();
        }
      }
      for( int i=0; i < particles.size(); i++){
        //if( l % 60 == 0 ) cout << "particle " << i << " links: " << particles[i].links.size() << endl;  
        particles[i]->onAnimate(.015);
      }
    }

  }

  void Fabric::onDraw( Graphics &g ){
    for( int i=0; i < particles.size(); i++){
      particles[i]->onDraw(g);
    }
  }

  void Fabric::applyForce( Vec3f f ){
    for( int i=0; i < particles.size(); i++){
      particles[i]->applyForce(f);
    }
  }


void Fabric::outputPointCloud( const char* file ){

  ofstream out( file );
  if( !out.is_open() ) return;

  for( int i=0; i < particles.size(); i++ ){
    
    Particle *x,*y;
    if( particles[i]->links.size() < 2 ) continue;
    x = particles[i]->links[0]->p2;
    y = particles[i]->links[1]->p2;
    Vec3f l = particles[i]->pos;
    Vec3f r = x->pos; 
    
    for( int j=0; j < 20; j++){

      Vec3f tmp = l; 
      for( int d=0; d < 20; d++){ //(int)1000*distance; j++ ){

        float s = .0015f;
        float s2 = s * sqrt(2.0);

        out << tmp.x << " " << tmp.y << " " << tmp.z+s << " 0 0 1" << endl;
        out << tmp.x << " " << tmp.y << " " << tmp.z-s << " 0 0 -1" << endl;
        out << tmp.x << " " << tmp.y+s << " " << tmp.z << " 0 1 0" << endl;
        out << tmp.x << " " << tmp.y-s << " " << tmp.z << " 0 -1 0" << endl;
        out << tmp.x+s << " " << tmp.y << " " << tmp.z << " 1 0 0" << endl;
        out << tmp.x-s << " " << tmp.y << " " << tmp.z << " -1 0 0" << endl;
        tmp.lerp( r , .05f); //distance / 1000.0f );

      }
      l.lerp( y->pos, .05f );
      r.lerp( x->links.back()->p2->pos, .05f );
    }
  }

}
