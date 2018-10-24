/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 7, 2016
 * Time: 8:29:57 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: Edge
 * Description:
 * A struct-like class linking neurons in different layers of
 * the neural net.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.io.Serializable;

/**
 * A struct-like class linking {@link Neuron} objects that reside in different
 * {@link Layer} objects within the {@link NeuralNet}.
 *
 * @author cld028
 */
public class Edge implements Serializable
{

	// The last weight of the edge.
	private double prevWeight;
	// The current weight of the edge.
	private SimplerDoubleProperty weight;
	// The edge's current error gradient, representing its difference from a more accurate classification.
	private double errorGradient = 0;
	// The neuron sending data to this edge.
	private Neuron from;
	// The constant that decides how aggressively the edge tries to avoid local minima
	private static double momentumConstant = 0.5;

	Edge(Neuron from)
	{
		this.prevWeight = 0;
		this.weight = new SimplerDoubleProperty(0);
		this.from = from;
	}

	Edge(Neuron from, double weight)
	{
		this.prevWeight = weight;
		this.weight = new SimplerDoubleProperty(weight);
		this.from = from;
	}

	/**
	 * Based on the old state of the edge, returns the weighted error gradient
	 * used to determine the influence of this edge's error gradient on its
	 * incoming {@link Neuron} for back-propagation.
	 *
	 * @return the product of the edge's prior weight and the old error gradient
	 */
	public double getWeightedErrorGradient()
	{
		return prevWeight * errorGradient;
	}

	/**
	 * Returns the weighted result value from the input neuron.
	 *
	 * @return the product of the edge's weight and the previous neuron's
	 * result.
	 */
	public double getWeightedResult()
	{
		return weight.get() * from.getResult();
	}

	/**
	 * Updates the weight of the edge based on its input, its error gradient,
	 * and the learning rate of the neural net. This method also makes sure that
	 * the previous weight value is up to date.
	 *
	 * @param alpha the learning rate of the neural net
	 */
	public void update(double alpha)
	{
		double prevWeightDelta = weight.get() - prevWeight;
		prevWeight = weight.get();
		weight.set(
				weight.get() + alpha * from.getResult() * errorGradient + momentumConstant * prevWeightDelta);
	}

	/**
	 * Returns the error gradient of the edge. This method is used for the
	 * back-propagation process.
	 *
	 * @return the error gradient of the edge.
	 */
	public double getErrorGradient()
	{
		return errorGradient;
	}

	/**
	 * Sets the error gradient of the edge to the specified value.
	 *
	 * @param errorGradient the error gradient to be set
	 */
	public void setErrorGradient(double errorGradient)
	{
		this.errorGradient = errorGradient;
	}

	/**
	 * Returns the current weight of the edge.
	 *
	 * @return the current weight of the edge
	 */
	public double getWeight()
	{
		return weight.get();
	}

	/**
	 * Sets the edge's weight to the specified value.
	 *
	 * @param weight the weight to be set
	 */
	public void setWeight(double weight)
	{
		this.weight.set(weight);
	}

	/**
	 * Returns the property associated with the edge's weight.
	 *
	 * @return the property associated with the edge's weight
	 */
	public SimplerDoubleProperty getWeightProperty()
	{
		return weight;
	}

	public static double getMomentumConstant()
	{
		return momentumConstant;
	}

	public static void setMomentumConstant(double momentumConstant)
	{
		Edge.momentumConstant = momentumConstant;
	}
}
