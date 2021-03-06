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
 * File: SigmoidActivationFunction
 * Description:
 * A legitimate ActivationFunction that converts any net input to a
 * value in the range (0, 1).
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.utility;

import java.io.Serializable;

/**
 * A legitimate {@link ActivationFunction} that converts any net input to a
 * value in the range (0, 1).
 *
 * @author cld028
 */
public class SigmoidActivationFunction implements Serializable,
												  ActivationFunction
{

	@Override
	public double calcOutput(double netInput)
	{
		double outputVal = 1 / (1 + Math.exp(-netInput));
		return outputVal;
	}

	@Override
	public double calcDerivOutput(double netInput)
	{
		return calcOutput(netInput) * (1 - calcOutput(netInput));
	}

	@Override
	public String toString()
	{
		return "Sigmoid";
	}
}
