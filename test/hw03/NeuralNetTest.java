/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 10, 2018
* Time: 9:00:57 PM
*
* Project: csci205_hw
* Package: hw01
* File: NeuralNetTest
* Description:
*
* ****************************************
 */
package hw03;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class NeuralNetTest
{

	NeuralNet oops;
	ArrayList<Layer> layers = new ArrayList<>();

	public NeuralNetTest()
	{
	}

	@Before
	public void setUp()
	{
	}

	@After
	public void tearDown()
	{
	}

	/**
	 * A test of all of the ways that neural net construction can fail
	 */
	@Test
	public void testNeuralNetConstructionException()
	{
		layers = new ArrayList<>();
		layers.add(new InputLayer(0));
		layers.add(new HiddenLayer(0));
		try
		{
			oops = new NeuralNet(layers, null);
			fail("Doesn't fail when not ending in OutputLayer");
		} catch(NeuralNetConstructionException e)
		{

		}

		layers.clear();
		layers.add(new HiddenLayer(0));
		layers.add(new OutputLayer(0));
		try
		{
			oops = new NeuralNet(layers, null);
			fail("Doesn't fail when not starting with InputLayer");
		} catch(NeuralNetConstructionException e)
		{

		}

		layers.clear();
		layers.add(new InputLayer(0));
		try
		{
			oops = new NeuralNet(layers, null);
			fail("Doesn't fail with only one layer");
		} catch(NeuralNetConstructionException e)
		{

		}

		layers = new ArrayList<>();
		layers.add(new InputLayer(0));
		layers.add(new InputLayer(0));
		layers.add(new OutputLayer(0));
		try
		{
			oops = new NeuralNet(layers, null);
			fail("Doesn't fail when ending in OutputLayer");
		} catch(NeuralNetConstructionException e)
		{

		}
	}

	/**
	 * Test the Serializable writing ability of NeuralNet
	 */
	@Test
	public void testSerializable()
	{
		NeuralNet nn = new NeuralNet();
		NeuralNet nn2;
		try
		{
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream("testSerializable.dat"));
			nn = new NeuralNet(Arrays.asList(new Layer[]
			{
				new InputLayer(2), new HiddenLayer(5), new OutputLayer(1)
			}), new RandomWeightAssignment());
			out.writeObject(nn);
			out.flush();
			out.close();
		} catch(IOException e)
		{
			System.out.println(e.getMessage());
			fail("Exception thrown!");
		}

		try
		{
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					"testSerializable.dat"));
			nn2 = (NeuralNet)(in.readObject());
			assertEquals(nn.getLayers()[1].getNeurons().size(),
						 nn2.getLayers()[1].getNeurons().size());
		} catch(IOException e)
		{
			System.out.println(e.getMessage());
			fail("IOException thrown!");
		} catch(ClassNotFoundException e)
		{
			System.out.println(e.getMessage());
			fail("ClassNotFoundException thrown!");
		}
	}
}
