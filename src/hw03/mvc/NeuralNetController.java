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

import hw03.model.Layer;
import hw03.model.NeuralNet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
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

	private static double CIRCLE_RADIUS = 20;

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
	private void onLoadNetworkItemClick()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Configuration File");
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
			"Neural Net Data File", "*.dat"));
		File importFile = fileChooser.showOpenDialog(stage);
		NeuralNet importedNet;
		while (importFile != null)
		{
			try
			{
				ObjectInputStream in = new ObjectInputStream(
					new FileInputStream(
						importFile));
				importedNet = (NeuralNet) in.readObject();
				setModel(importedNet);
				break;
			} catch (IOException | ClassNotFoundException e)
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSingleEpochButtonClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onClassifyButtonClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onLearnButtonClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onStopButtonClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSelectFileItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSaveNetworkItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSetLearningRateItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSetMomentumItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSetMaxEpochsItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSetEpochsPerUpdateItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSelectSigmoidItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSelectReLUItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@FXML
	private void onSelectTanhItemClick()
	{
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	private void assertNonNull()
	{
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
