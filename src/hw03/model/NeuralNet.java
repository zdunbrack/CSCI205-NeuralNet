/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 7, 2016
 * Time: 6:41:23 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: NeuralNet
 * Description:
 * A collection of Layers that represents a neural net capable of
 * training on and classifying given data sets.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import hw03.utility.ActivationFunction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * A collection of {@link Layer} objects that represents a neural net capable of
 * training on and classifying given data sets.
 *
 * @author cld028
 */
public class NeuralNet implements Serializable
{

	// The singular input layer in the neural net.
	private InputLayer inputLayer;
	// The list of hidden layers in the neural net.
	private HiddenLayer[] hiddenLayers;
	// The singular output layer in the neural net.
	private OutputLayer outputLayer;
	// The maximum sum of squares error at which the machine will stop learning.
	private double maxError = 0.001;
	// The maximum number of runs for which the neural network should learn.
	private int maxEpochs = 10000;
	// The average SSE upon termination of learning over the given data.
	private double avgSSE = 1;
	// The amount of time spent learning on the given data.
	private double secondsToTrain = 0;
	// The number of epochs that the neural net has taken to learn on the given data.
	private int epochs = 0;
	// The maximum sum of squares error at which the machine will stop learning.
	private transient SimpleDoubleProperty maxErrorProperty = new SimpleDoubleProperty(
			maxError);
	// The maximum number of runs for which the neural network should learn.
	private transient SimpleIntegerProperty maxEpochsProperty = new SimpleIntegerProperty(
			maxEpochs);
	// The average SSE upon termination of learning over the given data.
	private transient SimpleDoubleProperty avgSSEProperty = new SimpleDoubleProperty(
			0);
	// The amount of time spent learning on the given data.
	private transient SimpleDoubleProperty secondsToTrainProperty = new SimpleDoubleProperty(
			secondsToTrain);
	// The number of epochs that the neural net has taken to learn on the given data.
	private transient SimpleIntegerProperty epochsProperty = new SimpleIntegerProperty(
			epochs);
	/**
	 * The default learning rate for the neural net.
	 */
	public static double DEFAULT_ALPHA = 0.2;

	/**
	 * The default method of assigning weights to all {@link Edge} objects in
	 * the network.
	 */
	protected static WeightAssignment DEFAULT_WEIGHT_ASSIGNMENT = new RandomWeightAssignment();
	/**
	 * The method of assigning weights to all {@link Edge} objects in the
	 * network.
	 */
	protected WeightAssignment weightAssignment = DEFAULT_WEIGHT_ASSIGNMENT;

	/**
	 * Generates a neural net with a 2-neuron input layer, a 3-neuron hidden
	 * layer, and a 1-neuron output layer.
	 */
	public NeuralNet()
	{
		inputLayer = new InputLayer(2);
		HiddenLayer hiddenLayer = new HiddenLayer(3);
		outputLayer = new OutputLayer(1);
		hiddenLayers = new HiddenLayer[]
		{
			hiddenLayer
		};
		connectLayers(new Layer[]
		{
			inputLayer, hiddenLayer, outputLayer
		});
	}

	/**
	 * Generates a neural net with {@link Layer} objects contructed from the
	 * given {@code numNeurons} array, assigning initial weights to the
	 * {@link Edge} objects as dictated in {@code weightAssignment}.
	 *
	 * @param numNeurons the number of {@link Neuron} objects involved in each
	 * layer of the neural net
	 * @param weightAssignment the method of assigning weights; between -0.5 and
	 * 0.5 randomly distributed if null
	 * @throws NeuralNetConstructionException if there are any elements of
	 * {@code numNeurons} that are at most 0
	 */
	public NeuralNet(int[] numNeurons, WeightAssignment weightAssignment)
	{
		if (weightAssignment == null)
		{
			weightAssignment = DEFAULT_WEIGHT_ASSIGNMENT;
		}
		this.weightAssignment = weightAssignment;
		Layer[] layers = new Layer[numNeurons.length];
		if (numNeurons.length < 2)
		{
			throw new NeuralNetConstructionException(
					"Cannot construct a neural net with fewer than 2 layers.");
		}
		this.inputLayer = new InputLayer(numNeurons[0]);
		layers[0] = this.inputLayer;
		hiddenLayers = new HiddenLayer[layers.length - 2];
		for (int i = 1; i < numNeurons.length - 1; i++)
		{
			if (numNeurons[i] <= 0)
			{
				throw new NeuralNetConstructionException(
						"Cannot construct neural net with 0 or fewer neurons.");
			}
			hiddenLayers[i - 1] = new HiddenLayer(numNeurons[i]);
			layers[i] = hiddenLayers[i - 1];
		}
		this.outputLayer = new OutputLayer(
				numNeurons[numNeurons.length - 1]);
		layers[layers.length - 1] = this.outputLayer;
		connectLayers(layers);
	}

	/**
	 * Generates a neural net with the given {@link Layer} objects, assigning
	 * initial weights to the {@link Edge} objects as dictated in
	 * {@code weightAssignment}.
	 *
	 * @param layers the layers involved in the neural net
	 * @param weightAssignment the method of assigning weights; between -0.5 and
	 * 0.5 randomly distributed if null
	 * @throws NeuralNetConstructionException if the neural net is given
	 * nonsensical inputs for construction
	 */
	public NeuralNet(List<Layer> layers, WeightAssignment weightAssignment)
	{
		if (weightAssignment == null)
		{
			weightAssignment = DEFAULT_WEIGHT_ASSIGNMENT;
		}
		this.weightAssignment = weightAssignment;
		if (layers.size() < 2 || !(layers.get(0) instanceof InputLayer) || !(layers.get(
																			 layers.size() - 1) instanceof OutputLayer))
		{
			throw new NeuralNetConstructionException(
					"Input and output layers were not appropriately provided.");
		}
		this.inputLayer = (InputLayer) layers.get(0);
		this.hiddenLayers = new HiddenLayer[layers.size() - 2];
		this.outputLayer = (OutputLayer) layers.get(layers.size() - 1);
		for (int i = 1; i < layers.size() - 1; i++)
		{
			if (!(layers.get(i) instanceof HiddenLayer))
			{
				throw new NeuralNetConstructionException(
						"Intermediate layer was not constructed as hidden layer.");
			}
			hiddenLayers[i - 1] = (HiddenLayer) layers.get(i);
		}
		Layer[] layerArray = new Layer[layers.size()];
		for (int i = 0; i < layerArray.length; i++)
		{
			layerArray[i] = layers.get(i);
		}
		connectLayers(layerArray);
	}

	/**
	 * Uses the current state of the system to pass {@code data} through the
	 * neural net and takes the results from each {@link Neuron} in the
	 * {@link OutputLayer}.
	 *
	 * @param data an array of inputs to pass into the input neurons
	 * @return an array of outputs representing the final values at each of the
	 * output neurons
	 */
	public double[] classify(double[] data)
	{
		inputLayer.fireNeurons(data);
		for (HiddenLayer layer : hiddenLayers)
		{
			layer.fireNeurons();
		}
		outputLayer.fireNeurons();
		return outputLayer.getNetResults();
	}

	/**
	 * Creates a 3D jagged array representing all of the weights used in the
	 * neural net and returns that array. Each dimension represents a different
	 * component of the neural net. The first dimension represents the set of
	 * layers in the neural net; the second dimension represents the set of
	 * neurons in a given layer; and the third dimension represents the set of
	 * edges coming log of a given neuron.
	 *
	 * @return a three-dimensional array holding all of the weights in the
	 * neural net
	 */
	public double[][][] getEdgeWeights()
	{
		Layer[] layers = new Layer[1 + hiddenLayers.length];
		layers[0] = inputLayer;
		for (int i = 1; i < layers.length; i++)
		{
			layers[i] = hiddenLayers[i - 1];
		}
		double[][][] weights = new double[layers.length][][];
		for (int i = 0; i < layers.length; i++)
		{
			ArrayList<Neuron> neurons = layers[i].getNeurons();
			weights[i] = new double[neurons.size()][];
			for (int j = 0; j < neurons.size(); j++)
			{
				ArrayList<Edge> edges = neurons.get(j).getOutEdges();
				weights[i][j] = new double[edges.size()];
				for (int k = 0; k < edges.size(); k++)
				{
					Edge e = edges.get(k);
					weights[i][j][k] = e.getWeight();
				}
			}
		}
		return weights;
	}

	/**
	 * Creates a 2D jagged array representing all of the threshold values used
	 * in the neural net and returns that array. Each dimension represents a
	 * different component of the neural net. The first dimension represents the
	 * set of layers in the neural net; the second dimension represents the set
	 * of neurons in a given layer, each of which has a given threshold
	 * associated with it.
	 *
	 * @return a two-dimensional array holding all of the thresholds in the
	 * neural net
	 */
	public double[][] getThetas()
	{
		double[][] thetas = new double[2 + hiddenLayers.length][];
		thetas[0] = new double[inputLayer.getNeurons().size()];
		for (int i = 0; i < inputLayer.getNeurons().size(); i++)
		{
			thetas[0][i] = inputLayer.getNeurons().get(i).getTheta();
		}
		for (int i = 1; i < thetas.length - 1; i++)
		{
			thetas[i] = new double[hiddenLayers[i - 1].getNeurons().size()];
			for (int j = 0; j < hiddenLayers[i - 1].getNeurons().size(); j++)
			{
				thetas[i][j] = hiddenLayers[i - 1].getNeurons().get(j).getTheta();
			}
		}
		thetas[thetas.length - 1] = new double[outputLayer.getNeurons().size()];
		for (int i = 0; i < outputLayer.getNeurons().size(); i++)
		{
			thetas[thetas.length - 1][i] = outputLayer.getNeurons().get(i).getTheta();
		}
		return thetas;
	}

	/**
	 * Splits the {@code learningData} array into a list of inputs and outputs
	 * and calls the {@link Layer#learn(double alpha) learn} method of the
	 * {@link OutputLayer} and each {@link HiddenLayer} from end to beginning.
	 * The method then iterates the above process until the average sum of
	 * squared error across the set of inputs is less than the {@code maxError}
	 * attribute. Uses the class field {@code DEFAULT_ALPHA} to learn .
	 *
	 * @param learningData a list of data sets on which to learn, each
	 * containing a list of input values followed immediately by the expected
	 * output value(s) of the neural net
	 */
	public void learn(double[][] learningData)
	{
		this.learn(learningData, DEFAULT_ALPHA);
	}

	/**
	 * Splits the {@code learningData} array into a list of inputs and outputs
	 * and calls the {@link Layer#learn(double alpha) learn} method of the
	 * {@link OutputLayer} and each {@link HiddenLayer} from end to beginning.
	 * The method then iterates the above process until the average sum of
	 * squared error across the set of inputs is less than the {@code maxError}
	 * attribute. Uses the class field {@code DEFAULT_ALPHA} to learn
	 *
	 * @param learningData a list of data sets on which to learn, each
	 * containing a list of input values followed immediately by the expected
	 * output value(s) of the neural net
	 * @param alpha the learning rate for the neural net
	 */
	public void learn(double[][] learningData, double alpha)
	{
		long startTime = System.nanoTime();
		do
		{
			avgSSE = 0;
			for (double[] inputRow : learningData)
			{
				double localError = 0;
				double[] inputSet = Arrays.copyOfRange(inputRow, 0,
													   inputRow.length - outputLayer.getNeurons().size());
				double[] expectedOutputs = Arrays.copyOfRange(inputRow,
															  inputRow.length - outputLayer.getNeurons().size(),
															  inputRow.length);
				double[] outputSet = classify(inputSet);
				for (int i = 0; i < outputSet.length; i++)
				{
					localError += Math.pow((expectedOutputs[i] - outputSet[i]),
										   2);
				}
				outputLayer.setTargetOutput(expectedOutputs);
				outputLayer.learn(alpha);
				for (int i = hiddenLayers.length - 1; i >= 0; i--)
				{
					hiddenLayers[i].learn(alpha);
				}
				avgSSE += localError;
			}
			epochs++;
			epochsProperty.set(epochs);
			avgSSE /= learningData.length;
			avgSSEProperty.set(avgSSE); // Average square error across all data sets
			secondsToTrain += (System.nanoTime() - startTime) / 1.0E9;
			secondsToTrainProperty.set(secondsToTrain);
		} while (avgSSE > maxError && epochs < maxEpochs);
	}

	/**
	 * Splits the {@code learningData} array into a list of inputs and outputs
	 * and calls the {@link Layer#learn(double alpha) learn} method of the
	 * {@link OutputLayer} and each {@link HiddenLayer} from end to beginning.
	 * The method then iterates the above process until the average sum of
	 * squared error across the set of inputs is less than the {@code maxError}
	 * attribute. Uses the class field {@code DEFAULT_ALPHA} to learn
	 *
	 * @param learningData a list of data sets on which to learn, each
	 * containing a list of input values followed immediately by the expected
	 * output value(s) of the neural net
	 * @param alpha the learning rate for the neural net
	 * @param numEpochs the maximum number of epochs to train for this run over
	 * @param forceSSE whether or not the machine should be forced to learn even
	 * with adequate SSE
	 */
	public void learn(double[][] learningData, double alpha, int numEpochs,
					  boolean forceSSE)
	{
		long startTime = System.nanoTime();
		int localEpochs = 0;
		while ((forceSSE || avgSSE > maxError) && localEpochs < numEpochs)
		{
			avgSSE = 0;
			for (double[] inputRow : learningData)
			{
				double localError = 0;
				double[] inputSet = Arrays.copyOfRange(inputRow, 0,
													   inputRow.length - outputLayer.getNeurons().size());
				double[] expectedOutputs = Arrays.copyOfRange(inputRow,
															  inputRow.length - outputLayer.getNeurons().size(),
															  inputRow.length);
				double[] outputSet = classify(inputSet);
				for (int i = 0; i < outputSet.length; i++)
				{
					localError += Math.pow((expectedOutputs[i] - outputSet[i]),
										   2);
				}
				outputLayer.setTargetOutput(expectedOutputs);
				outputLayer.learn(alpha);
				for (int i = hiddenLayers.length - 1; i >= 0; i--)
				{
					hiddenLayers[i].learn(alpha);
				}
				avgSSE += localError;
			}
			localEpochs++;
			epochs++;
			epochsProperty.set(epochs);
			avgSSE /= learningData.length;
			avgSSEProperty.set(avgSSE); // Average square error across all data sets
			secondsToTrain += (System.nanoTime() - startTime) / 1.0E9;
			secondsToTrainProperty.set(secondsToTrain);
		}
	}

	public void learnSingle(double[] inputRow, double alpha)
	{

		double[] inputSet = Arrays.copyOfRange(inputRow, 0,
											   inputRow.length - outputLayer.getNeurons().size());
		double[] expectedOutputs = Arrays.copyOfRange(inputRow,
													  inputRow.length - outputLayer.getNeurons().size(),
													  inputRow.length);
		double[] outputSet = classify(inputSet);
		outputLayer.setTargetOutput(expectedOutputs);
		outputLayer.learn(alpha);
		for (int i = hiddenLayers.length - 1; i >= 0; i--)
		{
			hiddenLayers[i].learn(alpha);
		}
	}

	// Creates edges linking each of the consecutive layers.
	private void connectLayers(Layer[] layers)
	{
		for (int i = 0; i < layers.length - 1; i++)
		{
			layers[i].connectLayer(layers[i + 1], weightAssignment);
		}
	}

	/**
	 * Converts the local fields of an {@link InputLayer}, an
	 * {@link OutputLayer}, and an array of {@link HiddenLayer}s to an array of
	 * generic {@link Layer}s and returns that array.
	 *
	 * @return an array of generic {@link Layer}s starting with an
	 * {@link InputLayer} followed by some number of {@link HiddenLayer}s and
	 * terminated by an {@link OutputLayer}
	 */
	public Layer[] getLayers()
	{
		Layer[] layers = new Layer[2 + hiddenLayers.length];
		layers[0] = inputLayer;
		layers[layers.length - 1] = outputLayer;
		for (int i = 1; i < layers.length - 1; i++)
		{
			layers[i] = hiddenLayers[i - 1];
		}
		return layers;
	}

	/**
	 * Returns the average sum of squared error result from the most recent
	 * training data.
	 *
	 * @return the average sum of squared error result from the most recent
	 * training data
	 */
	public double getAvgSSE()
	{
		return avgSSE;
	}

	/**
	 * Returns the maximum error that the neural net will accept.
	 *
	 * @return the maximum error that the neural net will accept.
	 */
	public double getMaxError()
	{
		return maxError;
	}

	/**
	 * Returns the number of seconds that it took to train.
	 *
	 * @return the number of seconds that it took to train
	 */
	public double getSecondsToTrain()
	{
		return secondsToTrain;
	}

	/**
	 * Returns the number of epochs run in the training function.
	 *
	 * @return the number of epochs run in the training function
	 */
	public int getEpochs()
	{
		return epochs;
	}

	/**
	 * Returns the maximum number of epochs that the net will train for.
	 *
	 * @return the maximum number of epochs that the net will train for
	 */
	public int getMaxEpochs()
	{
		return maxEpochs;
	}

	/**
	 * Gets the {@link ActivationFunction} used by each {@link Neuron} object
	 *
	 * @return The {@link ActivationFunction} used by each {@link Neuron} object
	 */
	public ActivationFunction getActivationFunction()
	{
		return outputLayer.getNeurons().get(0).getActivationFunction();
	}

	/**
	 * Changes the {@link ActivationFunction} of each {@link Neuron} in each
	 * {@link HiddenLayer} and {@link OutputLayer}
	 *
	 * @param actFunc The target {@link ActivationFunction}
	 */
	public void setActivationFunction(ActivationFunction actFunc)
	{
		for (Layer layer : getLayers())
		{
			if (!(layer instanceof InputLayer))
			{
				for (Neuron n : layer.getNeurons())
				{
					n.setActivationFunction(actFunc);
				}
			}
		}
	}

	public void resetInternals()
	{
		this.avgSSE = maxError + 1;
		this.avgSSEProperty.set(0);
		this.epochs = 0;
		this.epochsProperty.set(epochs);
	}

	/**
	 * Sets the maximum error threshold at which the neural net will stop
	 * learning.
	 *
	 * @param maxError the maximum sum of squares error at which the neural net
	 * stops learning.
	 */
	public void setMaxError(double maxError)
	{
		this.maxError = maxError;
		this.maxErrorProperty.set(maxError);
	}

	/**
	 * Sets the weight assignment of the layer to the given
	 * {@link WeightAssignment} object. This method is used to initialize the
	 * {@link WeightAssignment} properties in each {@link Neuron} to the
	 * provided value.
	 *
	 * @param weightAssignment the {@link WeightAssignment} to be used
	 */
	public void setWeightAssignment(WeightAssignment weightAssignment)
	{
		this.weightAssignment = weightAssignment;
	}

	/**
	 * Sets the maximum number of epochs before the machine stops learning.
	 *
	 * @param maxEpochs the maximum number of epochs before the machine stops
	 * learning
	 */
	public void setMaxEpochs(int maxEpochs)
	{
		this.maxEpochs = maxEpochs;
		this.maxEpochsProperty.set(maxEpochs);
	}

	/**
	 * Returns the property associated with the Edge class's momentum value.
	 *
	 * @return the property associated with the Edge class's momentum value
	 */
	public double getMomentumConstantProperty()
	{
		return Edge.getMomentumConstant();
	}

	/**
	 * Sets the momentum constant of the Edge class.
	 *
	 * @param momentumConstant the new momentum constant
	 */
	public void setMomentumConstant(double momentumConstant)
	{
		Edge.setMomentumConstant(momentumConstant);
	}

	/**
	 * Returns the property associated with the neural net's max SSE.
	 *
	 * @return the property associated with the neural net's max SSE
	 */
	public SimpleDoubleProperty getMaxErrorProperty()
	{
		return maxErrorProperty;
	}

	/**
	 * Returns the property associated with the neural net's max epochs to
	 * learn.
	 *
	 * @return the property associated with the neural net's max epochs to
	 * learn.
	 */
	public SimpleIntegerProperty getMaxEpochsProperty()
	{
		return maxEpochsProperty;
	}

	/**
	 * Returns the property associated with the neural net's average SSE.
	 *
	 * @return the property associated with the neural net's average SSE
	 */
	public SimpleDoubleProperty getAvgSSEProperty()
	{
		return avgSSEProperty;
	}

	/**
	 * Returns the property associated with the neural net's time to train.
	 *
	 * @return the property associated with the neural net's time to train
	 */
	public SimpleDoubleProperty getSecondsToTrainProperty()
	{
		return secondsToTrainProperty;
	}

	/**
	 * Returns the property associated with the neural net's epochs run.
	 *
	 * @return the property associated with the neural net's epochs run
	 */
	public SimpleIntegerProperty getEpochsProperty()
	{
		return epochsProperty;
	}

	/**
	 * Restores the values stored in the non-Property fields to the associated
	 * Properties for this object and its children.
	 */
	public void repair()
	{
		maxErrorProperty = new SimpleDoubleProperty(maxError);
		maxEpochsProperty = new SimpleIntegerProperty(maxEpochs);
		avgSSEProperty = new SimpleDoubleProperty(avgSSE);
		secondsToTrainProperty = new SimpleDoubleProperty(secondsToTrain);
		epochsProperty = new SimpleIntegerProperty(epochs);
		for (Layer layer : getLayers())
		{
			layer.repair();
		}
	}
}
