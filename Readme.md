Seer
========

Seer is a framework for building and live coding body-driven interactive audio visual worlds for performance. Seer is the result of practice-led research in improvisational dance performance systems that incorporate body, motion tracking, immersive three-dimensional graphics, and digital audio. Seer is written in the Scala programming language and provides a number of high-level abstractions for developing the performance environment in real-time.

-------

## Getting Started

NOTE: only tested on OSX -- some dependencies will be missing on other platforms

install sbt from here: [http://www.scala-sbt.org/](http://www.scala-sbt.org/)

cd into cloned repo and run `sbt`

an interactive sbt console will eventually be ready, from here run: `downloadLibs`  this installs some necessary unmanaged libraries to the local lib directories.

to Run the Live Coding test app run the following commands:

~~~~
project iclc
run
~~~~

Open up your favorite text editor for the script files located at: `<clone_path>/iclc/scripts/workspace/live.scala`

There are a few examples located here: `<clone_path>/iclc/scripts/examples` copy and paste their contents into live.scala and save the file to trigger the script to load.





