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
WTP does grab a .dot-file and a configuration file and generates a nice looking image from the data. You can download an example .dot-file [here](https://drive.google.com/folderview?id=0B7TXAQUsQGPQfnozOWRBVVExR2s3SmVnb1RJa0t2VmJoZFNRSk5QMFBaYnN1VlhXSjl4WXc&usp=sharing) (kindly hosted by @FinnIckler). Please note, that using the bigger files will consume about 5GB RAM.  
Get your latest release of WeirdTreePLot (coming soon) or build your own Jar-file from sources.

For starters, we will use the file `wiki_sorted_20k.dot` and the configuration file `plotter_example.conf`. Move both into the same directory as your `ẁeird-tree-plot-X.X-static-dependencies.jar`.  
WeirdTreePlot accepts parameters both written in the configuration file and passed as command line options. For the beginning, we will only pass parameters through the command line. Open a terminal, navigate into the directory with our three files in and enter the following command:
```
java -jar weird-tree-plot-X.X-static-dependencies.jar -GRAPHinputDOTfile wiki_sorted_20k.dot -GRAPHrootCaption Philosophie
```
Now, a window should pop up, which illustrates the current plotting process. In the terminal, additional information is provided. Just take a walk in the meantime, this will take about 20min an on average machine. After the process is finished, you will find several output files in your directory.  
If you want to dig deeper into the capabilities of WeirdTreePlot, have a look into the `plotter_example.conf`, where every parameter is documented.

If you have additional questions, notes or bug reports, I'd be glad if you contact me on GitHub or Twitter.


# Building from sources
## GNU/Linux and OS X
Maven and Git are required. You propably know where to get it. If not, you might have luck with typing `sudo apt-get install git mvn` into a terminal.
Now enter the following commands into your terminal:
```
git clone https://github.com/erictapen/weird-tree-plot.git
cd weird-tree-plot
mvn package
```
If the build was successful, the resulting Jar file is located at `target/weird-tree-plot-YOUR_VERSION-static-dependencies.jar`
Move this Jar file into the same directory as your configuration file and get started!

## Windows
Unfortunately, I don't have a step-by-step solution here. You might get it running using Maven. If you got a working solution, please create a Pull Request!


#Data :
@FinnIckler hosted some example data [here](https://drive.google.com/folderview?id=0B7TXAQUsQGPQfnozOWRBVVExR2s3SmVnb1RJa0t2VmJoZFNRSk5QMFBaYnN1VlhXSjl4WXc&usp=sharing).
I will write another application about how to to get this kind of data from Wikipedia as soon as possible.
