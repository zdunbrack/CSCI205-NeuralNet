# CSCI 205 Neural Net Project
## Made by: Zach Dunbrack, Josh Dunbrack
For the most part, everything in this project that is present is functional.
We did not have time to set up all of the alerts that you would see, so if you do something wrong, the only indication is going to be that nothing changes in the state of the program.
## Configuring the ANN
### Handling Input Files
To set up the ANN, you must first give it a file to process.
The client expects a CSV file with each input set on its own line.
If the file contains inputs and expected outputs, it can be used for learning or classification.
If the file contains only inputs, it can only be used for classification.
### Saving and Loading Neural Nets:
To save or load neural nets, simply use the "Save" and "Load" buttons under the "File" menu.
There are two example neural nets provided in 3NeuronAND.dat and 4NeuronXOR.dat.
Both of these neural nets have already learned on their associated data set.
### Configuring the Layers
Each layer can have the number of neurons encapsulated in it adjusted under the "ANN Options > Configure ANN" submenu.
Changing the number of neurons in the ANN will reset the weights and thresholds of the neural net.
### Configuring the Parameters
The specific parameters that refine the learning process of the ANN can also be adjusted under the "ANN Options" menu.
If an acceptable value is presented in an input box and the dialog is submitted, then the value of the associated parameter will update.
You can see the current values of each of the parameters on the right sidebar.
Changing the activation function will reset the weights and thresholds of the neural net.
## Running the ANN
### Learning Over Single Steps
Once you have loaded in a set of data, you can progress through the data one step at a time.
Stepping through a single step will start with the first input in the given file.
After the first step, the program will keep track of its location in the file and will continue from there for future single-step calls.
Learning on the data or running single epochs will not reset this location - it remains fixed until a new file is loaded.
The single step will loop if the stepping reaches the end of the given set of inputs.
SSE would not be particularly meaningful for a single step, so it is not reported and leaves the previous values as it was.
### Learning Over Single Epochs
Once you have loaded in a set of data, you can progress through the data one epoch at a time.
This functionality allows the neural net to undergo one epoch's full iteration of back-propagation.
The reported SSE after a single epoch run is the SSE for that particular epoch.
### Classifying Data
If classification data is loaded in, then pressing the "Classify" button will begin the classification process on the main thread.
After classification is complete, you will see a prompt for a file in which to save the classification data.
Once you select a location, you will be able to view the classification results there as a CSV file.
If the classification data is labeled, then an alert will show presenting the SSE of the classification.
### Learning On Data
If learning data is loaded in, then pressing the "Learn" button will begin the learning process on a secondary thread.
As the learning process progresses, the GUI will update to reflect the status of the neural net.
During the process, pressing the "Stop" button will terminate the learning thread at the end of the currently-running epoch.
### Resetting the Neural Net
The reset button resets the neural net as described above.
This involves re-randomizing the weights and resetting all of the thresholds to their default values.
Parameters like the maximum number of epochs to learn on and the learning rate are maintained.
However, the learning information like SSE and number of epochs run is reset as well.
