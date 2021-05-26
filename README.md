# petrinets
This is an university project from the winter semester of 2020/2021.
It reads a petri net structure from a `*.pnml` file and displays it.
From there, the petrinet can be edited by adding or removing tokens from places or triggering transitions by hand.

Also, you can create the petri net's reachability graph to determine whether it is limited or not.
If triggered by hand, the new node will be displayed in the reachability graph, too.

Additionally, you can perform a bulk analysis of several `*.pnml` files using the text window to find out whether they are limited or not.

## Load a petri net and analyze it
You can do so by clicking "Datei" and then "Öffnen" to open a `*.pnml` file. Click "Analysiere" to create its reachability graph. 

The square nodes display transitions and the circle nodes are places. The places have a number of tokens which can be seen as a number, if they have any.
Places follow the naming conventions `[ID] name <number of tokens>`. Transitions follow `[ID] name`. 
Edges only have a name.

The reachability graph's orange path shows the path meeting the condition that the petri net is unlimited. 
The red node is the root node i.e. the start state of the petri net.
![A loaded petri net with its reachability graph.](./doc-img/screen1.PNG)

## Bulk analysis
You can also do this analysis in bulk by clicking "Datei", then "Analyse mehrerer Dateien".
The following text will show you whether each net is limited, the number of nodes and edges and well as the path meeting the condition.
![A bulk analysis in the text field.](./doc-img/screen-bulk-analysis.PNG)

## Edit tokens of a place
You can add tokens to a place by clicking the desired place and then "+" or "-".
That will change the number of tokens contained but also the reachability graph which will have a new root node.
![Added tokens to a place.](./doc-img/screen-more-tokens.PNG)

## Trigger transitions by hand
Do take a look at what happens if certain transitions are fired, you can simply click them.
The newest node in the reachability graph that results from firing will be displayed in green.
![An tranistion fired by hand.](./doc-img/screen-fire-by-hand.PNG)
