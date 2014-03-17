############################
# global state 
############################
$Main = com.fishuyo.seer.examples.fieldViewer.live.Main  # Main class
$go = Vec3.new(1,0,0)

$pos = Vec3.new(0,0,0)
$zoom = Vec3.new(0.8,0,0)


#############################
# run Every Frame
#############################

def step( dt )
    field = $Main.fieldViewer.field

    $Main.fieldViewer.resize(50,50)    

    for i in 0..field.w 
        for j in 0..field.h
            #field.set(i,j,1.0) if i.abs == j.abs
        end
    end

    diffuse(dt) if $go.x == 1.0 
    #gameoflife(dt) if $go.x == 1.0

end


############################
# mouse events
############################

Mouse.clear()
Mouse.use()
Mouse.bind("drag", lambda { |i|
    x = i[0]
    y = i[1]
    
    r = Camera.ray(x,y)
    hit = Quad[].intersect(r)

    v = hit.get()
    v.set( (v + Vec3.apply(1,1,0)) * 0.5 ) 
    x = (v.x*$Main.fieldViewer.w)
    y = (v.y*$Main.fieldViewer.h)

    #r = Randf.apply(0,1,false)
    #c = RGBA.apply( r[],r[],r[],0.1)

    $Main.fieldViewer.field.set(x,y,1.0)
})

############################
# osx trackpad interaction
############################

Trackpad.clear()
Trackpad.connect()

# called for each finger down
# f is an array containting [x,y,dx,dy,size,angle]
Trackpad.bindEach( lambda{|fingerID,f|
    x = f[0]*($Main.fieldViewer.w-1)
    y = f[1]*($Main.fieldViewer.h-1)
    r = (f[4]*2).to_i
    for j in -r..r
        for i in -r..r
            val = 1.0
            val = 1.0/(i.abs+j.abs) unless i==0 && j==0
            # $Main.field.set(x+i,y+j,val)
        end
    end
})

# called aggregating all fingers
# f is an array containting [centroidX,centroidY,dx,dy,size]
Trackpad.bind( lambda{|fingersDown,f|
    if fingersDown == 3
        $zoom.set( $zoom + Vec3.new(f[3]*0.001,0,0) )
    elsif fingersDown == 4
        $pos.set( $pos + Vec3.new(f[3]*-0.001,f[2]*-0.001,0) )
    end
})


############################
# Keyboard events
############################

Keyboard.clear()
Keyboard.use()
Keyboard.bind('g', lambda{ 
    $go.set(1-$go.x,0,0)
})
Keyboard.bind('c', lambda{ 
    mandlebrot()
})



#########################
# content step functions
#########################

def gameoflife(dt)
    field = $Main.fieldViewer.field
    nextf = $Main.fieldViewer.next
    for y in 0...field.h
        for x in 0...field.w

            count = field.multiplyKernel(x,y,[1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0,1.0])

            val = field.get(x,y).r
            if val > 0.0
                count -= 1.0
                if count == 2.0 || count == 3.0
                    nextf.set(x,y,1.0)
                else
                    nextf.set(x,y,0.0)
                end
            elsif val == 0.0
                if count == 3.0
                    nextf.set(x,y,1.0)
                else
                    nextf.set(x,y,0.0)
                end
            end

        end
    end
    field.set(nextf)
end

def diffuse(dt)
    field = $Main.fieldViewer.field
    nextf = $Main.fieldViewer.next
    
    alpha = 0.1
    dtt = 0.1
    dxx = 1.0
    for y in 0...field.h
        for x in 0...field.w

            lap = field.multiplyKernel(x,y,[0.0,1.0,0.0,1.0,-4.0,1.0,0.0,1.0,0.0])
            val = field.get(x,y).r + dtt * alpha * lap / (dxx)
            
            nextf.set(x,y,val)

            # nextf.set(x,y,1.0) if x < 10 && y < 10

        end
    end
    field.set(nextf)
    # field.set(1.0)
end

def mandlebrot()
    field = $Main.fieldViewer.field

    for y in 0...field.h
        for x in 0...field.w
          p = Vec3.new( $pos.x + x * 4.0*$zoom.x / field.w - 2.0*$zoom.x, $pos.y + y * 4.0*$zoom.x / field.h - 2.0*$zoom.x, 0);
          orig = Vec3.new(0,0,0) + p
          
          t = 0

          while p.mag < 2.0 && t < 100 do
            xx = p.x*p.x - p.y*p.y
            yy = 2*p.x*p.y
            p = Vec3.new( xx, yy, 0)
            #p = Vec3( p.x*p.x, p.y*p.y, 0)
            p += orig
            t += 1
          end

          field.set(y,x,t*0.01)

        end
    end
    #field.normalize()
end



