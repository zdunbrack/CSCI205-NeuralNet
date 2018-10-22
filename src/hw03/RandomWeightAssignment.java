/* *****************************************
 * CSCI205 - Software Engineering and Design
 * Fall 2016
 *
 * Name: Chris Dancy
 * Date: Oct 9, 2016
 * Time: 3:56:39 PM
 *
 * Project: csci205_hw
 * Package: hw02
 * File: RandomWeightAssignment
 * Description:
 * A WeightAssignment that assigns a random weight in the range (-0.5, 0.5).
 *
 * Updated by Josh and Zach Dunbrack in October 2018.
 * ****************************************
 */
package hw03;

import java.io.Serializable;
import java.util.Random;

/**
 * A {@link WeightAssignment} that assigns a random weight in the range (-0.5,
 * 0.5).
 *
 * @author cld028
 */
public class RandomWeightAssignment implements Serializable, WeightAssignment
{

	/**
	 * Returns a random value in the range (-0.5, 0.5).
	 *
	 * @return a value in the range (-0.5, 0.5).
	 */
	@Override
	public double assignWeight()
	{
		Random rnd = new Random();
		boolean pos = rnd.nextBoolean();
		//Get random double 0 < rndNum < 0.5
		double rndNum = rnd.nextDouble();
		rndNum = rndNum / 2;
		if(pos)
		{
			return (rndNum);
		}
		else
		{
			return (-rndNum);
		}
	}
}
