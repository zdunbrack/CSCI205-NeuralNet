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
* File: InputActivationFunctionTest
* Description:
*
* ****************************************
 */
package hw03.model;

import hw03.model.InputActivationFunction;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Joshua Dunbrack
 */
public class InputActivationFunctionTest {

    private InputActivationFunction actFunc;

    public InputActivationFunctionTest() {
    }

    @Before
    public void setUp() {
        actFunc = new InputActivationFunction();
    }

    @After
    public void tearDown() {
        actFunc = null;
    }

    /**
     * Test of calcOutput method, of class InputActivationFunction.
     */
    @Test
    public void testCalcOutput() {
        System.out.println("calcOutput");
        double netInput = 2.0;
        double expResult = 2.0;
        double result = actFunc.calcOutput(netInput);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of calcDerivOutput method, of class InputActivationFunction.
     */
    @Test
    public void testCalcDerivOutput() {
        System.out.println("calcDerivOutput");
        double netInput = 2.0;
        double expResult = 0.0;
        double result = actFunc.calcDerivOutput(netInput);
        assertEquals(expResult, result, 0.0);
    }

}
