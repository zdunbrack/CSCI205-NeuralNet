/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 10, 2018
* Time: 9:00:55 PM
*
* Project: csci205_hw
* Package: hw01
* File: OutputLayerTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.Neuron;
import hw03.model.RandomWeightAssignment;
import hw03.model.OutputLayer;
import hw03.model.Edge;
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
public class OutputLayerTest {

	private final static double LARGE_EPSILON = 1e-3;
	OutputLayer outputLayer;
	private static final int NUM_NEURONS = 3;

	public OutputLayerTest() {
	}

	@Before
	public void setUp() {
		outputLayer = new OutputLayer(NUM_NEURONS);
	}

	@After
	public void tearDown() {
		outputLayer = null;
	}

	/**
	 * Test of createNeurons method, of class OutputLayer.
	 */
	@Test
	public void testCreateNeurons_int() {
		System.out.println("createNeurons");
		ArrayList<Neuron> result = outputLayer.createNeurons(NUM_NEURONS);
		assertEquals(result.size(), NUM_NEURONS);
	}

	/**
	 * Test of connectLayer method, of class OutputLayer.
	 */
	@Test
	public void testConnectLayer() {
		System.out.println("testConnectLayer");
		try {
			outputLayer.connectLayer(new OutputLayer(NUM_NEURONS),
									 new RandomWeightAssignment());
			fail("Fails to throw UnsupportedOperationException!");
		} catch (UnsupportedOperationException e) {
		}
	}

	/**
	 * Test of learn method, of class OutputLayer, using the three-layer network
	 * calculations from the assignment.
	 */
	@Test
	public void testLearn() {
		System.out.println("learn");

		//Constructing the output layer as detailed in the assignment.
		outputLayer = new OutputLayer(1);
		outputLayer.setTargetOutput(new double[]{
			1.0
		});

		Neuron outputNeuron = outputLayer.getNeurons().get(0);
		outputNeuron.setTheta(0);
		outputNeuron.setNetInput(-0.295);

		Neuron fromTop = new Neuron();
		fromTop.setTheta(0);
		fromTop.setNetInput(-0.2);

		Neuron fromBottom = new Neuron();
		fromBottom.setTheta(0);
		fromBottom.setNetInput(0);

		Edge top = new Edge(fromTop, -0.1);
		Edge bottom = new Edge(fromBottom, -0.5);
		outputNeuron.addEdgeIn(top);
		outputNeuron.addEdgeIn(bottom);

		//Running the initial step of back-propogation
		outputLayer.learn(0.2);

		//Checking that the output layer's edges and neuron changed as expected
		assertEquals(top.getWeight(), -0.087, LARGE_EPSILON);
		assertEquals(bottom.getWeight(), -0.486, LARGE_EPSILON);
		assertEquals(outputNeuron.getTheta(), -0.028, LARGE_EPSILON);
		assertEquals(outputNeuron.getInEdges().get(0).getErrorGradient(), 0.14,
					 LARGE_EPSILON);
	}
}
