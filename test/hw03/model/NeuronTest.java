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
* File: NeuronTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.Neuron;
import hw03.model.InputActivationFunction;
import hw03.model.Edge;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class NeuronTest {

	Neuron neuron;

	public NeuronTest() {
	}

	@Before
	public void setUp() {
		neuron = new Neuron();
		neuron.setTheta(0);
	}

	@After
	public void tearDown() {
	}

	/**
	 * Test of fire method, of class Neuron.
	 */
	@Test
	public void testFire() {
		System.out.println("fire");
		Neuron input1 = new Neuron(new InputActivationFunction());
		input1.setTheta(0);
		input1.setNetInput(1);
		Neuron input2 = new Neuron(new InputActivationFunction());
		input2.setTheta(0);
		input2.setNetInput(1);
		Edge e1 = new Edge(input1, -0.3);
		Edge e2 = new Edge(input2, 0.1);
		neuron.addEdgeIn(e1);
		neuron.addEdgeIn(e2);

		neuron.fire();

		assertEquals(neuron.getResult(), 0.45, 0.01);
	}
}
