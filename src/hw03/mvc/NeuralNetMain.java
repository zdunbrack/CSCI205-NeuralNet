/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 23, 2018
* Time: 5:22:25 PM
*
* Project: csci205_hw03
* Package: hw03.mvc
* File: NeuralNetMain
* Description:
* The main class that runs the entire application.
* ****************************************
 */
package hw03.mvc;

import hw03.model.NeuralNet;
import hw03.utility.SigmoidActivationFunction;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * The main class that runs the entire application.
 *
 * @author zachd
 */
public class NeuralNetMain extends Application
{

	NeuralNet model;
	NeuralNetController controller;

	/**
	 * Runs on initialization; instantiates the model.
	 */
	@Override
	public void init()
	{
		model = new NeuralNet();
	}

	/**
	 * Tries to start the Java FX application.
	 *
	 * @param primaryStage the stage on which the application will be presented
	 * @throws Exception if loading the FXML file fails
	 */
	@Override
	public void start(Stage primaryStage) throws Exception
	{
		FXMLLoader loader = new FXMLLoader(
			this.getClass().getClassLoader().getResource(
				"hw03/mvc/NeuralNetView.fxml"));

		Parent root = (Parent) loader.load();

		controller = loader.getController();
		Scene scene = new Scene(root);
		primaryStage.setOnShown((WindowEvent event) ->
		{
			controller.setStage(primaryStage);
			controller.setModel(model, new SigmoidActivationFunction());
		});
		primaryStage.setResizable(false);
		primaryStage.setTitle("Neural Net GUI");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	/**
	 * Runs the neural net GUI.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		launch(args);
	}

}
