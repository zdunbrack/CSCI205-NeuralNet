/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 8, 2018
* Time: 9:39:08 PM
*
* Project: csci205_hw
* Package: hw02
* File: ActivationFunction
* Description:
* An interface representing an activation function that is applied to the net
* input of a neuron before passing it on to the next layer.
* ****************************************
 */
package hw03.model;

/**
 * An interface representing an activation function that is applied to the net
 * input of a {@link Neuron} before passing it on to the next {@link Neuron}.
 *
 * @author Zach Dunbrack
 */
public interface ActivationFunction {

	/**
	 * Calculates the output of a neuron given the input value.
	 *
	 * @param netInput total input from previous {@link Layer}
	 * @return the result of the activation function applied to the given input
	 */
	public double calcOutput(double netInput);

	/**
	 * Calculates the derivative for activation function.
	 *
	 * @param netInput total input from previous {@link Layer}
	 * @return the derivative of the output function at the given input
	 */
	public double calcDerivOutput(double netInput);
}
