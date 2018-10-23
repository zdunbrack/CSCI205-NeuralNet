/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 9, 2016
 * Time: 6:31:37 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: OutputLayer
 * Description:
 * An extension of the Layer class that has only leading edges
 * and is incapable of connecting to another layer.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.util.ArrayList;

/**
 * An extension of the {@link Layer} class that has only leading {@link Edge}
 * objects and is incapable of connecting to another {@link Layer}.
 *
 * @author cld028
 */
public class OutputLayer extends Layer {

	// The list of outputs that the neurons in the layer should have produced.
	private double[] targetOutput;
	// The list of differences between the target outputs and the actual outputs.
	private double[] outputErrors;

	OutputLayer(int numNeurons) {
		super(numNeurons);
		outputErrors = new double[neurons.size()];
	}

	@Override
	public ArrayList<Neuron> createNeurons(int numNeurons) {
		ArrayList<Neuron> neurons = new ArrayList();
		for (int i = 0; i < numNeurons; i++) {
			neurons.add(new Neuron());
		}
		return neurons;
	}

	/**
	 * Throws an {@link UnsupportedOperationException} since this should not be
	 * connecting to another {@link Layer}.
	 *
	 * @throws UnsupportedOperationException if called
	 */
	@Override
	public void connectLayer(Layer nextLayer, WeightAssignment weightAssignment) {
		throw new UnsupportedOperationException(
			"Output layer shouldn't be connecting!");
	}

	public void learn(double alpha) {
		calculateErrors();
		for (int i = 0; i < neurons.size(); i++) {
			for (Edge e : neurons.get(i).getInEdges()) {
				e.setErrorGradient(
					neurons.get(i).getDerivResult() * outputErrors[i]);
				e.update(alpha);
			}
			neurons.get(i).updateTheta(
				neurons.get(i).getDerivResult() * outputErrors[i],
				alpha);
		}
	}

	// Populates the outputErrors array based on the given target outputs.
	private void calculateErrors() {
		for (int i = 0; i < targetOutput.length; i++) {
			outputErrors[i] = targetOutput[i] - neurons.get(i).getResult();
		}
	}

	/**
	 * Defines the list of target outputs that the layer is trying to match.
	 *
	 * @param targetOutput the list of expected outputs from the given
	 * classification.
	 */
	public void setTargetOutput(double[] targetOutput) {
		this.targetOutput = targetOutput;
	}

	/**
	 * Creates and returns an array representing the results of each of the
	 * output {@link Neuron} objects in the layer.
	 *
	 * @return an array of doubles where a given entry is the result of the
	 * {@link Neuron} in the output layer at the same index
	 */
	public double[] getNetResults() {
		double[] results = new double[neurons.size()];
		for (int i = 0; i < results.length; i++) {
			results[i] = neurons.get(i).getResult();
		}
		return results;
	}
}
