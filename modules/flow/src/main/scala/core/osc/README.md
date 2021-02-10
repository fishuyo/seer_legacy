
# OSC Manager

The OSC Manager should be a singleton class handling the creation of other OSC actors. It also handles the port bindings so they can be shared between OSC actors.

The application should create one OSC Manager, and request actors on demand:

val manager = system.actorOf(OSCManagerActor.props)

manager ? OSCApiActor(oscConfig)
manager ? props


