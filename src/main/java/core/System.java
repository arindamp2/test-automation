package core;

import jxl.Sheet;
import jxl.Workbook;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Properties;


/**
 * Singleton that handles the following
 * <li>Loading Test Data</li>
 * <li>Loading Configuration Data</li>
 *
 * @author Arindam
 *
 */
public final class System {

	private static String propertiesFileName = "/cmr_selenium_env.properties";
	private static String browser;
	private static System instance = null;
	private static Properties systemProperties = null;
	private static HashMap<String, String> configData = new HashMap<String, String>();
	private static HashMap<String, String> testData = new HashMap<String, String>();
	private static HashMap<String, String> xPath = new HashMap<String, String>();

	private static HashMap<String, String> listOfXPaths = new HashMap<String, String>();
	static Logger logger = Logger.getLogger(System.class);

	private System(String testDataFile, String testDataTab, String xPathDataTab) throws Exception{
		systemProperties = new Properties();
		systemProperties.load(this.getClass().getResourceAsStream(propertiesFileName));
		logger.info("Loaded properties file " + propertiesFileName);
		configData = readExcelData(configData,
			systemProperties.getProperty("configFileName"),
			systemProperties.getProperty("configTabName"));
		logger.info("Config data reading over from " + systemProperties.getProperty("configFileName"));
		testData = readExcelData(testData,
			systemProperties.getProperty("testFileName"),
			systemProperties.getProperty("testTabName"));

		logger.info("Test data reading over from " + systemProperties.getProperty("testFileName"));
		listOfXPaths = readExcelData(testData,
			systemProperties.getProperty("listOfXPathsFileName"),
			systemProperties.getProperty("listOfXPathsTabName"));
		logger.info("Test data reading over from " + systemProperties.getProperty("testFileName"));
		// try reading data from the data sheet passed by user, if it is found it will only append
		//(or overwrite if keys match) to system data
		if(testDataFile != null){
			try{
				testData = readExcelData(testData, testDataFile, testDataTab);
				testData = readExcelData(testData, testDataFile, xPathDataTab);
			}
			catch(Exception e){
				logger.fatal("Could not read test data file. Data file name provided " + testDataFile + ", Data tab name provided " + testDataTab, e);
			}
		}

		browser = getConfigData("Browser");
		logger.info("Browser set to " + browser);
	}

	public static void setTestData(HashMap<String, String> injectedTestData){
		testData = injectedTestData;
	}

	/**
	 * Initializes all system and environment objects
	 *
	 * @param testDataFile - additional test data file
	 * @param testDataTab - data tab name in test data file
	 * @param xPathDataTab - Data Tab Name which contains XPaths
	 * @return
	 */
	public static System getInstance(String testDataFile, String testDataTab, String xPathDataTab) {
		try {
			return instance == null ? new System(testDataFile, testDataTab, xPathDataTab) : instance;
		} catch (Exception e) {
			logger.fatal("System Initialization Failed", e);
			return null;
		}
	}

	public static System getInstance(String testDataFile, String testDataTab) {
		try {
			java.lang.System.out.println("Inside 2 param getInstance");
			return instance == null ? new System(testDataFile, testDataTab, null) : instance;
		} catch (Exception e) {
			logger.fatal("System Initialization Failed", e);
			return null;
		}
	}

	/**
	 * Initializes all system and environment objects
	 * @return
	 */
	public static System getInstance() {
		try {
			return instance == null ? new System(null, null, null) : instance;
		} catch (Exception e) {
			logger.fatal("System Initialization Failed", e);
			return null;
		}
	}


	private HashMap<String, String> readExcelData(HashMap<String, String> map, String fileName, String tabName)
		throws Exception{
//		logger.debug("reading " + fileName);
//		logger.debug("reading " + tabName);
		Workbook configWorkBook = Workbook.getWorkbook(this.getClass().getResourceAsStream(fileName));
//		logger.debug("configWorkBook " + configWorkBook);
		Sheet configDataSheet = configWorkBook.getSheet(tabName);
//		logger.debug("reading " + fileName);
//		logger.debug("found " + configDataSheet.getRows() + "rows in " + fileName);
		for(int row=1; row<configDataSheet.getRows(); row++){
			String key = configDataSheet.getCell(0, row).getContents();
			String value = configDataSheet.getCell(1, row).getContents();
//			logger.debug("processing row " + row + " in " + fileName + " - found key " + key + " value " + value);
			map.put(key, value);
		}
		return map;
	}


	/**
	 * Reads and returns the appropriate class name based on browser
	 * @param key
	 * @return
	 */
	protected static String getClassName(String key){
		if(browser.equalsIgnoreCase("IE"))
			return systemProperties.getProperty("IE." + key);
		else if(browser.equalsIgnoreCase("FF"))
			return systemProperties.getProperty("FF." + key);
		else {
			logger.error("Class for key <" + key + "> not found. Is it defined in " + propertiesFileName);
			throw new ScriptExecutionException("Class for key <" + key + "> not found. Is it defined in " + propertiesFileName);
		}
	}

	/**
	 * @param key find the value of the configuration data represented by key
	 * @return value of the key if found
	 * @throws TestDataSetupException if the passed key is not found
	 */
	public static String getConfigData(String key){
		if(!configData.containsKey(key))
			throw new TestDataSetupException("The key <" + key + "> is not available in config data");
//		logger.info("Call to getConfigData with key " + key + "returned " + testData.get(key));
		return configData.get(key);
	}

	/**
	 * @param key find the value of the test data represented by key
	 * @return value of the key if found
	 * @throws TestDataSetupException if the passed key is not found
	 */
	public static String getTestData(String key){
		if(!testData.containsKey(key))
			throw new TestDataSetupException("The key <" + key + "> is not available in test data");
		logger.info("Call to getTestData with key " + key + " returned " + testData.get(key));
		return testData.get(key);
	}


	public static String getXPath(String key){
		if(!xPath.containsKey(key))
			throw new TestDataSetupException("The key <" + key + "> is not available in test data");
		logger.info("Call to getTestData with key " + key + "returned " + xPath.get(key));
		return xPath.get(key);
	}




	/**
	 * @param key find the value of the test data represented by key
	 * @return value of the key if found
	 * @throws TestDataSetupException if the passed key is not found
	 */
	public static String getListOfXPaths(String key){
		if(!listOfXPaths.containsKey(key))
			throw new TestDataSetupException("The key <" + key + "> is not available in test data");
		logger.info("Call to getListOfXPaths with key " + key + "returned " + listOfXPaths.get(key));
		return listOfXPaths.get(key);
	}

	/**
	 * The name of the browser as read from configuration
	 * @return The browser's name
	 */
	public static String getBrowser(){
		return browser;
	}
}
