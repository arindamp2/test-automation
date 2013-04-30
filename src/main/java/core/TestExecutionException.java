package atlas.cmr.automation.core;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
/**
 * Un-Checked Exception, displays essential details
 * tester- who executed the test
 * testCaseName
 * internalMethodName
 * Should be thrown by all UTC
 * @author Arindam
 *
 */
public class TestExecutionException extends RuntimeException {
	private String tester;
	private String internalMethodName;
	private String testCaseName;

	public TestExecutionException(String tester, String internalMethodName,
								  String testCaseName, String message){
		super(message);
		this.tester = tester;
		this.internalMethodName = internalMethodName;
		this.testCaseName = testCaseName;
	}

	public TestExecutionException(String message){
		super(message);
	}

	public TestExecutionException(Exception e) {
		super(e);
	}

	public String getMessage()
	{
		StringBuffer buffer = new StringBuffer();
		if(!StringUtils.isEmpty(tester))
			buffer.append("Tested By : " + tester);
		if(!StringUtils.isEmpty(testCaseName))
			buffer.append("Called From Test: " + testCaseName);
		if(!StringUtils.isEmpty(internalMethodName))
			buffer.append("Internal Method Name: " + internalMethodName);
		if(!StringUtils.isEmpty(super.getMessage()))
			buffer.append("Message: " + super.getMessage());
		return buffer.toString();
	}


}
