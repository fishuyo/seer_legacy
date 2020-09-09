Seer
========

Seer is a framework for live-coding interactive audiovisual worlds. Seer is the result of practice-led research in improvisational dance performance distributed systems that incorporate body, motion tracking, immersive three-dimensional graphics, and interactive digital audio instrument. Seer is written in the Scala programming language and provides a number of high-level abstractions for developing the performance environment in real-time.

-------

## Getting Started

install simple build tool (sbt): [http://www.scala-sbt.org/](http://www.scala-sbt.org/)

On macOS using homebrew: `brew install sbt`

Running an example:
~~~~
cd seer
sbt
project examples
run
~~~~

then choose an example from list, or specify directly with:

~~~~
runMain seer.examples.audio.SimpleSynth
~~~~

You can make modifications to the running code by editing the file: `seer/examples/scripts/audio/synth.scala`

It will take a few seconds to recompile.

