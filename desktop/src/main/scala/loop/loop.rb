require 'java'
Seer = Java::com.fishuyo
Key = Seer.io.Keyboard
Trackpad = Seer.io.Trackpad

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
Key.bind("r",lambda{ looper.toggleRecord(l) })
Key.bind("c",lambda{ looper.clear(l) })
Key.bind("s",lambda{ looper.stack(l) })
Key.bind("t",lambda{ looper.togglePlay(l) })
Key.bind("v",lambda{ looper.reverse(l) })
Key.bind("q",lambda{ looper.switchTo(l) })


Trackpad.clear()
Trackpad.connect()
Trackpad.bind( lambda{ |i,f| 

	if i == 3
		#looper.setGain(l,2*f[1])
		looper.setPan(l,f[0])
	elsif i == 2
		looper.setGain(l,f[6])
		#print f[5]
		#puts f[6]
		looper.setBounds(l,f[5],f[7])


	end
})




