require 'java'
Seer = Java::com.fishuyo
Key = Seer.io.Keyboard

looper = Seer.examples.loop.Main.looper
l = 0

Key.clear()
Key.use()
Key.bind("1",lambda{ l=0 })
Key.bind("2",lambda{ l=1 })
Key.bind("3",lambda{ l=2 })
Key.bind("4",lambda{ l=3 })
Key.bind("5",lambda{ l=4 })
Key.bind("6",lambda{ l=5 })
Key.bind("7",lambda{ l=6 })
Key.bind("8",lambda{ l=7 })
Key.bind("r",lambda{ looper.toggleRecord(l)})
Key.bind("c",lambda{ looper.clear(l) })
Key.bind("s",lambda{ looper.stack(l) })
Key.bind("t",lambda{ 
	if looper.loops[l].playing
		looper.loops[l].stop()
	else
		looper.loops[l].play()
	end
})
Key.bind("v",lambda{ looper.reverse(l) })
Key.bind("q",lambda{ looper.switchTo(l) })



