/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 10, 2016
 * Time: 2:58:06 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: InputActivationFunction
 * Description:
 * A fake ActivationFunction for the input layer that does not modify to
 * the input.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import hw03.utility.ActivationFunction;
import java.io.Serializable;

/**
 * A fake {@link ActivationFunction} for the {@link InputLayer} that does not
 * modify to the input.
 *
 * @author cld028
 */
public class InputActivationFunction implements Serializable, ActivationFunction
{

	@Override
	public double calcOutput(double netInput)
	{
		return netInput;
	}

	@Override
	public double calcDerivOutput(double netInput)
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return "Input";
	}
}
