

$fv = com.fishuyo.seer.examples.fieldViewer.reactdiffuse.barkley.Main.fv


############################
# Called every frame
############################

def step( dt )

	$fv.resize(150,150)

    field = $fv.field

    # set 40 by 40 square in center of field to 1 every frame
    x = field.w / 2
    y = field.h / 2
    for j in y-20..y+20
    	for i in x-20..x+20
    		# field.set(i,j,1.0)
    	end
    end
end


############################
# Keyboard events
############################

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r", lambda{ 
    $fv.toggleRunning()
})


############################
# mouse events
############################

Mouse.clear()
Mouse.use()

# set value to 1.0 where mouse dragged
Mouse.bind("drag", lambda { |i|
    x = i[0]
    y = i[1]

	# convert click to position in field
    r = Camera.ray(x,y)
    hit = Quad[].intersect(r)
    v = hit.get()
    v.set( (v + Vec3.apply(1,1,0)) * 0.5 ) 

    x = (v.x*$fv.w)
    y = (v.y*$fv.h)

    $fv.chemA.set(x,y,1.0)
})

############################
# osx trackpad interaction
############################
Trackpad.clear()
Trackpad.connect()

Trackpad.bind( lambda{ |fingers,f|
    field = $fv.field

    x = f[0]*(field.w-1)
    y = f[1]*(field.h-1)
    r = (f[4]*1.5).to_i
    for j in -r..r
        for i in -r..r
            $fv.chemA.set(x+i,y+j,1.0) if fingers == 2
            $fv.chemB.set(x+i,y+j,1.0) if fingers == 3
        end
    end
})











