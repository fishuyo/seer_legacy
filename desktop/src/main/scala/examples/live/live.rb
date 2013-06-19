
$Main = com.fishuyo.live.Main


Mouse.clear()
Mouse.use()
Mouse.bind("drag", lambda { |x,y,i,b|
	r = Camera.ray(x,y)
    hit = Quad[].intersect(r)

    v = hit.get()
    v.set( (v + Vec3.apply(1,1,0)) * 0.5 ) 
    x = (v.x*$Main.n)
    y = (v.y*$Main.n)

    r = Randf.apply(0,1,false)
    c = RGBA.apply( r[],r[],r[],r[])
    $Main.field.set(x,y,1)
})


def step( dt )

 $Main.field.sstep(dt)
 #gameoflife(dt)
end



def gameoflife(dt)
	field = $Main.field
    for y in 0...field.h/10
    	for x in 0...field.w/10

    		count = 0.0
    		for j in -1..1
    			for i in -1..1
    				count += field.getToroidal(x+i,j+j).r
    			end
    		end

    		val = field.get(x,y).r
    		if val > 0.0
    			count -= 1.0
    			if count == 2.0 || count == 3.0
    				field.next.set(x,y,1)
    			else
    				field.next.set(x,y,0)
    			end
    		elsif val == 0.0
    			if count == 3.0
    				field.next.set(x,y,1)
    			else
    				field.next.set(x,y,0)
    			end
    		end

    	end
    end
    for i in 0...field.w*field.h
    	field.set(i, field.next.apply(i))
    end
end





