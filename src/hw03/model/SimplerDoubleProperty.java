/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 23, 2018
* Time: 9:53:49 PM
*
* Project: csci205_hw03
* Package: hw03.model
* File: SimplerDoubleProperty
* Description:
*
* ****************************************
 */
package hw03.model;

import java.io.Serializable;
import javafx.beans.property.SimpleDoubleProperty;

/**
 *
 * @author zachd
 */
public class SimplerDoubleProperty extends SimpleDoubleProperty implements
	Serializable
{

	SimplerDoubleProperty()
	{
		super();
	}

	SimplerDoubleProperty(double value)
	{
		super(value);
	}
}
