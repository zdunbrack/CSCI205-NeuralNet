/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 10, 2018
* Time: 9:00:56 PM
*
* Project: csci205_hw
* Package: hw01
* File: InputLayerTest
* Description:
*
* ****************************************
 */
package hw03.model;

import java.util.ArrayList;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class InputLayerTest
{

	InputLayer inputLayer;
	private static final int NUM_NEURONS = 3;

	public InputLayerTest()
	{
	}

	@Before
	public void setUp()
	{
		inputLayer = new InputLayer(NUM_NEURONS);
	}

	@After
	public void tearDown()
	{
		inputLayer = null;
	}

	/**
	 * Test of createNeurons method, of class InputLayer.
	 */
	@Test
	public void testCreateNeurons_int()
	{
		System.out.println("createNeurons");
		ArrayList<Neuron> result = inputLayer.createNeurons(NUM_NEURONS);
		assertEquals(result.size(), NUM_NEURONS);
	}

	/**
	 * Test of fireNeurons method, of class InputLayer.
	 */
	@Test
	public void testFireNeurons()
	{
		System.out.println("fireNeurons");
		double[] inputVals = new double[NUM_NEURONS];
		for (int i = 0; i < NUM_NEURONS; i++)
		{
			inputVals[i] = i + 1;
		}
		inputLayer.fireNeurons(inputVals);
		for (int i = 0; i < NUM_NEURONS; i++)
		{
			inputLayer.getNeurons().get(i).setTheta(0);
			assertEquals(inputLayer.getNeurons().get(i).getResult(),
						 inputVals[i], 0.0);
		}
	}

	/**
	 * Test of learn method, of class InputLayer.
	 */
	@Test
	public void testLearn()
	{
		System.out.println("learn");
		try
		{
			inputLayer.learn(0.2);
			fail("inputLayer failed to throw an exception when learning!");
		} catch (UnsupportedOperationException e)
		{
			//yey
		}
	}
}
