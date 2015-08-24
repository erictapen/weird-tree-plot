# About WeirdTreePlot
This tool was originally an attempt, to create a "map" of the whole Wikipedia, which one can hang up on the wall. It turned out to be useful as a graph plotter for somewhat special trees.
![Alt text](example_result.jpg?raw=true "The wrong sizes of the text is Gimp's fault. In the svg (also included) it looks better.")

# Do I need it?
You might like it, if your tree has
* several 100k nodes.
* different node sizes with about 10 orders of magnitude between.
* very large amounts of children on one node, like 100k.

Also it should be okay for you, that
* plots could need about a few hours.
* connected nodes won't be necessarily drawn next to each other. It's about the big picture.
* edges won't be drawn. WeirdTreePlot is more about an artistical view on data. 
* there are some restrictions about layout possibilities, input and output format.


# How do I use it?
WTP does grab a .dot-file and optionally a configuration file and generates a nice looking image from the data. You can download an example .dot-file [here](https://drive.google.com/folderview?id=0B7TXAQUsQGPQfnozOWRBVVExR2s3SmVnb1RJa0t2VmJoZFNRSk5QMFBaYnN1VlhXSjl4WXc&usp=sharing) (kindly hosted by @FinnIckler). Please note, that using the bigger files will consume about 5GB RAM.
Get your latest release of WeirdTreePLot (coming soon) or build your own Jar-file from sources.


# Building from sources
## GNU/Linux and OS X
Maven and Git are required. You propably know where to get it. If not, you might have luck with typing `sudo apt-get install git mvn` into a terminal.
Keep that terminal open, clone this repository and move into the directory.
```
git clone https://github.com/erictapen/weird-tree-plot.git
cd weird-tree-plot
```
Build the project using Maven.
```
mvn package
```
The resulting jar file is located at `target/weird-tree-plot-YOUR_VERSION-static-dependencies.jar
There are two other files, but you may want to use this one, because it does'nt need any dependencies.

## Windows
Unfortunately, I don't have a step-by-step solution here. You might get it running using Maven. If you got a working solution, please create a Pull Request!


#Data :
The Data can be downloaded as .dot here:
https://drive.google.com/folderview?id=0B7TXAQUsQGPQfnozOWRBVVExR2s3SmVnb1RJa0t2VmJoZFNRSk5QMFBaYnN1VlhXSjl4WXc&usp=sharing
Without this file, you can't do anything. It is just too big for GitHub.
