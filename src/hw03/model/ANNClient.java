/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 7, 2016
 * Time: 6:40:50 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: ANNClient
 * Description:
 * A client designed to work with the NeuralNet class and provide a
 * console-based interface for the user to create or import neural networks
 * before training them or using them to classify data.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import hw03.utility.SigmoidActivationFunction;
import hw03.utility.TanhActivationFunction;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/**
 * A client designed to work with the NeuralNet class and provide a
 * console-based interface for the user to create or import neural networks
 * before training them or using them to classify data.
 *
 * @author cld028
 */
public final class ANNClient implements Serializable
{

	// Scanner linked to standard input, used for console input.
	private static final Scanner sc = new Scanner(System.in);
	// The neural net used for this run of the program.
	private static NeuralNet neuralNet;
	// Whether or not the neural net is in learn mode (testing/classifying if false).
	private static boolean learning;
	// The set of input lists that the neural net will be classifying or learning from.
	private static double[][] inputs;
	// Whether or not the neural net should terminate at the end of this run.
	private static boolean terminating;
	// Whether the program is testing a data set with expected outputs (classifying only if false)
	private static boolean testing;

	// Private constructor to prevent initialization
	private ANNClient()
	{
	}

	/**
	 * Creates a new {@link NeuralNet} and uses it to train on or classify data.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		terminating = false;
		initializeNeuralNet();
		while (!(terminating))
		{
			determineMode();
			takeDataSet();
			if (learning)
			{
				teachNeuralNet();
			}
			else
			{
				classifyData();
			}
			askIfTerminating();
		}
	}

	private static void initializeNeuralNet()
	{
		String resp;
		while (true)
		{
			System.out.println(
					"Would you like to create a new neural net or import your own config file?");
			System.out.print(
					"Respond \"new\" for a new neural net or \"import\" to import one: ");
			resp = sc.next();
			if (resp.equals("new") || resp.equals("import"))
			{
				break;
			}
			System.out.println(
					"Please respond with one of the provided options.");
		}
		if (resp.equals("new"))
		{
			neuralNet = promptNeuralNet();
			initializeActivationFunction();
		}
		else
		{
			neuralNet = importNeuralNet();
		}
	}

	private static void initializeActivationFunction()
	{
		String resp;
		while (true)
		{
			System.out.println(
					"What activation function would you like to use?");
			System.out.print(
					"Respond \"Sigmoid\", \"Tanh\", or \"ReLU\" ");
			resp = sc.next();
			if (resp.equals("Sigmoid") || resp.equals("ReLU") || resp.equals(
					"Tanh"))
			{
				break;
			}
			System.out.println(
					"Please respond with one of the provided options.");
		}
		switch (resp)
		{
			case "Sigmoid":
				neuralNet.setActivationFunction(new SigmoidActivationFunction());
				break;
			case "ReLU":
				neuralNet.setActivationFunction(
						new LeakyReLUActivationFunction());
				break;
			case "Tanh":
				neuralNet.setActivationFunction(new TanhActivationFunction());
				break;
			default:
		}
	}

	// Prompts the user to enter information essential to the construction of the neural net.
	private static NeuralNet promptNeuralNet()
	{
		System.out.print("How many inputs should your neural net have? ");
		int numInputs = sc.nextInt();
		System.out.print("How many hidden layers should your neural net have? ");
		int hiddenLayers = sc.nextInt();
		if (hiddenLayers > 0)
		{
			System.out.print(
					"How many neurons would you like in your hidden layers? ");
		}
		int neuronsPerHiddenLayer = sc.nextInt();
		System.out.println("Assigning random initial weights...");
		return constructNeuralNet(numInputs, hiddenLayers, neuronsPerHiddenLayer);
	}

	// Imports a neural net from a file containing edges and a file containing threshold values.
	private static NeuralNet importNeuralNet()
	{
		ObjectInputStream in;
		NeuralNet n;
		System.out.print("In which file is your neural net stored? ");
		while (true)
		{
			String fileName = sc.next();
			try
			{
				in = new ObjectInputStream(new FileInputStream(fileName));
				n = (NeuralNet) in.readObject();
				break;
			} catch (FileNotFoundException e)
			{
				System.out.print(
						"File not found. Please enter another file name. ");
			} catch (IOException ex)
			{
				System.out.print(
						"Invalid NeuralNet file. Please enter another file name. ");
			} catch (ClassNotFoundException ex)
			{
				System.out.print(
						"Invalid NeuralNet file. Please enter another file name. ");
			}
		}
		return n;
	}

	//Constructs a neural net based on the given arguments.
	private static NeuralNet constructNeuralNet(int numInputs, int hiddenLayers,
												int neuronsPerHiddenLayer)
	{
		ArrayList<Layer> layers = new ArrayList();
		layers.add(new InputLayer(numInputs));
		for (int i = 0; i < hiddenLayers; i++)
		{
			layers.add(new HiddenLayer(neuronsPerHiddenLayer));
		}
		layers.add(new OutputLayer(1));
		NeuralNet n = new NeuralNet(layers, new RandomWeightAssignment()
							{
								@Override
								public double assignWeight()
								{
									Random rnd = new Random();
									boolean pos = rnd.nextBoolean();
									//Get random double 0 < rndNum < 2.4/inputs
									double rndNum = rnd.nextDouble();
									rndNum = rndNum * 2.4 / numInputs;
									if (pos)
									{
										return (rndNum);
									}
									else
									{
										return (-rndNum);
									}
								}
							});
		return n;
	}

	// Asks for filenames until a file is found, then returns a Scanner reading that file.
	private static Scanner createImportScanner(String message)
	{
		Scanner fileScanner;
		System.out.print(message + " ");
		while (true)
		{
			String fileName = sc.next();
			File importFile = new File(fileName);
			try
			{
				fileScanner = new Scanner(importFile);
				break;
			} catch (FileNotFoundException e)
			{
				System.out.print(
						"File not found. Please enter another file name. ");
			}
		}
		return fileScanner;
	}

	// Asks for a file name until a file can be written to and returns a PrintWriter writing to that file.
	private static PrintWriter createExportPrintWriter(String message)
	{
		PrintWriter out;
		System.out.print(message + " ");
		while (true)
		{
			String fileName = sc.next();
			File exportFile = new File(fileName);
			try
			{
				out = new PrintWriter(exportFile);
				break;
			} catch (FileNotFoundException e)
			{
				System.out.print(
						"File not found. Please enter another file name. ");
			}
		}
		return out;
	}

	// Asks the user if they want to classify or learn with the network and updates accordingly.
	private static void determineMode()
	{
		String resp;
		while (true)
		{
			System.out.println(
					"Would you like to classify data or train the given neural net on a set of data?");
			System.out.print(
					"Respond \"classify\" to classify a given input set or \"train\" to train the neural net on a set of inputs and output(s): ");
			resp = sc.next();
			if (resp.equals("classify") || resp.equals("train"))
			{
				break;
			}
			System.out.println(
					"Please respond with one of the provided options.");
		}
		learning = resp.equals("train");
	}

	// Sets the input array to the values in a CSV file chosen by the user.
	private static void takeDataSet()
	{
		Scanner fileScanner = createImportScanner(
				"What file contains the list of input values for the neural network?");
		String fileString = "";
		while (fileScanner.hasNext())
		{
			fileString += fileScanner.nextLine();
			fileString += "\n";
		}
		fileString = fileString.substring(0, fileString.length() - 1);
		String[] inputSets = fileString.split("\n");
		String[][] inputStrings = new String[inputSets.length][];
		int numInputs = neuralNet.getLayers()[0].getNeurons().size();
		int numOutputs = neuralNet.getLayers()[neuralNet.getLayers().length - 1].getNeurons().size();
		for (int i = 0; i < inputStrings.length; i++)
		{
			inputStrings[i] = inputSets[i].split(",");
			if (inputStrings[i].length == numInputs + numOutputs)
			{
				testing = true;
			}
			else if (inputStrings[i].length == numInputs)
			{
				testing = false;
			}
			else
			{
				throw new IllegalArgumentException(
						"Input file could not be parsed as classification, testing, or learning input.\n");
			}
		}
		inputs = new double[inputStrings.length][inputStrings[0].length];
		for (int i = 0; i < inputStrings.length; i++)
		{
			for (int j = 0; j < inputStrings[i].length; j++)
			{
				try
				{
					inputs[i][j] = Double.parseDouble(inputStrings[i][j]);
				} catch (NumberFormatException e)
				{
					System.out.println(
							"Non-numeric value found in input file. ");
					System.out.println(
							"Please ensure that the file contains only decimal values "
							+ "in a CSV format as dictated in the readme.");
					System.out.println("Terminating program.");
					System.exit(0);
				}
			}
		}
	}

	// Teaches the neural net based on the previously-taken inputs.
	private static void teachNeuralNet()
	{
		System.out.print(
				"What would you like the to be maximum SSE at which the neural net will stop learning? ");
		neuralNet.setMaxError(sc.nextDouble());
		System.out.print("After how many epochs should the neural net give up? ");
		neuralNet.setMaxEpochs(sc.nextInt());
		neuralNet.learn(inputs);
		System.out.println("Performance metrics: ");
		System.out.println(
				"Average SSE after training: " + neuralNet.getAvgSSE());
		System.out.println(
				"Time to train: " + neuralNet.getSecondsToTrain() + "s");
		System.out.println("Number of epochs to train: " + neuralNet.getEpochs());
		exportNeuralNet();
	}

	// Writes the neural net to a file prompted from user.
	private static void exportNeuralNet()
	{
		ObjectOutputStream out;
		System.out.print(
				"In which file would you like to store the neural net? ");
		while (true)
		{
			String fileName = sc.next();
			try
			{
				out = new ObjectOutputStream(new FileOutputStream(fileName));
				out.writeObject(neuralNet);
				out.flush();
				out.close();
				break;
			} catch (FileNotFoundException e)
			{
				System.out.print(
						"File not found. Please enter another file name. ");
			} catch (IOException ex)
			{
				System.out.println(ex.getMessage());
				System.out.print(
						"Invalid output file. Please enter another file name. ");
			}
		}

	}

	// Classifies the user's input(s) and outputs the resulting value(s) to a file.
	private static void classifyData()
	{
		double[][] classifications = new double[inputs.length][];
		int inputNeurons = neuralNet.getLayers()[0].getNeurons().size();
		double[][] expectedOutputs = null;
		if (testing)
		{
			expectedOutputs = new double[inputs.length][inputs[0].length];
			for (int i = 0; i < inputs.length; i++)
			{
				expectedOutputs[i] = Arrays.copyOfRange(inputs[i], inputNeurons,
														inputs[i].length);
				inputs[i] = Arrays.copyOfRange(inputs[i], 0, inputNeurons);
			}
		}
		for (int i = 0; i < inputs.length; i++)
		{
			classifications[i] = neuralNet.classify(inputs[i]);
		}
		if (testing)
		{
			double testSSE = 0;
			for (int i = 0; i < inputs.length; i++)
			{
				for (int j = 0; j < classifications[i].length; j++)
				{
					testSSE += Math.pow(
							classifications[i][j] - expectedOutputs[i][j], 2);
				}
			}
			testSSE /= classifications.length;
			System.out.println(
					"Average sum of squared error over test data: " + testSSE);
		}
		outputClassifications(classifications);
	}

	// Outputs the classifications of the given input to a file.
	private static void outputClassifications(double[][] classifications)
	{
		PrintWriter out = createExportPrintWriter(
				"What file would you like to store the classification results in?");
		String[][] classificationStrings = new String[classifications.length][classifications[0].length];
		for (int i = 0; i < classificationStrings.length; i++)
		{
			for (int j = 0; j < classificationStrings[i].length; j++)
			{
				classificationStrings[i][j] = String.valueOf(
						classifications[i][j]);
			}
			out.print(String.join(" ", classificationStrings[i]));
			if (i < classificationStrings.length - 1)
			{
				out.println();
			}
		}
		out.flush();
		out.close();
	}

	// Asks the user if the program should terminate and updates accordingly.
	private static void askIfTerminating()
	{
		String resp;
		while (true)
		{
			System.out.println(
					"Would you like to terminate the program or continue working with this neural net?");
			System.out.print(
					"Respond \"terminate\" to terminate the program or \"continue\" to continue the program. ");
			resp = sc.next();
			if (resp.equals("terminate") || resp.equals("continue"))
			{
				break;
			}
			System.out.println(
					"Please respond with one of the provided options.");
		}
		terminating = resp.equals("terminate");
	}
}
