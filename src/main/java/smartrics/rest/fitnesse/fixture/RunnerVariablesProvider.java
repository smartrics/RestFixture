/**
 * 
 */
package smartrics.rest.fitnesse.fixture;

import smartrics.rest.fitnesse.fixture.support.Variables;

/**
 * The fixture provides the variables of the runner.
 * This interface abstracts the fixture so that it can
 * be tested easily.
 */
public interface RunnerVariablesProvider {

	/**
	 * Get a variable store linked to the current runner environment.
	 */
	Variables createRunnerVariables();

}
