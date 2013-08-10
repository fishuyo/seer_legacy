
/* 
 * Tim Wood
 *
 */

#include "alloutil/al_App.hpp"
#include "Gamma/Player.h"

#include <iostream>
#include <vector>

#include "fabric.h"
#include "tree.h"


using namespace al;
using namespace std;

struct LsysApp : App, InputEventHandler {
  
  //LSys *lsys;
  //LTree *tree;

  bool wind;
  bool gusting;
  double t;
  float duration;
  float mag;

  vector<Fabric *> fabrics;

  vector<TreeNode *> trees;

  gam::Player<float, gam::ipl::Cubic, gam::tap::Wrap> player;
  //gam::Decay<1,1> decay;
  
  LsysApp() : player( "../../examples/201b/wood_tim/final-project/wind.wav") {

    gam::Sync::master().spu(audioIO().fps());
    
    Fabric *f;

    wind = false;
    gusting = false;
    t = duration = mag = 0;
  
    TreeNode *tree = new TreeNode( Vec3f( 3.f, 0.f, -4.f), .3f);
    tree->branch(5, 10.f, .9f, 0);
    trees.push_back( tree );
    
    f = new Fabric( Vec3f( 3.f,0.f, -4.f), 1.0f, 1.0f, .05f, 1.f, 1 );
    fabrics.push_back(f);

    tree = new TreeNode( Vec3f( 0.f, 0.f, -4.f), .1f);
    tree->branch(6, 45.f, .8f, 0);
    trees.push_back( tree );

    f = new Fabric( Vec3f( 0.f,0.f, -4.0f), 1.0f, 1.0f, .05f, 1.f, 1 );
    fabrics.push_back(f);

    tree = new TreeNode( Vec3f( 9.f, 0.f, -4.f), .1f);
    tree->branch(8, 20.f, .5f, 0);
    trees.push_back( tree );
    
    f = new Fabric( Vec3f( 9.f,0.f, -4.f), 1.0f, 1.0f, .05f, 1.f, 1 );
    fabrics.push_back(f);
    
    tree = new TreeNode( Vec3f( 6.f, 0.f, -4.f), .3f);
    tree->branch(10, 20.f, .5f, 0);
    trees.push_back( tree );

    f = new Fabric( Vec3f( 6.f,0.f, -4.f), 1.0f, 1.0f, .05f, 1.f, 1 );
    fabrics.push_back(f);


    Window *w = initWindow(Window::Dim(700,700));
    w->prepend( *this );
    
    initAudio(44100, 512, 2, 0);
    start();
  }

  virtual void onAnimate( double dt ) {
    
    t += dt;

    
    if( wind ){
      if( t > duration ){
        t = 0;
        if( rnd::prob(.8) ){
          gusting = true;
          mag = rnd::uniform( -1.f, -20.f);
          //cout << "Gust: " << mag << endl;
        }else gusting = false;
        duration = rnd::uniform( .1f, 1.f );
      }

      if(gusting){
        for( int i=0; i<fabrics.size(); i++) fabrics[i]->applyForce( Vec3f( mag, 0.f, 0.f ) );
      }
    }
    for( int i=0; i<fabrics.size(); i++) fabrics[i]->onAnimate(dt);

    double ts = .015;
    static int l=0;
    static double xt=0;
    int timeSteps = (int)( (dt + xt)/ ts );
    xt += dt - timeSteps * ts; 

    //if( l++ % 60 == 0 ) cout << "num particles: " << particles.size() << endl;

    for( int t=0; t < timeSteps; t++) {
      for( int solve=0; solve < 3; solve++){
        for( int i=0; i<trees.size(); i++) trees[i]->solveConstraints();
      }
      for( int i=0; i<trees.size(); i++){
        if( wind && gusting) trees[i]->applyForce( Vec3f( mag, 0.f, 0.f ) );
        trees[i]->onAnimate(.015);
      }
    }
  }

  virtual void onDraw( Graphics &g, const Viewpoint &v ) {
    for( int i=0; i<fabrics.size(); i++) fabrics[i]->onDraw(g);
    for( int i=0; i<trees.size(); i++) trees[i]->onDraw(g);
  }

  virtual void onSound( AudioIOData &io ) {
    static float g=0.f;
    float gain;
    if( wind ) gain = 1.0f;
    else gain = 0.0f;
     
    while(io()){
      float s = player();
      if( g < gain ) g += .00001f;
      if( g > gain ) g -= .000006f;
      io.out(0) = s * g;
      io.out(1) = s * g;
    }
  }

  virtual bool onKeyDown(const Keyboard& k){
    //cout << "you pressed: " << (char)k.key() << endl;
    
    ofstream out;

    switch( k.key() ){
      case 'g':
        gravity = gravity == 0.f ? -5.0f : 0.f ;
        cout << "gravity " << gravity << endl;
        break;
      case 't':
        wind = !wind;
        cout << "wind " << (wind ? "on!" : "off!") << endl;
        break;
      case 'y':
        out.open( "tree.xyz" );
        trees[1]->outputPointCloud( out );
        fabrics[1]->outputPointCloud( "fabric.xyz" );
        break;
      default:

        break;
    }
    
    return true;
  }

};

int main(){}

LsysApp app;
