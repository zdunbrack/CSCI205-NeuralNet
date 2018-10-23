/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 5, 2018
* Time: 9:15:48 AM
*
* Project: csci205_hw
* Package: hw02
* File: WeightAssignment
* Description:
* An interface representing the method for assigning initial weights to a
* newly-constructed neural network.
*
* Updated by Josh and Zach Dunbrack in October 2018.
* ****************************************
 */
package hw03.model;

/**
 * An interface representing the method for assigning initial weights to a
 * newly-constructed neural network.
 *
 * @author Zach Dunbrack
 */
public interface WeightAssignment {

	/**
	 * Uses some algorithm to generate a pseudorandom weight, implicitly
	 * understood to be assigned to an {@link Edge} object upon call.
	 *
	 * @return a pseudorandom value based on an implementation-dependent
	 * algorithm.
	 */
	public double assignWeight();
}
