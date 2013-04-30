package core;


/**
 * Indicates a test data that was expected but was not present
 * @author Arindam
 */
@SuppressWarnings("serial")
public class TestDataSetupException extends ScriptExecutionException {

	public TestDataSetupException(Exception e) {
		super(e);
	}
	public TestDataSetupException(String s) {
		super(s);
	}
}
