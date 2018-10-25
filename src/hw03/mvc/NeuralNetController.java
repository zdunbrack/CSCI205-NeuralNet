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

import hw03.model.Edge;
import hw03.model.Layer;
import hw03.model.NeuralNet;
import hw03.utility.ActivationFunction;
import hw03.utility.LeakyReLUActivationFunction;
import hw03.utility.SigmoidActivationFunction;
import hw03.utility.TanhActivationFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.Scanner;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
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
	private MenuItem selectFileItem;
	@FXML
	private MenuItem saveNetworkItem;
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
	private Label learningRateLabel;
	@FXML
	private Label momentumLabel;
	@FXML
	private Label maxSSELabel;
	@FXML
	private Label epochsPerUpdateLabel;
	@FXML
	private Label activationFunctionLabel;
	@FXML
	private Label currentEpochLabel;
	@FXML
	private Label maxEpochsLabel;
	@FXML
	private Label currentSSELabel;
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
	MenuItem setNumInputNeuronsItem;
	@FXML
	MenuItem setNumHiddenNeuronsItem;
	@FXML
	MenuItem setNumOutputNeuronsItem;
	@FXML
	private MenuItem setLearningRateItem;
	@FXML
	private MenuItem setMomentumItem;
	@FXML
	private MenuItem setMaxSSEItem;
	@FXML
	private MenuItem setMaxEpochsItem;
	@FXML
	private MenuItem setEpochsPerUpdateItem;
	@FXML
	private CheckMenuItem selectSigmoidItem;
	@FXML
	private CheckMenuItem selectReLUItem;
	@FXML
	private CheckMenuItem selectTanhItem;
	@FXML
	private Menu activationFunctionMenu;

	private NeuralNet model;

	private Stage stage;

	private double[][] inputs;

	private transient SimpleDoubleProperty learningRateProperty;

	private transient SimpleIntegerProperty epochsPerUpdateProperty;

	private static final double CIRCLE_RADIUS = 20;

	private static final int DEFAULT_EPOCHS_PER_UPDATE = 10;

	/**
	 *
	 */
	@FXML
	public void initialize()
	{
		learningRateProperty = new SimpleDoubleProperty(NeuralNet.DEFAULT_ALPHA);
		epochsPerUpdateProperty = new SimpleIntegerProperty(
				DEFAULT_EPOCHS_PER_UPDATE);
		selectSigmoidItem.setSelected(true);
	}

	/**
	 * Sets the model for the controller and initializes the display based on
	 * the model.
	 *
	 * @param model the {@link hw03.model.NeuralNet} being worked with
	 * @param actFunc the {@link hw03.utility.ActivationFunction} that the
	 * neural net will use on non-input neurons
	 */
	public void setModel(NeuralNet model, ActivationFunction actFunc)
	{
		this.model = model;
		model.setActivationFunction(actFunc);
		if (actFunc.toString().equals("Sigmoid"))
		{
			selectSigmoidItem.setSelected(true);
			selectReLUItem.setSelected(false);
			selectTanhItem.setSelected(false);
		}
		else if (actFunc.toString().equals("Leaky ReLU"))
		{
			selectSigmoidItem.setSelected(false);
			selectReLUItem.setSelected(true);
			selectTanhItem.setSelected(false);
		}
		else
		{
			selectSigmoidItem.setSelected(false);
			selectReLUItem.setSelected(false);
			selectTanhItem.setSelected(true);
		}
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
				edge.strokeProperty().bind(Bindings.when(
						model.getLayers()[0].getNeurons().get(j).getOutEdges().get(
								i).getWeightProperty().lessThan(
										0)).then(Color.RED).otherwise(
						Color.GREEN));
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
				edge.strokeProperty().bind(Bindings.when(
						model.getLayers()[1].getNeurons().get(j).getOutEdges().get(
								i).getWeightProperty().lessThan(
										0)).then(Color.RED).otherwise(
						Color.GREEN));
				neuralNetDisplayPane.getChildren().add(edge);
			}
		}
		learningRateLabel.textProperty().bind(Bindings.concat("Learning Rate: ",
															  learningRateProperty));
		momentumLabel.textProperty().bind(
				Bindings.concat("Momentum Constant: ",
								Edge.getMomentumProperty()));
		maxSSELabel.textProperty().bind(Bindings.concat("Max SSE: ",
														model.getMaxErrorProperty()));
		epochsPerUpdateLabel.textProperty().bind(Bindings.concat(
				"Epochs per Update: ", epochsPerUpdateProperty));
		activationFunctionLabel.setText(
				"Activation Function: " + model.getActivationFunction().toString());
		currentEpochLabel.textProperty().bind(Bindings.concat(
				"Epoch: ", model.getEpochsProperty()));
		maxEpochsLabel.textProperty().bind(Bindings.concat(
				"Max Epochs: ", model.getMaxEpochsProperty()));
		currentSSELabel.textProperty().bind(Bindings.concat("SSE: ",
															model.getAvgSSEProperty()));
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
		File dataFile = fileChooser.showOpenDialog(new Stage());
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
				dataFile = fileChooser.showOpenDialog(new Stage());
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
		File exportFile = fileChooser.showSaveDialog(new Stage());
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
				e.printStackTrace();
				exportFile = fileChooser.showSaveDialog(new Stage());
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
		File importFile = fileChooser.showOpenDialog(new Stage());
		NeuralNet importedNet;
		while (importFile != null)
		{
			try
			{
				ObjectInputStream in = new ObjectInputStream(
						new FileInputStream(
								importFile));
				importedNet = (NeuralNet) in.readObject();
				importedNet.repair();
				setModel(importedNet, importedNet.getActivationFunction());
				break;
			} catch (IOException | ClassNotFoundException e)
			{
				importFile = fileChooser.showOpenDialog(new Stage());
			}
		}
	}

	@FXML
	private void onExitItemClick()
	{
		stage.close();
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
	private void onSetNumInputNeuronsItemClick()
	{
		int numInputNeurons = intTextPopup("Set Number of Input Neurons",
										   "Number of Input Neurons",
										   model.getLayers()[0].getNeurons().size(),
										   0,
										   25);
		if (numInputNeurons != -1 && numInputNeurons != model.getLayers()[0].getNeurons().size())
		{
			NeuralNet newModel = new NeuralNet(new int[]
			{
				numInputNeurons, model.getLayers()[1].getNeurons().size(), model.getLayers()[2].getNeurons().size()
			}, null);
			newModel.setMaxEpochs(model.getMaxEpochs());
			newModel.setMaxError(model.getMaxError());
			setModel(newModel, model.getActivationFunction());
		}
	}

	@FXML
	private void onSetNumHiddenNeuronsItemClick()
	{
		int numHiddenNeurons = intTextPopup("Set Number of Hidden Neurons",
											"Number of Hidden Neurons",
											model.getLayers()[1].getNeurons().size(),
											0,
											25);
		if (numHiddenNeurons != -1 && numHiddenNeurons != model.getLayers()[0].getNeurons().size())
		{
			NeuralNet newModel = new NeuralNet(new int[]
			{
				model.getLayers()[0].getNeurons().size(), numHiddenNeurons, model.getLayers()[2].getNeurons().size()
			}, null);
			newModel.setMaxEpochs(model.getMaxEpochs());
			newModel.setMaxError(model.getMaxError());
			setModel(newModel, model.getActivationFunction());
		}
	}

	@FXML
	private void onSetNumOutputNeuronsItemClick()
	{
		int numOutputNeurons = intTextPopup("Set Number of Output Neurons",
											"Number of Output Neurons",
											model.getLayers()[2].getNeurons().size(),
											0,
											25);
		if (numOutputNeurons != -1 && numOutputNeurons != model.getLayers()[2].getNeurons().size())
		{
			NeuralNet newModel = new NeuralNet(new int[]
			{
				model.getLayers()[0].getNeurons().size(), model.getLayers()[1].getNeurons().size(), numOutputNeurons
			}, null);
			newModel.setMaxEpochs(model.getMaxEpochs());
			newModel.setMaxError(model.getMaxError());
			setModel(newModel, model.getActivationFunction());
		}
	}

	@FXML
	private void onSetLearningRateItemClick()
	{
		double newRate = doubleTextPopup("Set Learning Rate", "Learning Rate",
										 learningRateProperty.get(), 0, 1);
		if (newRate != -1)
		{
			learningRateProperty.set(newRate);
		}
		stage.sizeToScene();
	}

	@FXML
	private void onSetMomentumItemClick()
	{
		double newMomentum = doubleTextPopup("Set Momentum Constant",
											 "Momentum Constant",
											 model.getMomentumConstantProperty(),
											 0, 1);
		if (newMomentum != -1)
		{
			model.setMomentumConstant(newMomentum);
		}
		stage.sizeToScene();
	}

	@FXML
	private void onSetMaxSSEItemClick()
	{
		double newError = doubleTextPopup("Set Max SSE", "Max SSE",
										  model.getMaxError(), 0,
										  Double.MAX_VALUE);
		if (newError != -1)
		{
			model.setMaxError(newError);
		}
		stage.sizeToScene();
	}

	@FXML
	private void onSetMaxEpochsItemClick()
	{
		int newMaxEpochs = intTextPopup("Set Max Epochs", "Max Epochs",
										model.getMaxEpochs(),
										0,
										Integer.MAX_VALUE);
		if (newMaxEpochs != -1)
		{
			model.setMaxEpochs(newMaxEpochs);
		}
		stage.sizeToScene();
	}

	@FXML
	private void onSetEpochsPerUpdateItemClick()
	{
		int newEpochsPerUpdate = intTextPopup("Set Epochs per Update",
											  "Epochs per Update",
											  epochsPerUpdateProperty.get(),
											  0,
											  Integer.MAX_VALUE);
		if (newEpochsPerUpdate != -1)
		{
			epochsPerUpdateProperty.set(newEpochsPerUpdate);
		}
		stage.sizeToScene();
	}

	@FXML
	private void onSelectSigmoidItemClick()
	{
		if (selectSigmoidItem.isSelected())
		{
			resetNeuralNet(new SigmoidActivationFunction());
		}
		selectSigmoidItem.setSelected(true);
		selectReLUItem.setSelected(false);
		selectTanhItem.setSelected(false);
	}

	@FXML
	private void onSelectReLUItemClick()
	{
		if (selectReLUItem.isSelected())
		{
			resetNeuralNet(new LeakyReLUActivationFunction());
		}
		selectSigmoidItem.setSelected(false);
		selectReLUItem.setSelected(true);
		selectTanhItem.setSelected(false);
	}

	@FXML
	private void onSelectTanhItemClick()
	{
		if (selectTanhItem.isSelected())
		{
			resetNeuralNet(new TanhActivationFunction());
		}
		selectSigmoidItem.setSelected(false);
		selectReLUItem.setSelected(false);
		selectTanhItem.setSelected(true);
	}

	private void resetNeuralNet(ActivationFunction actFunc)
	{
		NeuralNet newModel = new NeuralNet(model.getResetLayers(), null);
		newModel.setMaxEpochs(model.getMaxEpochs());
		newModel.setMaxError(model.getMaxError());
		setModel(newModel, actFunc);
	}

	private int intTextPopup(String title, String contentText,
							 int oldValue, int minimum,
							 int maximum)
	{
		TextInputDialog dialog = new TextInputDialog(String.valueOf(oldValue));
		dialog.setHeaderText(
				"Please provide an integer value between " + minimum + " (exclusive) and " + maximum + " (inclusive).");
		dialog.setTitle(title);
		dialog.setContentText(contentText);
		Optional<String> result = dialog.showAndWait();
		while (result.isPresent())
		{
			try
			{
				int num = Integer.parseInt(result.get());
				if (num <= minimum || num > maximum)
				{
					throw new NumberFormatException();
				}
				return num;
			} catch (NumberFormatException e)
			{
				dialog.setHeaderText(
						"The previous value was not a integer value in the given range.\n"
						+ "Please provide a integer value between " + minimum + " (exclusive) and " + maximum + " (inclusive).");
				result = dialog.showAndWait();
			}
		}
		return -1;
	}

	private double doubleTextPopup(String title, String contentText,
								   double oldValue, double minimum,
								   double maximum)
	{
		TextInputDialog dialog = new TextInputDialog(String.valueOf(oldValue));
		dialog.setHeaderText(
				"Please provide a decimal value between " + minimum + " (exclusive) and " + maximum + " (inclusive).");
		dialog.setTitle(title);
		dialog.setContentText(contentText);
		Optional<String> result = dialog.showAndWait();
		while (result.isPresent())
		{
			try
			{
				double num = Double.parseDouble(result.get());
				if (num <= minimum || num > maximum)
				{
					throw new NumberFormatException();
				}
				return num;
			} catch (NumberFormatException e)
			{
				dialog.setHeaderText(
						"The previous value was not a decimal value in the given range.\n"
						+ "Please provide a decimal value between " + minimum + " (exclusive) and " + maximum + " (inclusive).");
				result = dialog.showAndWait();
			}
		}
		return -1;
	}

	/**
	 *
	 * @param stage
	 */
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}
}
