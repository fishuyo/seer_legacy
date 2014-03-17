

importPackage(com.fishuyo.seer.io)
importPackage(com.fishuyo.seer.maths)
importPackage(com.fishuyo.seer.graphics)
importPackage(com.fishuyo.seer.examples.fieldViewer.live)

// Main.n = 200
// println(Main.n())
var fv = Main.fieldViewer()

Mouse.clear()
Mouse.use()
Mouse.bind("drag", function(i){ 
    var x = i[0]
    var y = i[1]
    println( x + " " + y)
	  var r = Camera().ray(x,y)
    var hit = Quad.apply().intersect(r)
    println(hit)

    var v = hit.get()
    v.set( (v + Vec3.apply(1,1,0)) * 0.5 ) 
    x = (v.x()*fv.w())
    y = (v.y()*fv.h())

    // var r = Randf.apply(0,1,false)
    // var c = RGBA.apply( r.apply(),r.apply(),r.apply(),r.apply())
    fv.field().set(x,y,1)
})

function step( dt ){

  fv.resize(30,30)

  var field = fv.field()

  for( var x=10; x < 20; x++){
  	for( var y = 10; y < 20; y++)
  		field.set(x,y,1)
  }
 //$Main.field.sstep(dt)
 // println( fv.next() )
  gameoflife()
}


function gameoflife(){
  field = fv.field()
  next = fv.next()

  var w = field.w()
  var h = field.h()

  for( var y=0; y<h; y++){
  for( var x=0; x<w; x++){
      
    var count = 0;
    for( var j=-1; j<=1; j++){
    for( var i=-1; i<=1; i++){
      count += field.getToroidal(x+i,y+j).r()
    }}
    if( field.get(x,y).r() > 0.0 ){
      count -= 1
      if( count == 2 || count == 3) next.set(x,y,1.0)
      else {
        next.set(x,y,0.0)
      //println( x + " " + y + " dieing")
      }
    }else if( field.get(x,y).r() == 0.0) { //was dead
      if( count == 3 ){
        next.set(x,y,1.0)
        //println( x + " " + y + " born")
      }
      else next.set(x,y,0.0)
    }

  }}

  field.set( next )
  
}





