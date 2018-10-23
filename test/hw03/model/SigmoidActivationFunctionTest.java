/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 10, 2018
* Time: 9:00:54 PM
*
* Project: csci205_hw
* Package: hw01
* File: SigmoidActivationFunctionTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.SigmoidActivationFunction;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class SigmoidActivationFunctionTest {

    private final double EPSILON = 1e-12;
    private SigmoidActivationFunction actFunc;

    public SigmoidActivationFunctionTest() {
    }

    @Before
    public void setUp() {
        actFunc = new SigmoidActivationFunction();
    }

    @After
    public void tearDown() {
        actFunc = null;
    }

    /**
     * Test of calcOutput method, of class SigmoidActivationFunction.
     */
    @Test
    public void testCalcOutput() {
        System.out.println("calcOutput");
        double netInput = 2.0;
        double expResult = 1 / (1 + Math.exp(-2.0));
        double result = actFunc.calcOutput(netInput);
        assertEquals(expResult, result, EPSILON);
    }

    /**
     * Test of calcDerivOutput method, of class SigmoidActivationFunction.
     */
    @Test
    public void testCalcDerivOutput() {
        System.out.println("calcDerivOutput");
        double netInput = 2.0;
        double expResult = (1 / (1 + Math.exp(-2.0))) * (1 - 1 / (1 + Math.exp(
                                                                  -2.0)));
        double result = actFunc.calcDerivOutput(netInput);
        assertEquals(expResult, result, EPSILON);
    }

}
