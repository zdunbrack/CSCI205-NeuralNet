/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 10, 2018
* Time: 9:00:53 PM
*
* Project: csci205_hw
* Package: hw01
* File: HiddenLayerTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.Neuron;
import hw03.model.InputActivationFunction;
import hw03.model.HiddenLayer;
import hw03.model.Edge;
import java.util.ArrayList;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class HiddenLayerTest {

	private final static double LARGE_EPSILON = 1e-3;
	private static final int NUM_NEURONS = 3;
	HiddenLayer hiddenLayer;

	public HiddenLayerTest() {

	}

	@Before
	public void setUp() {
		hiddenLayer = new HiddenLayer(NUM_NEURONS);
	}

	@After
	public void tearDown() {
		hiddenLayer = null;
	}

	/**
	 * Test of createNeurons method, of class HiddenLayer.
	 */
	@Test
	public void testCreateNeurons_int() {
		System.out.println("createNeurons");
		ArrayList<Neuron> result = hiddenLayer.createNeurons(NUM_NEURONS);
		assertEquals(result.size(), NUM_NEURONS);
	}

	/**
	 * Test of learn method, of class HiddenLayer
	 */
	@Test
	public void testLearn() {
		System.out.println("learn");
		//Constructing the output layer as detailed in the assignment.
		hiddenLayer = new HiddenLayer(2);

		Neuron outputNeuron = new Neuron();
		outputNeuron.setTheta(0);
		outputNeuron.setNetInput(-0.295);

		Neuron hiddenTop = hiddenLayer.getNeurons().get(0);
		hiddenTop.setTheta(0);
		hiddenTop.setNetInput(-0.2);

		Neuron hiddenBottom = hiddenLayer.getNeurons().get(1);
		hiddenBottom.setTheta(0);
		hiddenBottom.setNetInput(0);

		Neuron inputTop = new Neuron(new InputActivationFunction());
		inputTop.setTheta(0);
		inputTop.setNetInput(1);

		Neuron inputBottom = new Neuron(new InputActivationFunction());
		inputBottom.setTheta(0);
		inputBottom.setNetInput(1);

		Edge out1 = new Edge(hiddenTop, -0.1);
		Edge out2 = new Edge(hiddenBottom, -0.5);
		Edge input11 = new Edge(inputTop, -0.3);
		Edge input12 = new Edge(inputTop, 0.2);
		Edge input21 = new Edge(inputBottom, 0.1);
		Edge input22 = new Edge(inputBottom, -0.2);

		out1.setErrorGradient(0.14);
		out2.setErrorGradient(0.14);

		hiddenTop.addEdgeIn(input11);
		hiddenTop.addEdgeIn(input21);
		hiddenTop.addEdgeOut(out1);

		hiddenBottom.addEdgeIn(input12);
		hiddenBottom.addEdgeIn(input22);
		hiddenBottom.addEdgeOut(out2);//Checking that the output layer's edges and neuron changed as expected

		hiddenLayer.learn(0.2);

		assertEquals(input11.getWeight(), -0.3006, LARGE_EPSILON);
		assertEquals(input12.getWeight(), 0.1964, LARGE_EPSILON);
		assertEquals(input21.getWeight(), 0.0994, LARGE_EPSILON);
		assertEquals(input22.getWeight(), -0.2036, LARGE_EPSILON);
		assertEquals(hiddenTop.getTheta(), 0.0006, LARGE_EPSILON);
		assertEquals(hiddenBottom.getTheta(), 0.0036, LARGE_EPSILON);
	}

}
