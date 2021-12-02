___
# Seer System Design

___
# Seer System Motivation
- Seer is a software platform to explore expressive tools for embodied worldmaking
- inspired by existing creative coding software
-- i.e. Processing, OpenFrameworks, allolib, Field, Max-msp, Touch Designer
- inspired by rapid-prototyping capabilities of live-coding methodology
-- i.e. Extempore, Gibber, Tidal cycles, SuperCollider
- felt needs for more expressive and modular systems that scale
- written in the general purpose programming language Scala
- a novel combination of creative coding and live-coding methods

___
# Creativity Support for Embodied Worldmaking 
- Creativity Support Tools (Shneiderman 2007), design principles:
  -- support exploratory search (+++)
  -- enable collaboration (+)
  -- provide rich history keeping (+)
  -- design with low thresholds, high ceilings, and wide walls (++)

- Ways of supporting embodied worldmaking process?
  -- rapid exploration of generative and biomimetic systems
  -- rapid exploration of embodied instrument design
  -- spatiotemporal composition and layering of sketches
  -- scaling across distributed performance systems

___
# Supporting Rapid Exploration
- novel actor-based live coding system
- actors are a form of asynchronous computation using message passing
- monitors scripts for changes and reloads actor into running audiovisual system
- implement the init, draw and audioIO methods
- receive method handles messages

___
$grid 1 1 0 0 2.5
! /Users/fishuyo/_projects/2020_dissertation/media/demo/cube.png

___
# Rapid Exploration of Biomimetic Programs
- expressive terse syntax
- high level abstractions for graphics and audio
- reusable worldmaking primitive components:
  -- particle systems, spring constraints, attractors
  -- vector fields and fluid simulation
  -- recursive branching data structures
  -- octree spatial partitioning data structure

___
$grid 1 1 0 0 2.5
! /Users/fishuyo/_projects/2020_dissertation/media/demo/particles.png 


___
# Rapid Exploration of Embodied Instruments
- diverse set of modules supporting various sensors and interface devices
  --  OpenCV, Kinect, VRPN tracking systems, HID devices, multi-touch trackpads 
- Flow: Dataflow Mapping Language
  -- domain specific language for creating interaction mappings
  -- stand alone browser based environment for mapping device data via OSC
  -- integrated into Seer as well
- Generic data stream sampling
  -- sampling embodied input data as resource
- Pickable raycasting api
  -- mapping directed inputs to virtual materials

// flow example

___
# Spatiotemporal Composition
- Parameters and simple user interfaces
  -- parameters house mutable state within a separate actor
  -- provides a thread safe asynchronous boundary backed by a synchronous cached value
  -- persistent across script reloads
  -- easy to load and save parameters as named presets
  -- easy to generate pickable UI
- Timeline api for chaining events in time
  -- building sequences
  -- interpolating state transitions

___
# Scaling to Distributed Performance Systems
- performance systems often require many computers to run effectively
  -- driving multiple projectors, multi-channel audio, and other devices 
  -- monolithic programs require a lot of work to re-design for distributed systems
- actor based implementation runs on the Akka Actor framework
- actors are independent units of computation
- message passing works across the network as well
- makes it easy to scale a system across different computers with little changes

