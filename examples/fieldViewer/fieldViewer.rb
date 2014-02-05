############################
# FieldViewer Interaction Script
# 	save this file to reload it
############################

$fieldViewer = com.fishuyo.seer.examples.fieldViewer.Main.fieldViewer


############################
# Called every frame
############################

def runEverytime( dt )

	$fieldViewer.resize(200,200)

    field = $fieldViewer.field

    # set 40 by 40 square in center of field to 1 every frame
    x = field.w / 2
    y = field.h / 2
    for j in y-20..y+20
    	for i in x-20..x+20
    		field.set(i,j,1.0)
    	end
    end
end


############################
# Keyboard events
############################

Keyboard.clear()
Keyboard.use()
Keyboard.bind("r", lambda{ 
    $fieldViewer.toggleRunning()
})


############################
# mouse events
#    "up" -> [x,y,pointer,button]
#    "down" -> [x,y,pointer,button]
#    "drag" -> [x,y,pointer]
#    "move" -> [x,y]
#    "scroll" -> [amount]
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

    x = (v.x*$fieldViewer.w)
    y = (v.y*$fieldViewer.h)

    $fieldViewer.field.set(x,y,1.0)
})

############################
# osx trackpad interaction
############################

Trackpad.clear()
Trackpad.connect()

# called for each finger down
# f is an array containting [x,y,dx,dy,size,angle]
Trackpad.bindEach( lambda{ |fingerID,f|
	field = $fieldViewer.field

    x = f[0]*(field.w-1)
    y = f[1]*(field.h-1)
    r = (f[4]*1.5).to_i
    for j in -r..r
        for i in -r..r
            field.set(x+i,y+j,1.0)
        end
    end
})








