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
* File: RandomWeightAssignmentTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.RandomWeightAssignment;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertNotEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class RandomWeightAssignmentTest {

    RandomWeightAssignment weightAssignment;

    public RandomWeightAssignmentTest() {
        weightAssignment = new RandomWeightAssignment();
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of assignWeight method, of class RandomWeightAssignment.
     */
    @Test
    public void testAssignWeight() {
        System.out.println("assignWeight");
        double notExpResult = 0.0;
        double result = weightAssignment.assignWeight();
        assertNotEquals(result, notExpResult, 0.0);
    }
}
