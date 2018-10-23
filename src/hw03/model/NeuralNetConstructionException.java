/* ****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 10, 2016
 * Time: 11:30:37 AM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: NeuralNetConstructionException
 * Description:
 * An Exception that occurs when a neural net is constructed improperly.
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03.model;

import java.io.Serializable;

/**
 * An {@link Exception} that occurs when a neural net is constructed improperly.
 *
 * @author cld028
 */
public class NeuralNetConstructionException extends RuntimeException implements
		Serializable
{

	/**
	 * Constructs a new exception with the specified detail message.
	 *
	 * @param message the detail message
	 */
	public NeuralNetConstructionException(String message)
	{
		super(message);
	}
}
