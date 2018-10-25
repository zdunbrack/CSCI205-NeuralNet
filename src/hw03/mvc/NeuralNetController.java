/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 23, 2018
* Time: 11:13:42 AM
*
* Project: csci205_hw03
* Package: hw03.controller
* File: NeuralNetController
* Description:
*
* ****************************************
 */
package hw03.mvc;

import hw03.model.LeakyReLUActivationFunction;
import hw03.model.NeuralNet;
import hw03.utility.SigmoidActivationFunction;
import hw03.utility.TanhActivationFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author zachd
 */
public class NeuralNetController
{

	@FXML
	private Pane neuralNetDisplayPane;
	@FXML
	private HBox neuralNetDisplayRow;
	@FXML
	private VBox rootBox;
	@FXML
	private MenuItem loadNetworkItem;
	@FXML
	private MenuItem exitItem;
	@FXML
	private VBox inputLayerColumn;
	@FXML
	private VBox hiddenLayerColumn;
	@FXML
	private VBox outputLayerColumn;
	@FXML
	private VBox statusBox;
	@FXML
	private Label statusLabel;
	@FXML
	private Label currentSSELabel;
	@FXML
	private Label currentEpochLabel;
	@FXML
	private Label currentFileLabel;
	@FXML
	private Button singleStepButton;
	@FXML
	private Button singleEpochButton;
	@FXML
	private Button classifyButton;
	@FXML
	private Button learnButton;
	@FXML
	private Button stopButton;
	@FXML
	private MenuItem selectFileItem;
	@FXML
	private MenuItem saveNetworkItem;
	@FXML
	private MenuItem setLearningRateItem;
	@FXML
	private MenuItem setMomentumItem;
	@FXML
	private MenuItem setMaxEpochsItem;
	@FXML
	private MenuItem setEpochsPerUpdateItem;
	@FXML
	private MenuItem selectSigmoidItem;
	@FXML
	private MenuItem selectReLUItem;
	@FXML
	private MenuItem selectTanhItem;

	private NeuralNet model;

	private Stage stage;

	private double[][] inputs;

	private static final double CIRCLE_RADIUS = 20;

	@FXML
	public void initialize()
	{
		assertNonNull();
	}

	/**
	 * Sets the model for the controller and initializes the display based on
	 * the model.
	 *
	 * @param model the neural net being worked with
	 */
	public void setModel(NeuralNet model)
	{
		this.model = model;
		initializeNeuralNetDisplay();
	}

	// Creates the display representing the system's current neural net.
	private void initializeNeuralNetDisplay()
	{
		inputLayerColumn.getChildren().clear();
		hiddenLayerColumn.getChildren().clear();
		outputLayerColumn.getChildren().clear();
		neuralNetDisplayPane.getChildren().clear();
		neuralNetDisplayPane.getChildren().add(neuralNetDisplayRow);
		Layer[] layers = model.getLayers();
		for (int i = 0; i < layers[0].getNeurons().size(); i++)
		{
			Circle neuronRepresentation = new Circle(CIRCLE_RADIUS);
			neuronRepresentation.fillProperty().set(Color.RED);
			inputLayerColumn.getChildren().add(neuronRepresentation);
		}
		for (int i = 0; i < layers[1].getNeurons().size(); i++)

		{
			Circle neuronRepresentation = new Circle(CIRCLE_RADIUS);
			neuronRepresentation.fillProperty().set(Color.GREEN);
			hiddenLayerColumn.getChildren().add(neuronRepresentation);
		}
		for (int i = 0; i < layers[2].getNeurons().size(); i++)
		{
			Circle neuronRepresentation = new Circle(CIRCLE_RADIUS);
			neuronRepresentation.fillProperty().set(Color.BLUE);
			outputLayerColumn.getChildren().add(neuronRepresentation);
		}
		stage.sizeToScene();
		for (int i = 0; i < hiddenLayerColumn.getChildren().size(); i++)
		{
			for (int j = 0; j < inputLayerColumn.getChildren().size(); j++)
			{
				Line edge = new Line();
				edge.setStartX(
						inputLayerColumn.getLayoutX() + inputLayerColumn.getWidth() / 2 + CIRCLE_RADIUS);
				edge.setStartY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(j,
																			inputLayerColumn.getChildren().size(),
																			inputLayerColumn.getSpacing()));
				edge.setEndX(
						hiddenLayerColumn.getLayoutX() + hiddenLayerColumn.getWidth() / 2 - CIRCLE_RADIUS);
				edge.setEndY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(i,
																			hiddenLayerColumn.getChildren().size(),
																			hiddenLayerColumn.getSpacing()));
				neuralNetDisplayPane.getChildren().add(edge);
			}
		}
		for (int i = 0; i < outputLayerColumn.getChildren().size(); i++)
		{
			for (int j = 0; j < hiddenLayerColumn.getChildren().size(); j++)
			{
				Line edge = new Line();
				edge.setStartX(
						hiddenLayerColumn.getLayoutX() + hiddenLayerColumn.getWidth() / 2 + CIRCLE_RADIUS);
				edge.setStartY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(j,
																			hiddenLayerColumn.getChildren().size(),
																			hiddenLayerColumn.getSpacing()));
				edge.setEndX(
						outputLayerColumn.getLayoutX() + outputLayerColumn.getWidth() / 2 - CIRCLE_RADIUS);
				edge.setEndY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(i,
																			outputLayerColumn.getChildren().size(),
																			outputLayerColumn.getSpacing()));
				neuralNetDisplayPane.getChildren().add(edge);
			}
		}
		stage.sizeToScene();
	}

	private double heightInVBox(int index, int numNeurons, double spacing)
	{
		double numSections = -1 * ((numNeurons - 1) / 2.0 - index) + 0.5;
		return numSections * (2 * CIRCLE_RADIUS + spacing) - CIRCLE_RADIUS - spacing / 2;
	}

	@FXML
	private void onSelectFileItemClick()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Training/Classification File");
		fileChooser.getExtensionFilters().clear();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
				"Comma Separated Values", "*.csv"));
		File dataFile = fileChooser.showOpenDialog(stage);
		Scanner fileScanner;
		while (dataFile != null)
		{
			try
			{
				fileScanner = new Scanner(dataFile);
				String fileString = "";
				while (fileScanner.hasNext())
				{
					fileString += fileScanner.nextLine();
					fileString += "\n";
				}
				fileString = fileString.substring(0, fileString.length() - 1);
				String[] inputSets = fileString.split("\n");
				String[][] inputStrings = new String[inputSets.length][];
				int numInputs = model.getLayers()[0].getNeurons().size();
				int numOutputs = model.getLayers()[model.getLayers().length - 1].getNeurons().size();
				for (int i = 0; i < inputStrings.length; i++)
				{
					inputStrings[i] = inputSets[i].split(",");
					if (!(inputStrings[i].length == numInputs + numOutputs || inputStrings[i].length == numInputs))
					{
						throw new IllegalArgumentException(
								"Input file could not be parsed as classification, testing, or learning input.");
					}
				}
				inputs = new double[inputStrings.length][inputStrings[0].length];
				for (int i = 0; i < inputStrings.length; i++)
				{
					for (int j = 0; j < inputStrings[i].length; j++)
					{
						inputs[i][j] = Double.parseDouble(inputStrings[i][j]);
					}
				}
				break;
			} catch (IOException | IllegalArgumentException e)
			{
				dataFile = fileChooser.showOpenDialog(stage);
			}
		}
	}

	@FXML
	private void onSaveNetworkItemClick()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Configuration File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(
						"Neural Net Data File", "*.dat"));
		File exportFile = fileChooser.showSaveDialog(stage);
		ObjectOutputStream out;
		while (exportFile != null)
		{
			try
			{
				out = new ObjectOutputStream(
						new FileOutputStream(exportFile));
				out.writeObject(model);
				out.flush();
				out.close();
				break;
			} catch (IOException e)
			{
				exportFile = fileChooser.showSaveDialog(stage);
			}
		}
	}

	@FXML
	private void onLoadNetworkItemClick()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Configuration File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(
						"Neural Net Data File", "*.dat"));
		File importFile = fileChooser.showOpenDialog(stage);
		NeuralNet importedNet;
		while(importFile != null)
		{
			try
			{
				ObjectInputStream in = new ObjectInputStream(
						new FileInputStream(
								importFile));
				importedNet = (NeuralNet)in.readObject();
				setModel(importedNet);
				break;
			} catch(IOException | ClassNotFoundException e)
			{
				importFile = fileChooser.showOpenDialog(stage);
			}
		}
	}

	@FXML
	private void onExitItemClick()
	{
		System.exit(0);
	}

	@FXML
	private void onSingleStepButtonClick()
	{
	}

	@FXML
	private void onSingleEpochButtonClick()
	{
	}

	@FXML
	private void onClassifyButtonClick()
	{
	}

	@FXML
	private void onLearnButtonClick()
	{
	}

	@FXML
	private void onStopButtonClick()
	{
	}

	@FXML
	private void onSetLearningRateItemClick()
	{
		// set field
	}

	@FXML
	private void onSetMomentumItemClick()
	{
		model.setMomentumConstant(0);
	}

	@FXML
	private void onSetMaxEpochsItemClick()
	{
		model.setMaxEpochs(0);
	}

	@FXML
	private void onSetEpochsPerUpdateItemClick()
	{
		// set field
	}

	@FXML
	private void onSelectSigmoidItemClick()
	{
		resetNeuralNet();
		model.setActivationFunction(new SigmoidActivationFunction());
	}

	@FXML
	private void onSelectReLUItemClick()
	{
		resetNeuralNet();
		model.setActivationFunction(new LeakyReLUActivationFunction());
	}

	@FXML
	private void onSelectTanhItemClick()
	{
		resetNeuralNet();
		model.setActivationFunction(new TanhActivationFunction());
	}

	private void resetNeuralNet()
	{
		setModel(new NeuralNet(model.getResetLayers(), null));
	}

	private void assertNonNull()
	{
		assert rootBox != null : "fx:id=\"rootBox\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert neuralNetDisplayRow != null : "fx:id=\"neuralNetDisplayRow\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert neuralNetDisplayPane != null : "fx:id=\"neuralNetDisplayPane\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert loadNetworkItem != null : "fx:id=\"loadNetworkItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert exitItem != null : "fx:id=\"exitItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert inputLayerColumn != null : "fx:id=\"inputLayerColumn\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert hiddenLayerColumn != null : "fx:id=\"hiddenLayerColumn\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert outputLayerColumn != null : "fx:id=\"outputLayerColumn\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert statusBox != null : "fx:id=\"statusBox\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert statusLabel != null : "fx:id=\"statusLabel\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert currentSSELabel != null : "fx:id=\"currentSSELabel\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert currentEpochLabel != null : "fx:id=\"currentEpochLabel\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert currentFileLabel != null : "fx:id=\"currentFileLabel\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert singleStepButton != null : "fx:id=\"singleStepButton\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert singleEpochButton != null : "fx:id=\"singleEpochButton\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert classifyButton != null : "fx:id=\"classifyButton\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert learnButton != null : "fx:id=\"learnButton\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert stopButton != null : "fx:id=\"stopButton\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert selectFileItem != null : "fx:id=\"selectFileItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert saveNetworkItem != null : "fx:id=\"saveNetworkItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert setLearningRateItem != null : "fx:id=\"setLearningRateItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert setMomentumItem != null : "fx:id=\"setMomentumItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert setMaxEpochsItem != null : "fx:id=\"setMaxEpochsItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert setEpochsPerUpdateItem != null : "fx:id=\"setEpochsPerUpdateItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert selectSigmoidItem != null : "fx:id=\"selectSigmoidItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert selectReLUItem != null : "fx:id=\"selectReLUItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
		assert selectTanhItem != null : "fx:id=\"selectTanhItem\" was not injected: check your FXML file 'NeuralNetView.fxml'.";
	}

	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
}
