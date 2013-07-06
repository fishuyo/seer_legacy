

importPackage(com.fishuyo.io)
importPackage(com.fishuyo.maths)
importPackage(com.fishuyo.graphics)
importPackage(com.fishuyo.live)

Main.n = 200
println(Main.n())

Mouse.clear()
Mouse.use()
Mouse.bind("drag", function(x,y,i,b){ 
	var r = Camera.ray(x,y)
    var hit = Quad.apply().intersect(r)

    var v = hit.get()
    v.set( (v + Vec3.apply(1,1,0)) * 0.5 ) 
    var x = (v.x*Main.n)
    var y = (v.y*Main.n)

    var r = Randf.apply(0,1,false)
    var c = RGBA.apply( r.apply(),r.apply(),r.apply(),r.apply())
    Main.field.set(x,y,1)
})


function step( dt ){

  for( var x=10; x < 20; x++){
  	for( var y = 10; y < 20; y++)
  		Main.field.set(x,y,1)
  }
 //$Main.field.sstep(dt)
 //gameoflife(dt)
}






