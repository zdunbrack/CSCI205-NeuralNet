/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 7, 2016
 * Time: 10:09:12 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: Layer
 * Description:
 * An abstract class used to represent the idea of a layer in the neural net.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * An abstract class used to represent the idea of a layer in the neural net.
 * This class acts as a framework for the
 * {@link InputLayer}, {@link OutputLayer}, and {@link HiddenLayer} classes.
 *
 * @author cld028
 */
public abstract class Layer implements Serializable
{

	/**
	 * The list of {@link Neuron} objects in the layer.
	 */
	protected ArrayList<Neuron> neurons;

	/**
	 * The method of assigning weights to all {@link Edge}s entering
	 * {@link Neuron} in this layer.
	 */
	protected WeightAssignment weightAssignment = new RandomWeightAssignment();

	Layer(int numNeurons)
	{
		this.neurons = this.createNeurons(numNeurons);
	}

	/**
	 * Creates {@link Neuron} objects that will reside in layer.
	 *
	 * @param numNeurons total number of {@link Neuron} objects to be created
	 * within layer
	 * @return an {@link ArrayList} of all newly created {@link Neuron} objects
	 */
	public abstract ArrayList<Neuron> createNeurons(int numNeurons);

	/**
	 * Calls the {@link Neuron#fire() fire} method for each {@link Neuron} in
	 * the layer.
	 */
	public void fireNeurons()
	{
		for (Neuron n : neurons)
		{
			n.fire();
		}
	}

	/**
	 * Calculates the effective error for each {@link Neuron} in the layer and
	 * uses that to adjust the weights of all {@link Edge} objects leading into
	 * the layer's {@link Neuron} objects.
	 *
	 * @param alpha the learning rate for this process
	 */
	public abstract void learn(double alpha);

	/**
	 * Connects the current layer to another layer (with this layer being on the
	 * left).
	 *
	 * @param nextLayer the right layer to which to connect
	 * @param weightAssignment the method for assigning weights to the
	 * {@link Edge} objects connecting the two layers
	 */
	public abstract void connectLayer(Layer nextLayer,
									  WeightAssignment weightAssignment);

	/**
	 *
	 * @return
	 */
	public ArrayList<Neuron> getNeurons()
	{
		return neurons;
	}

	/**
	 * Restores the values stored in the non-Property fields to the associated
	 * Properties for this object and its children.
	 */
	public void repair()
	{
		for (Neuron neuron : getNeurons())
		{
			neuron.repair();
		}
	}
}
