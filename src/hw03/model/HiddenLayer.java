/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 10, 2016
 * Time: 10:42:36 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: HiddenLayer
 * Description:
 * An extension of the abstract Layer class that has both leading
 * and trailing edges and is able to propagate error backwards from the
 * next layer.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.util.ArrayList;

/**
 * An extension of the abstract {@link Layer} class that has both leading and
 * trailing {@link Edge} objects and is able to propagate error backwards from
 * the next layer.
 *
 * @author cld028
 */
public class HiddenLayer extends Layer {

	// A list of the weighted sums of the error gradients from the previous layer.
	private double[] outputErrors;

	HiddenLayer(int numNeurons) {
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

	@Override
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

	// Fills in the outputErrors array with the weighted sums of the error gradients from the next layer.
	private void calculateErrors() {
		for (int i = 0; i < neurons.size(); i++) {
			outputErrors[i] = 0;
			for (Edge e : neurons.get(i).getOutEdges()) {
				outputErrors[i] += e.getWeightedErrorGradient();
			}
		}
	}
}
