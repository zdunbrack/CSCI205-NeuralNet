/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 11, 2018
* Time: 2:18:56 PM
*
* Project: csci205_hw
* Package: hw01
* File: ANNClientTest
* Description:
*
* ****************************************
 */
package hw03;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class ANNClientTest
{

	public ANNClientTest()
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
	 * Test of main method, of class ANNClient.
	 *
	 * @throws java.io.UnsupportedEncodingException
	 * @throws java.io.FileNotFoundException
	 */
	@Test
	public void testMain() throws UnsupportedEncodingException, FileNotFoundException
	{
		// A potential series of inputs from a user that would test
		// XOR, AND, classification, training, and writing/reading of files.
		String data = "new 2 1 3 Sigmoid train exampleXOR.csv 0.0001 100000 nnXOR.dat"
					  + " continue classify exampleXOR.csv outputXOR.txt terminate "
					  + "new 2 1 3 Sigmoid train exampleAND.csv 0.0001 100000 nnAND.dat terminate "
					  + "import nnAND.dat classify exampleAND.csv outputAND.txt terminate";
		String[] args = new String[0];
		InputStream testInput = new ByteArrayInputStream(data.getBytes("UTF-8"));
		InputStream old = System.in;
		try
		{
			System.setIn(testInput);

			//Three calls
			//Train and classify XOR
			ANNClient.main(args);

			//Train AND and output to file
			ANNClient.main(args);

			//Import AND from file and classify
			ANNClient.main(args);

			// Test XOR
			Scanner sc = new Scanner(new FileInputStream(new File(
				"outputXOR.txt")));
			assertEquals(Double.parseDouble(sc.nextLine()), 0.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 1.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 1.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 0.0, 0.1);

			// Test AND
			sc = new Scanner(new FileInputStream(new File("outputAND.txt")));
			assertEquals(Double.parseDouble(sc.nextLine()), 0.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 0.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 0.0, 0.1);
			assertEquals(Double.parseDouble(sc.nextLine()), 1.0, 0.1);
		} finally
		{
			System.setIn(old);
		}
	}
}
