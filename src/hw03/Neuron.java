/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 7, 2016
 * Time: 6:41:58 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: Neuron
 * Description:
 * A class representing a neuron or node in a neural net.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A class representing a neuron or node in a {@link NeuralNet}. This class
 * links different {@link Layer} objects to each other by creating {@link Edge}
 * objects between them. It also allows the {@link NeuralNet} to advance beyond
 * simple linear combinations through its use of an {@link ActivationFunction}.
 *
 * @author cld028
 */
public class Neuron implements Serializable
{

	// The list of edges entering the neuron.
	private ArrayList<Edge> inEdges = new ArrayList();
	// The list of edges exiting the neuron.
	private ArrayList<Edge> outEdges = new ArrayList();
	// The activation function used to normalize net inputs.
	private ActivationFunction activationFunction;
	// The net input of the weighted sum of the previous neurons' values.
	private double netInput;
	// The threshold value subtracted from netInput before passing it into the activation function.
	private double theta;
	// The default threshold for the neuron.
	private final static double DEFAULT_THETA = -0.5;
	// The default activation function for the neuron.
	private final static ActivationFunction DEFAULT__ACTIVATION_FUNCTION = new SigmoidActivationFunction();

	Neuron()
	{
		this.theta = DEFAULT_THETA;
		this.activationFunction = DEFAULT__ACTIVATION_FUNCTION;
	}

	Neuron(ActivationFunction actFunction)
	{
		this();
		this.activationFunction = actFunction;
	}

	/**
	 * Updates the neuron's net input based on the edges feeding into it.
	 */
	public void fire()
	{
		netInput = 0;
		for (Edge edge : inEdges)
		{
			netInput += edge.getWeightedResult();
		}
	}

	/**
	 * Returns the neuron's derivative output, used to determine the direction
	 * and magnitude of modifications to weights for Edges feeding into this
	 * neuron.
	 *
	 * @return the activation function's derivative evaluated at the difference
	 * between the neuron's net input and its threshold.
	 */
	public double getDerivResult()
	{
		return activationFunction.calcDerivOutput(netInput - theta);
	}

	/**
	 * Adds an edge to the list of edges feeding into the neuron.
	 *
	 * @param e the edge to be added to the list of incoming edges
	 */
	public void addEdgeIn(Edge e)
	{
		inEdges.add(e);
	}

	/**
	 * Updates the neuron's threshold based on the given error and learning
	 * rate. @param error the weighted sum of error gradients of all
	 * {@link Edge}s leaving the neuron
	 *
	 * @param alpha the learning rate of the neural net
	 * @param error the error used to update the threshold
	 */
	public void updateTheta(double error, double alpha)
	{
		theta += alpha * -1 * error;
	}

	/**
	 * Adds an edge to the list of edges coming out of the neuron.
	 *
	 * @param e the edge to be added to the list of outgoing edges
	 */
	public void addEdgeOut(Edge e)
	{
		outEdges.add(e);
	}

	/**
	 * Returns the neuron's output, ready to be passed on to the next neuron or
	 * activation function.
	 *
	 * @return the activation function evaluated at the difference between the
	 * neuron's net input and its threshold.
	 */
	public double getResult()
	{
		return activationFunction.calcOutput(netInput - theta);
	}

	/**
	 * Returns the list of {@link Edge} objects exiting the neuron.
	 *
	 * @return the list of {@link Edge} objects exiting the neuron
	 */
	public ArrayList<Edge> getOutEdges()
	{
		return outEdges;
	}

	/**
	 * Returns the neuron's threshold value.
	 *
	 * @return the neuron's threshold value
	 */
	public double getTheta()
	{
		return theta;
	}

	/**
	 * Sets the neuron's threshold value to the parameter value.
	 *
	 * @param theta the new threshold value
	 */
	public void setTheta(double theta)
	{
		this.theta = theta;
	}

	/**
	 * Returns the list of {@link Edge} objects entering the neuron.
	 *
	 * @return the list of {@link Edge} objects entering the neuron
	 */
	public ArrayList<Edge> getInEdges()
	{
		return inEdges;
	}

	/**
	 * Sets the net input value of the neuron. This method is used solely for
	 * providing inputs to the neurons in an {@link InputLayer}.
	 *
	 * @param netInput the input that the neuron will activate on to provide its
	 * future results
	 */
	public void setNetInput(double netInput)
	{
		this.netInput = netInput;
	}

	/**
	 * Returns the {@link ActivationFunction} used by the neuron.
	 *
	 * @return the {@link ActivationFunction} used by the neuron.
	 */
	public ActivationFunction getActivationFunction()
	{
		return activationFunction;
	}

	/**
	 * Sets the neuron's {@link ActivationFunction} to the specified value.
	 *
	 * @param actFunc the new {@link ActivationFunction} for the neuron to use
	 */
	public void setActivationFunction(ActivationFunction actFunc)
	{
		this.activationFunction = actFunc;
	}
}
