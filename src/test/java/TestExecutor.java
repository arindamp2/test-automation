package atlas.cmr.automation;

import org.testng.TestNG;

/**
 * This the single executor main class that would be used to run test using
 * a single jar file for the ph-selenium project with an option to pass the
 * params(the xml file) from outside
 */
public class TestExecutor {
	public static void main(String[] params){
		System.out.println("Hello World.");
		TestNG.main(params);
	}
}