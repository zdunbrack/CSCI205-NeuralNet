/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 24, 2018
* Time: 9:24:37 PM
*
* Project: csci205_hw03
* Package: hw03.model
* File: SimplerIntegerProperty
* Description:
*
* ****************************************
 */
package hw03.model;

import java.io.Serializable;
import javafx.beans.property.SimpleIntegerProperty;

/**
 *
 * @author zachd
 */
public class SimplerIntegerProperty extends SimpleIntegerProperty implements
		Serializable
{

	/**
	 *
	 */
	public SimplerIntegerProperty()
	{
	}

	/**
	 *
	 * @param value
	 */
	public SimplerIntegerProperty(int value)
	{
		super(value);
	}

}
