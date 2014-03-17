
$Main = com.fishuyo.seer.examples.fieldViewer.fractal.Main
$Main.field.zoom.set(1)
$Main.field.pos.set(0)

Trackpad.clear()
Trackpad.connect()
Trackpad.bind( lambda{|i,f|
	zoom = $Main.field.zoom
	pos = $Main.field.pos
    if i == 3
        zoom.set( zoom + zoom*f[3]*0.01 )
    elsif i == 4
        pos.set( pos + Vec3.new(f[3]*-0.01,f[2]*-0.01,0)*zoom.x )
    end
})


def step( dt )
    $Main.field.resize(150,150)
    $Main.field.step(dt)
end