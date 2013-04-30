package core;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;


/**
 * This class serves as the basis of Finding the browser specific functional
 * unit implementation for all automation functionality
 * 
 * @author Arindam
 * 
 */
public class Environment {
	static Logger logger = Logger.getLogger(Environment.class);
	public WebDriver driver = null;

	public Environment() {
		if (System.getBrowser().equalsIgnoreCase("IE")){
			// To run the test in Grid enable the following line
//			String hubUrl = "http://10.10.70.116:4444/wd/hub";
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			// Enable the following try and catch in order to run the test in Grid
//			try{
//				driver = new RemoteWebDriver(new URL(hubUrl), ieCapabilities);
//				logger.debug("Remote Web driver is now set!!" + driver);
//			}catch (MalformedURLException uriEx){
//			}
			// Disable the following line to open browser in Grid
			driver = new InternetExplorerDriver(ieCapabilities);
		}
		else if (System.getBrowser().equalsIgnoreCase("FF")){
			FirefoxProfile fp = new FirefoxProfile();
			fp.setEnableNativeEvents(true);
			fp.setPreference("prompts.tab_modal.enabled", false);
			fp.setPreference("dom.successive_dialog_time_limit", 0);
			driver = new FirefoxDriver(fp);
		}
		else
			throw new ScriptExecutionException("Un-Supported browser <"
				+ System.getBrowser() + "> requested.");
	}

	private Constructor<?> constructorFactory(String className,
			Class<?>... classes) throws ClassNotFoundException,
			SecurityException, NoSuchMethodException {
		Class<?> cls = Class.forName(className);
		return cls.getConstructor((Class<?>[]) classes);
	}

	private Object classFactory(Constructor<?> constructor,
			Object... initParams) throws IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		return constructor.newInstance(initParams);
	}
	

}
