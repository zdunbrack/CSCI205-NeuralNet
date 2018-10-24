/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 9, 2016
 * Time: 7:43:16 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: LeakyReLUActivationFunction
 * Description:
 * A legitimate ActivationFunction that converts any net input to a
 * value in the range (0, infinity).
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import hw03.utility.ActivationFunction;
import java.io.Serializable;

/**
 * A legitimate {@link ActivationFunction} that converts any net input to a
 * value in the range (0, infinity).
 *
 * @author cld028
 */
public class LeakyReLUActivationFunction implements Serializable,
													ActivationFunction
{

	@Override
	public double calcOutput(double netInput)
	{
		return Math.max(netInput, 0);
	}

	@Override
	public double calcDerivOutput(double netInput)
	{
		return (calcOutput(netInput) > 0 ? 1 : 0.2);
	}

	public String toString()
	{
		return "Leaky ReLU";
	}
}
