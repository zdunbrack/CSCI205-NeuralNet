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
 * File: TanhActivationFunction
 * Description:
 * A legitimate ActivationFunction that converts any net input to a
 * value in the range (-1, 1).
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.io.Serializable;

/**
 * A legitimate {@link ActivationFunction} that converts any net input to a
 * value in the range (-1, 1).
 *
 * @author cld028
 */
public class TanhActivationFunction implements ActivationFunction, Serializable
{

	@Override
	public double calcOutput(double netInput)
	{
		return Math.tanh(netInput);
	}

	@Override
	public double calcDerivOutput(double netInput)
	{
		return 1 - Math.pow(calcOutput(netInput), 2);
	}

	public String toString()
	{
		return "Tanh";
	}
}
