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
* A view controller that maintains and manages the relationship between the
* neural net model running in the background and the view presented to the user.
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
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Optional;
import java.util.Scanner;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * A view controller that maintains and manages the relationship between the
 * neural net model running in the background and the view presented to the
 * user.
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
	private Button resetButton;
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
	private CheckMenuItem selectSigmoidItem;
	@FXML
	private CheckMenuItem selectReLUItem;
	@FXML
	private CheckMenuItem selectTanhItem;
	@FXML
	private Menu activationFunctionMenu;

	// The model that the GUI is based on.
	private NeuralNet model;

	// The stage on which the view is presented.
	private Stage stage;

	// The current set of inputs, parsed from the last file.
	private double[][] inputs;

	// The current position of the iteration across indices.
	private int inputIndex = 0;

	// A property representing the learning rate that the user has provided.
	private transient SimpleDoubleProperty learningRateProperty;

	// The radius of the circles in the display.
	private static final double CIRCLE_RADIUS = 20;

	// The task running to teach the neural net.
	private NeuralNetTask task;

	/**
	 * Initializes the controller based on the default state of the neural net.
	 */
	@FXML
	public void initialize()
	{
		learningRateProperty = new SimpleDoubleProperty(NeuralNet.DEFAULT_ALPHA);
		selectSigmoidItem.setSelected(true);
		task = new NeuralNetTask(model, inputs, false);
		inputs = new double[0][0];
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
		createCircles(layers);
		stage.sizeToScene();
		createEdges();
		bindInfoLabels();
		stage.sizeToScene();
		createNeuronLabels();
	}

	// Binds the labels on the right side of the screen to their necessary text values.
	private void bindInfoLabels()
	{
		learningRateLabel.textProperty().bind(Bindings.concat("Learning Rate: ",
															  learningRateProperty));
		momentumLabel.textProperty().bind(
				Bindings.concat("Momentum Constant: ",
								Edge.getMomentumProperty()));
		maxSSELabel.textProperty().bind(Bindings.concat("Max SSE: ",
														model.getMaxErrorProperty()));
		activationFunctionLabel.setText(
				"Activation Function: " + model.getActivationFunction().toString());
		currentEpochLabel.textProperty().bind(Bindings.concat(
				"Epoch: ", model.getEpochsProperty()));
		maxEpochsLabel.textProperty().bind(Bindings.concat(
				"Max Epochs: ", model.getMaxEpochsProperty()));
		currentSSELabel.textProperty().bind(Bindings.concat("SSE: ",
															model.getAvgSSEProperty()));
	}

	// Creates the labels that show the thresholds for the neural net and those associated with inputs/outputs.
	private void createNeuronLabels()
	{
		// Threshold Label Creation
		for (int i = 0; i < 3; i++)
		{
			for (int j = 0; j < model.getLayers()[i].getNeurons().size(); j++)
			{
				VBox currentColumn = (VBox) neuralNetDisplayRow.getChildren().get(
						i);
				Label thresholdLabel = new Label();
				thresholdLabel.textFillProperty().set(Color.BLACK);
				thresholdLabel.textProperty().bind(
						model.getLayers()[i].getNeurons().get(j).getThetaProperty().asString(
								"%.1f"));
				neuralNetDisplayPane.getChildren().add(thresholdLabel);
				thresholdLabel.setLayoutX(
						currentColumn.getLayoutX() + currentColumn.getWidth() / 2 - CIRCLE_RADIUS / 2);
				thresholdLabel.setLayoutY(heightInVBox(j,
													   model.getLayers()[i].getNeurons().size(),
													   currentColumn.getSpacing()) + neuralNetDisplayPane.getHeight() / 2 - CIRCLE_RADIUS / 2);
				thresholdLabel.setTextAlignment(TextAlignment.CENTER);
			}
		}
		// Input/Output Label Creation
		for (int j = 0; j < model.getLayers()[0].getNeurons().size(); j++)
		{
			Label valueLabel = new Label();
			valueLabel.textFillProperty().set(Color.BLACK);
			valueLabel.textProperty().bind(
					model.getLayers()[0].getNeurons().get(j).getResultProperty().add(
							model.getLayers()[0].getNeurons().get(j).getThetaProperty()).asString(
							"%.2f"));
			valueLabel.setLayoutX(
					inputLayerColumn.getLayoutX() + inputLayerColumn.getWidth() / 2 - 5 * CIRCLE_RADIUS / 2);
			valueLabel.setLayoutY(heightInVBox(j,
											   model.getLayers()[0].getNeurons().size(),
											   inputLayerColumn.getSpacing()) + neuralNetDisplayPane.getHeight() / 2 - CIRCLE_RADIUS / 2);
			valueLabel.setTextAlignment(TextAlignment.CENTER);
			neuralNetDisplayPane.getChildren().add(valueLabel);
			valueLabel.toFront();
		}
		for (int j = 0; j < model.getLayers()[2].getNeurons().size(); j++)
		{
			Label valueLabel = new Label();
			valueLabel.textFillProperty().set(Color.BLACK);
			valueLabel.textProperty().bind(
					model.getLayers()[2].getNeurons().get(j).getResultProperty().asString(
							"%.2f"));
			valueLabel.setLayoutX(
					outputLayerColumn.getLayoutX() + inputLayerColumn.getWidth() / 2 + 3 * CIRCLE_RADIUS / 2);
			valueLabel.setLayoutY(heightInVBox(j,
											   model.getLayers()[2].getNeurons().size(),
											   outputLayerColumn.getSpacing()) + neuralNetDisplayPane.getHeight() / 2 - CIRCLE_RADIUS / 2);
			valueLabel.setTextAlignment(TextAlignment.CENTER);
			neuralNetDisplayPane.getChildren().add(valueLabel);
			valueLabel.toFront();
		}
	}

	// Creates the lines between the nodes in the display.
	private void createEdges()
	{
		for (int i = 0; i < hiddenLayerColumn.getChildren().size(); i++)
		{
			for (int j = 0; j < inputLayerColumn.getChildren().size(); j++)
			{
				Line edge = new Line();
				edge.setStartX(
						inputLayerColumn.getLayoutX() + inputLayerColumn.getWidth() / 2);
				edge.setStartY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(j,
																			inputLayerColumn.getChildren().size(),
																			inputLayerColumn.getSpacing()));
				edge.setEndX(
						hiddenLayerColumn.getLayoutX() + hiddenLayerColumn.getWidth() / 2);
				edge.setEndY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(i,
																			hiddenLayerColumn.getChildren().size(),
																			hiddenLayerColumn.getSpacing()));
				final SimpleDoubleProperty weightProperty = model.getLayers()[0].getNeurons().get(
						j).getOutEdges().get(i).getWeightProperty();
				edge.strokeProperty().bind(Bindings.when(
						model.getLayers()[0].getNeurons().get(j).getOutEdges().get(
								i).getWeightProperty().lessThan(
										0)).then(Color.RED).otherwise(
						Color.GREEN));

				edge.strokeWidthProperty().bind(Bindings.createDoubleBinding(
						() ->
				{
					return new SigmoidActivationFunction().calcOutput(
							weightProperty.get()) * 10;
				},
						weightProperty)
				);
				neuralNetDisplayPane.getChildren().add(edge);
				edge.toBack();
			}
		}
		for (int i = 0; i < outputLayerColumn.getChildren().size(); i++)
		{
			for (int j = 0; j < hiddenLayerColumn.getChildren().size(); j++)
			{
				Line edge = new Line();
				edge.setStartX(
						hiddenLayerColumn.getLayoutX() + hiddenLayerColumn.getWidth() / 2);
				edge.setStartY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(j,
																			hiddenLayerColumn.getChildren().size(),
																			hiddenLayerColumn.getSpacing()));
				edge.setEndX(
						outputLayerColumn.getLayoutX() + outputLayerColumn.getWidth() / 2);
				edge.setEndY(
						neuralNetDisplayPane.getHeight() / 2 + heightInVBox(i,
																			outputLayerColumn.getChildren().size(),
																			outputLayerColumn.getSpacing()));
				final SimpleDoubleProperty weightProperty = model.getLayers()[1].getNeurons().get(
						j).getOutEdges().get(i).getWeightProperty();
				edge.strokeProperty().bind(Bindings.when(
						weightProperty.lessThan(
								0)).then(Color.RED).otherwise(
						Color.GREEN));
				edge.strokeWidthProperty().bind(Bindings.createDoubleBinding(
						() ->
				{
					return new SigmoidActivationFunction().calcOutput(
							weightProperty.get()) * 10;
				},
						weightProperty)
				);
				neuralNetDisplayPane.getChildren().add(edge);
				edge.toBack();
			}
		}
	}

	// Creates the cirular nodes representing neurons in the display.
	private void createCircles(Layer[] layers)
	{
		for (int j = 0; j < 3; j++)
		{
			for (int i = 0; i < layers[j].getNeurons().size(); i++)
			{
				Circle neuronRepresentation = new Circle(CIRCLE_RADIUS);
				neuronRepresentation.fillProperty().set(Color.SKYBLUE);
				neuronRepresentation.strokeProperty().set(Color.BLACK);
				((VBox) (neuralNetDisplayRow.getChildren().get(j))).getChildren().add(
						neuronRepresentation);
				neuronRepresentation.toFront();
			}
		}
	}

	// Calculates the height of a circle based on its position in the VBox element (relative to the center of the VBox).
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
				"Comma Separated Values (.csv)", "*.csv"));
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
				model.resetInternals();
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
						"Neural Net File (.dat)", "*.dat"));
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
						"Neural Net File (.dat)", "*.dat"));
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
		if (canLearn())
		{
			task.cancel();
			task = new NeuralNetTask(model, new double[][]
							 {
								 inputs[inputIndex]
			}, true);
			inputIndex = (inputIndex + 1) % inputs.length;
			runTask();
		}
	}

	@FXML
	private void onSingleEpochButtonClick()
	{
		if (canLearn())
		{
			task.cancel();
			task = new NeuralNetTask(model, inputs, true);
			runTask();
		}
	}

	// Determines whether or not the machine can learn based on whether or not the controller has the correct number of inputs.
	private boolean canLearn()
	{
		return (inputs.length > 0 && inputs[0].length == model.getLayers()[0].getNeurons().size() + model.getLayers()[2].getNeurons().size());
	}

	@FXML
	private void onClassifyButtonClick()
	{
		double[][] classifications = new double[inputs.length][];
		double[][] realInputs = Arrays.copyOf(inputs, inputs.length);
		int inputNeurons = model.getLayers()[0].getNeurons().size();
		int outputNeurons = model.getLayers()[2].getNeurons().size();
		double[][] expectedOutputs = null;
		if (realInputs.length == 0)
		{
			return;
		}
		boolean testing = realInputs[0].length == inputNeurons + outputNeurons;
		if (testing)
		{
			expectedOutputs = new double[realInputs.length][realInputs[0].length];
			for (int i = 0; i < realInputs.length; i++)
			{
				expectedOutputs[i] = Arrays.copyOfRange(realInputs[i],
														inputNeurons,
														realInputs[i].length);
				realInputs[i] = Arrays.copyOfRange(realInputs[i], 0,
												   inputNeurons);
			}
		}
		for (int i = 0; i < realInputs.length; i++)
		{
			classifications[i] = model.classify(realInputs[i]);
		}
		double testSSE = 0;
		if (testing)
		{
			for (int i = 0; i < realInputs.length; i++)
			{
				for (int j = 0; j < classifications[i].length; j++)
				{
					testSSE += Math.pow(
							classifications[i][j] - expectedOutputs[i][j], 2);
				}
			}
			testSSE /= classifications.length;
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("Classification SSE");
			alert.setHeaderText(null);
			alert.setContentText(String.format("The classification SSE is %f",
											   testSSE));
			alert.showAndWait();
		}
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Classification File");
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(
						"Comma Separated Values (.csv)", "*.csv"));
		File exportFile = fileChooser.showSaveDialog(new Stage());

		System.out.println("Classifying...");
		while (exportFile != null)
		{
			try
			{
				PrintWriter out = new PrintWriter(new FileOutputStream(
						exportFile));
				String[][] classificationStrings = new String[classifications.length][classifications[0].length];
				for (int i = 0; i < classificationStrings.length; i++)
				{
					for (int j = 0; j < classificationStrings[i].length; j++)
					{
						classificationStrings[i][j] = String.valueOf(
								classifications[i][j]);
					}
					out.print(String.join(",", classificationStrings[i]));
					if (i < classificationStrings.length - 1)
					{
						out.println();
					}
				}
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
	private void onLearnButtonClick()
	{
		if (canLearn())
		{
			task.cancel();
			task = new NeuralNetTask(model, inputs, false);
			runTask();
		}
	}

	@FXML
	private void onStopButtonClick()
	{
		task.cancel();
	}

	@FXML
	private void onResetButtonClick()
	{
		task.cancel();
		resetNeuralNet(model.getActivationFunction());
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

	// Resets the neural net to a newly-generated one based on its neurons in each layer.
	private void resetNeuralNet(ActivationFunction actFunc)
	{
		NeuralNet newModel = new NeuralNet(new int[]
		{
			model.getLayers()[0].getNeurons().size(), model.getLayers()[1].getNeurons().size(), model.getLayers()[2].getNeurons().size()
		}, null);
		newModel.setMaxEpochs(model.getMaxEpochs());
		newModel.setMaxError(model.getMaxError());
		setModel(newModel, actFunc);
	}

	// Creates a Java FX popup box that only accepts when an appropriate integer is presented.
	// Returns -1 if closed without receiving meaningful input.
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

	// Creates a Java FX popup box that only accepts when an appropriate decimal value is presented.
	// Returns -1 if closed without receiving meaningful input.
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
	 * Sets the stage that the controller is working in to the given value.
	 *
	 * @param stage the stage that the controller is working with
	 */
	public void setStage(Stage stage)
	{
		this.stage = stage;
	}

	// Runs the thread to learn on the current data set.
	private void runTask()
	{
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}

	/**
	 * This is the thread that encapsulates the code that facilitates the
	 * learning of the neural net
	 */
	class NeuralNetTask extends Task<Void>
	{

		private final NeuralNet neuralNet;
		private final double[][] inputs;
		private final boolean oneEpoch;

		/**
		 * Constructs the task with the model and the number of iterations to
		 * run through
		 */
		public NeuralNetTask(NeuralNet neuralNet, double[][] inputs,
							 boolean oneEpoch)
		{
			this.neuralNet = neuralNet;
			this.inputs = inputs;
			this.oneEpoch = oneEpoch;
		}

		/**
		 * call - you must override this method in your Task class! This handles
		 * the actual computations in the separate thread!
		 *
		 * @return null
		 * @throws Exception if you messed up
		 */
		@Override
		protected Void call() throws Exception
		{
			int epochsToRun = neuralNet.getMaxEpochs() - neuralNet.getEpochs();
			if (oneEpoch)
			{
				if (inputs.length > 1)
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							neuralNet.learn(inputs, learningRateProperty.get(),
											1, true);
						}
					});
				}
				else
				{
					Platform.runLater(new Runnable()
					{
						@Override
						public void run()
						{
							neuralNet.learnSingle(inputs[0],
												  learningRateProperty.get());
						}
					});
				}
				return null;
			}
			for (int i = 0; i < epochsToRun; i++)
			{
				Platform.runLater(new Runnable()
				{
					@Override
					public void run()
					{
						if (neuralNet.getAvgSSE() > neuralNet.getMaxError())
						{
							neuralNet.learn(inputs, learningRateProperty.get(),
											1, false);
						}
					}
				});
				if (isCancelled())
				{
					updateMessage("Cancelled");
					break;
				}
			}

			return null;
		}
	}
}
