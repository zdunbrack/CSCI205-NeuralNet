/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 10, 2016
 * Time: 12:55:36 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: InputLayer
 * Description:
 * An extension of the Layer class that has only trailing Edges
 * and is incapable of learning.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03;

import java.util.ArrayList;

/**
 * An extension of the {@link Layer} class that has only trailing {@link Edge}
 * objects and is incapable of learning.
 *
 * @author cld028
 */
public class InputLayer extends Layer {

	InputLayer(int numNeurons) {
		super(numNeurons);
	}

	@Override
	public ArrayList<Neuron> createNeurons(int numNeurons) {
		ArrayList<Neuron> neurons = new ArrayList();
		for (int i = 0; i < numNeurons; i++) {
			neurons.add(new Neuron(new InputActivationFunction()));
		}
		return neurons;
	}

	@Override
	public void connectLayer(Layer nextLayer, WeightAssignment weightAssignment) {
		for (Neuron n : neurons) {
			for (Neuron n2 : nextLayer.getNeurons()) {
				Edge e = new Edge(n, weightAssignment.assignWeight());
				n.addEdgeOut(e);
				n2.addEdgeIn(e);
			}
		}
	}

	/**
	 * Sets the net input values for each of the {@link Neuron} objects in the
	 * layer to the elements of {@code inputVals}.
	 *
	 * @param inputVals a list of input values to be assigned to the neurons
	 * @throws IllegalArgumentException if length of {@code inputVals} array
	 * does not match with number of {@link Neuron} objects in the layer
	 */
	public void fireNeurons(double[] inputVals) {
		if (inputVals.length != neurons.size()) {
			throw new IllegalArgumentException(
				String.format(
					"Number of inputs (%d) does not agree with number of neurons in input layer (%d)",
					inputVals.length, neurons.size()));
		}
		for (int i = 0; i < inputVals.length; i++) {
			neurons.get(i).setNetInput(inputVals[i]);
		}
	}

	/**
	 * Throws an {@link UnsupportedOperationException} since this should not be
	 * learning.
	 *
	 * @throws UnsupportedOperationException if called
	 */
	@Override
	public void learn(double alpha) {
		throw new UnsupportedOperationException(
			"Input layer shouldn't learning!");
	}
}
