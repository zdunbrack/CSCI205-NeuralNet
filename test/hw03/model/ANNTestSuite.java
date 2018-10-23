/* *****************************************
* CSCI205 - Software Engineering and Design
* Fall 2018
*
* Name: Josh Dunbrack, Zach Dunbrack
* Date: Oct 11, 2018
* Time: 8:07:46 PM
*
* Project: csci205_hw
* Package: hw01
* File: ANNTestSuite
* Description:
*
* ****************************************
 */
package hw03.model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author rsf
 */
@RunWith(Suite.class)
@Suite.SuiteClasses(
{
	SigmoidActivationFunctionTest.class, HiddenLayerTest.class, InputActivationFunctionTest.class, NeuronTest.class, RandomWeightAssignmentTest.class, InputLayerTest.class, OutputLayerTest.class, ANNClientTest.class, NeuralNetTest.class
})
public class ANNTestSuite
{

}
