package helpers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.thoughtworks.selenium.Selenium;
import core.ScriptExecutionException;
import core.System;
import core.TestDataSetupException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * The Util class can used to call several small methods
 * waitForNewWindowToAppear to wait for new windows
 * closeDialog to deal with accepting OK/Cancel type of message in new window
 * checkInputDataAndEnterTextBox to enter data in Text Box
 * logOut to Log Out from Labworks
 * accessLWMenu to access the level 2 menu under LW Menu bar and click on that
 * checkInputDataAndSelectGridOption to select one option from a drop down
 * @author Arindam
 */
public class Utils {
	static Logger logger = Logger.getLogger(Utils.class);
	
	public static void waitForNewWindowToAppear(WebDriver driver) {
		int currentWindows = driver.getWindowHandles().size();
		if (currentWindows == 2)
			return;
		while (true) {
			if (driver.getWindowHandles().size() == 2
					|| driver.getWindowHandles().size() > currentWindows)
				break;
		}
	}

	public static void pauseFewSeconds() {
		try {
			Thread.sleep(3000L);
		} catch (InterruptedException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}
	}

	public static void pressCtrlPlusAThenDelete(WebDriver driver,
			WebElement element) {
		element.sendKeys(Keys.chord(Keys.CONTROL, "a"));
		driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
		element.sendKeys(Keys.DELETE);
	}

	public static String doLoginCMR(WebDriver driver, String baseUrl, String userName,
									String password) throws ScriptExecutionException {
		try {
			Selenium selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
			driver.get(baseUrl);
			selenium.waitForPageToLoad("56000");
			try{
				driver.switchTo().defaultContent();
				driver.navigate().to("javascript:document.getElementById('overridelink').click()");
				selenium.waitForPageToLoad("56000");
				java.lang.System.out.println("******* Executed JS **********");
			} catch (Exception ex) {
				logger.fatal("JS could not be executed", ex);
				throw new ScriptExecutionException(ex);
			}
			WebElement userNameField = driver.findElement(By.id("txtUsername"));
			WebElement passwordField = driver.findElement(By.id("txtPassword"));
			userNameField.sendKeys(userName);
			passwordField.sendKeys(password);
			Thread.sleep(500L);
			driver.findElement(By.id("btnLogin")).click();
			selenium.waitForPageToLoad("56000");
			String pageTitle = driver.getTitle();
			return pageTitle;
		} catch (Exception ex) {
			logger.fatal("IEDictionary.doLogin threw error", ex);
			throw new ScriptExecutionException(ex);
		}
	}

	/**
	 *
	 * @param driver
	 * @param currentWindows : Size of current window handles
	 */
	public static void waitForNewWindowsToAppear(WebDriver driver, int currentWindows){
		while(true){
			if(driver.getWindowHandles().size()>currentWindows)
				break;
		}
	}

	/**
	 *
	 * @param driver
	 * @param xpathOfCloseButton  pass x-path of the close button with in the new window
	 * @param verificationURL pass url of the newly appeared window
	 * @return boolean value
	 */
	public static boolean closeDialog(WebDriver driver, 
			String xpathOfCloseButton, String verificationURL) {
		WebDriver popup;
		for (String winHandle : driver.getWindowHandles()) {
			popup = driver.switchTo().window(winHandle);
			if (popup.getCurrentUrl().equalsIgnoreCase(verificationURL)) {
				java.lang.System.out.println("*** Inside Pop Closer ***");

//                driver.findElement(By.xpath(xpathOfCloseButton)).click();
				try {
					Thread.sleep(1500L);
					driver.findElement(By.xpath(xpathOfCloseButton)).click();
					return true;
				} catch (Exception ex) {
					logger.fatal(ex);
					return false;
				}
			}
		}
		return false;
	}

	/**
	 *
	 * @param driver
	 * @param parentIFrame ,the name of the root frame
	 * @param firstIframe, one child frame of the root frame
	 * @param secondIFrame, one child frame of the root frame
	 * @param mainMenu, x-path of the Main Menu
	 * @param subMenu , x-path of the sub menu
	 * Use this method to access LW Menu and click on sub menu
	 */
	public static void accessLWMenu(WebDriver driver, String parentIFrame,
									String firstIframe, String secondIFrame, String mainMenu, String subMenu){
		driver.switchTo().defaultContent();
		driver.switchTo().frame(parentIFrame);
		driver.switchTo().frame(firstIframe);
		driver.findElement(By.id(mainMenu)).click();
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		driver.switchTo().defaultContent();
		driver.switchTo().frame(parentIFrame);
		driver.switchTo().frame(secondIFrame);
		driver.findElement(By.id(subMenu)).click();

	}

	/**
	 * Use this method to land on a particular window
	 * @param driver
	 * @param windowUrl The url of the desired window where you would like to land
	 */
	public static boolean switchToRightWindow(WebDriver driver, String windowUrl){
		WebDriver popup;
		boolean switched = false;
		for (String winHandle : driver.getWindowHandles()) {
			java.lang.System.out.println(winHandle);
			popup = driver.switchTo().window(winHandle);
			if (popup.getCurrentUrl().equalsIgnoreCase(windowUrl)) {
				java.lang.System.out.println("*** Inside Pop Closer ***");
				switched = true;
				break;
			}
		}
		
		return switched;
	}

	/**
	 * This method can be used to click on input box
	 * Clear the content and then type the text
	 * @param fieldName  The value we need to write
	 * @param selector   The place where we need to write
	 * @param driver
	 * @param isMandatory  Pass if the field is mandatory or not
	 */
	public static void checkInputDataAndEnterTextBox(String fieldName, String selector, WebDriver driver, boolean isMandatory){
		try{
			String data = System.getTestData(fieldName);
			driver.findElement(By.xpath(selector)).clear();
			driver.findElement(By.xpath(selector)).sendKeys(data);
		} catch (TestDataSetupException tEx){
			if(isMandatory)
				throw new TestDataSetupException("Mandatory field <" + fieldName +"> not provided in test data.");
		} catch(Exception ex){}
	}

	/**
	 * This method can be used to generate 9 digit random number
	 * @return  the 9 digit string by converting int to string
	 */
	public static String getNineDigitUniqueNumber(){
		Random random = new Random();
		int tempRandomNum = random.nextInt(123456783) + 111111111;
		String pSSN = Integer.toString(tempRandomNum);
		return pSSN;
	}

	/**
	 * This method would generate a random number between any two provided values
	 * @param low
	 * @param high
	 * @return
	 */
	public static int randomNumberBetweenTwoValues(int low, int high){

		Random random = new Random();
		int randomNumber = random.nextInt(high - low) + low;
		return randomNumber;
	}



	/**
	 *
	 * @param fieldName  The value we need to select
	 * @param selector   The place where we need to select
	 * @param driver     pass the default driver
	 * @param isMandatory Pass if the field is mandatory or not
	 */
	public static void checkInputDataAndSelectOption(String fieldName, String selector, WebDriver driver, boolean isMandatory){
		try{
			String data = System.getTestData(fieldName);
			WebElement element = driver.findElement(By.xpath(selector)); 
			Select dropDown = new Select(element);
			dropDown.selectByVisibleText(data);
		} catch (TestDataSetupException tEx){
			if(isMandatory)
				throw new TestDataSetupException("Mandatory field <" + fieldName +"> not provided in test data.");
		}
	}

	/**
	 *
	 * @param fieldName The value we need to select from a grid
	 * @param gridClick  The place where we need to click
	 * @param driver     pass the default driver
	 * @param isMandatory Pass if the field is mandatory or not
	 */
	public static void checkInputDataAndSelectGridOption(String fieldName, String gridClick,
			WebDriver driver, boolean isMandatory){
		WebElement element = null;
		try{
			String data = System.getTestData(fieldName);
			element = driver.findElement(By.xpath(gridClick));
			element.click();
			Thread.sleep(1000L);
			driver.findElement(By.xpath("//input[@value='" + data +"']")).click();
		} catch (TestDataSetupException tEx){
			if(isMandatory)
				throw new TestDataSetupException("Mandatory field <" + fieldName +"> not provided in test data.");
		} catch (InterruptedException e) {
			throw new ScriptExecutionException(e);
		} catch (NoSuchElementException noEx){
			throw new TestDataSetupException(noEx.getMessage() + " was trying to enter <" + fieldName + ">.");
		}
	}
	
	/**
	 * This method can be used to select a text from drop down using JQuery
	 * Visible Text would be used to select a value.
	 * @param driver
	 * @param dropDownID
	 * @param visibleText
	 * @return String : Returns the text which has been selected by JQuery
	 */
	public static String selectAValueInDropDownByVisibleText(WebDriver driver, String dropDownID, String visibleText) {

		// This null object would contain the Selected text returned by javascript executor
		Object selectedText = null;
		try{
			// Get the Jquery loaded in your application
			// jquery.min.js is stored under src/test/resources
			URL jqueryUrl = Resources.getResource("jquery.min.js");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String jqueryText = Resources.toString(jqueryUrl, Charsets.UTF_8);
			js.executeScript(jqueryText);
			// Build your Jquery String to Select a given text from the drop down
			/* The JQuery String Would Look like
			 $("#cboDistrict").find("option:contains('district_selenium_0001')").each(function(){
  				if( $(this).text() == 'district_selenium_0001') {
					$(this).attr("selected","selected");} });
			  */
			String jQueryString = "";
			jQueryString += "$("+"\""+"#"+dropDownID+"\").find"+"("+"\""+"option:contains("+"\'"+visibleText+"\')\""+").each(function(){ if( $(this).text() == "+"\'" +visibleText+"\') { $(this).attr("+"\"selected"+"\","+"\"selected"+"\");} });";

			java.lang.System.out.println("Final JQuery String :");
			java.lang.System.out.println(jQueryString);
			js = (JavascriptExecutor) driver;
			// Run the Jquery
			js.executeScript(jQueryString);
			logger.info("Drop Down Text: "+ visibleText + " : Selected");

			String script = " return $("+"\""+"#"+dropDownID+"\""+").find('option:selected').text();";
			selectedText = js.executeScript(script);
			// Wait For few seconds in order to get the drop-down text selected
			Utils.pauseFewSeconds();
			// Check if the selected text is blank which means the drop down text is not found
			if(StringUtils.isBlank(selectedText.toString()) ||
				StringUtils.isEmpty(selectedText.toString())){
				logger.error("Visible Text: "+ visibleText + " :  not found in the drop down.");
				Assert.fail("Visible Text: "+ visibleText + " :  not found in the drop down.");
			}

		} catch (Exception ex) {
			java.lang.System.out.println("Visible Text: "+ visibleText + " :  not found in the drop down.");
			logger.error("Visible Text: "+ visibleText + " :  not found in the drop down.", ex);
		}
		return selectedText.toString();
	}

	/**
	 * This method would load JQuery-fi the loaded application
	 * At present jquery.min.js file is placed under src/test/resources
	 * @param jQueryFile pass the jquery file name
	 * @param driver pass the default driver
	 * @return the JavaScriptExecutor to run the Java Script
	 */
	public static JavascriptExecutor loadJquery(String jQueryFile, WebDriver driver){
		JavascriptExecutor js = (JavascriptExecutor) driver;
		try{
			URL jqueryUrl = Resources.getResource(jQueryFile);
			js = (JavascriptExecutor) driver;
			String jqueryText = Resources.toString(jqueryUrl, Charsets.UTF_8);
			js.executeScript(jqueryText);
			js = (JavascriptExecutor) driver;
		} catch (Exception ex){
			throw new ScriptExecutionException("jquery file not found in resource or could not be loaded");
		}
		return js;
	}

	/**
	 * This method would read the first n number of specified values in a drop down
	 * @param driver pass the default driver
	 * @param dropDownID pass the dropdown ID
	 * @param n pass the number of values to fetch from the dropdown
	 * @return a List of Strings
	 */
	public static List<String> readFirstNValuesInDropDown(WebDriver driver, String dropDownID, int n){
		Object selectedText = null;
		List<String> dropDownValues = new ArrayList<String>();
		try{
			// Get the Jquery loaded in your application
			// jquery.min.js is stored under src/test/resources
			URL jqueryUrl = Resources.getResource("jquery.min.js");
			JavascriptExecutor js = (JavascriptExecutor) driver;
			String jqueryText = null;
				jqueryText = Resources.toString(jqueryUrl, Charsets.UTF_8);

			js.executeScript(jqueryText);
			js = (JavascriptExecutor) driver;
			// Run the Jquery
			for(int i = 1; i <= n; i++){
				String script = "$("+"\"#"+dropDownID+" option:eq("+i+")"+"\""+").attr('selected','selected');";
				js.executeScript(script);
				String jqueryString = " return $("+"\""+"#"+dropDownID+"\""+").find('option:selected').text();";
				selectedText = js.executeScript(jqueryString);
				dropDownValues.add(selectedText.toString());
			}

		} catch (Exception ex){
			logger.fatal(ex);
		  throw new ScriptExecutionException("Could not read first " + n + "number of Values in the drop down");
		}
		return dropDownValues;

	}

	/**
	 * This method can used to add n number of Blank values in the drop down by manipulating
	 * the DOM. SO the change would only be displayed application that would be under test
	 * until the page is refreshed/reloaded partly ot fully
	 * @param js pass the JavaScript Executor
	 * @param dropDownID pass the drop down ID
	 * @param n pass the number of blank fields to add at the beginning of dropdown
	 */
	public static void addNBlankValuesInDropDown(JavascriptExecutor js, String dropDownID, int n){
		try{
			String jQueryString = "";
			jQueryString += "$("+"\""+"#"+dropDownID+"\""+").prepend(\"<option value=''></option>\").val('');";
			java.lang.System.out.println("JQuery String = " + jQueryString);
			// Enter n number of blank values in the drop down
			for(int i = 0; i < n; i++){
				js.executeScript(jQueryString);
				java.lang.System.out.println(i);
			}

		} catch (Exception ex){
			logger.error("Could not load " + n +" number of Blank Values");
			throw new ScriptExecutionException("Could not load " + n +" number of Blank Values");
		}
	}


	/**
	 * This method switch to active alert .
	 * prints it's text on screen.
	 * click OK button on alert.
	 * @param driver is default driver.
	 * @return boolean.
	 */
	public static boolean acceptAlert(WebDriver driver) {
		
		boolean result = true;
		try {
			Alert alert = driver.switchTo().alert();
			java.lang.System.out.println("Alert text is : " + alert.getText());
			alert.accept();
		} catch (Exception ex) {
			java.lang.System.out.println("### No Alert is Active..!!");
			result = false;
		}
		return result;
	}
	
	public static boolean dismissAlert(WebDriver driver) {
		
		boolean result = true;
		try {
			Alert alert = driver.switchTo().alert();
			java.lang.System.out.println("Alert text is : " + alert.getText());
			alert.dismiss();
		} catch (Exception ex) {
			java.lang.System.out.println("### No Alert is Active..!!");
			result = false;
		}
		return result;
	}
	
	public static boolean isAlertRaised(WebDriver driver) {
		
		boolean result = true;
		try {
			driver.switchTo().alert();
		} catch (Exception ex) {
			java.lang.System.out.println("### No Alert is Active..!!");
			result = false;
		}
		return result;
	}
	
	/**
	 * This method should be used to switch focus to newly opened window
	 * using new window's url or an element's x-path
	 * First we create one iterator contains list of window handles
	 * Then, we would iterate through the Iterator and find the desired window
	 * In some cases we do not have option to pass new window's url so on those
	 * cases pass null for 'url' and pass the x-path
	 * If we would like to find the new window based on its URL pass null for
	 * xPath parameter.
	 * @param driver pass the default driver
	 * @param url pass the new window's url
	 * @param xPath xPath of the element to be found
	 * @return Return type is Boolean indicating if the window has been found
	 *
	 */
	public static boolean switchWindowByNewWindowUrlOrElement(WebDriver driver, String url, String xPath){
		Iterator<String> windowIterator = driver.getWindowHandles().iterator();
		Selenium selenium;
		boolean windowFound = false;
		while(windowIterator.hasNext()){
			String windowHandle = windowIterator.next();
			String testWindowUrl = driver.switchTo().window(windowHandle).getCurrentUrl();
			selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
			if(url != null){
				if(StringUtils.equalsIgnoreCase(url, testWindowUrl)){
					windowFound = true;
					logger.debug("Switched to new Window by using the window's url");
					break;
				}
			}
			if(xPath != null){
				if(selenium.isElementPresent(xPath)){
					windowFound = true;
					logger.debug("Switched to new Window by using an element in the new window");
					break;
				}
			}

		}
		return windowFound;
	}


	/**
	 * Use switchToNewWindow method to Select newly opened window
	 * This will search for one particular pre-known element in that window
	 * Then switch the focus of the driver to the new window
	 * @param driver pass the default driver
	 * @param  xPathOfOneButton pass the x-path of one Element in the new window
	 * @return return type is void
	 *
	 */
	public static void switchToNewWindow(WebDriver driver, String xPathOfOneButton){
		Utils.pauseFewSeconds();
		Selenium selenium = new WebDriverBackedSelenium(driver, driver.getCurrentUrl());
		Utils.pauseFewSeconds();
		for (String popUpHandle : driver.getWindowHandles()) {
			driver.switchTo().window(popUpHandle);
			selenium.waitForPageToLoad("10000");
			if (selenium.isElementPresent(xPathOfOneButton)) {
				java.lang.System.out.println("*** Found the New Window ***");
				break;
			}
		}
	}

	/**
	 * writeToTextFile method can be used to write a tab separated text file
	 * when we pass a List of Strings and the file name.
	 * @param columnData List of the dictionary values
	 * @param fileName  pass the file name that needs to be prepared
	 * @return boolean indicating if file write has been successful or not
	 */
	public static Boolean writeToTextFile(List<String> columnData, String fileName){

		String text = "";
		int count = 1;
		int sizeOfColumnData = columnData.size();
		BufferedWriter writer = null;
		Boolean hasWriteToFileBeenSuccessful = false;
		try {
			for(String value : columnData){
			text += value;
			// Add extra TAB only if the the latest entry in the text is not the last entry
			if(count < sizeOfColumnData)
				text += "\t";
			count++;
			}
			writer = new BufferedWriter(new FileWriter(fileName));
			writer.write(text);
			hasWriteToFileBeenSuccessful = true;
			} catch (IOException ex1) {
				java.lang.System.err.println(ex1);
			} finally {
				if (writer != null) {
					try {
						writer.close();
					} catch (IOException ex2) {
						java.lang.System.err.println(ex2);
					}
				}
			}
		return hasWriteToFileBeenSuccessful;
	}

	/**
	 *
	 * @param rawDate  should be in "YYYY.MM.DD" format
	 * @return
	 */
	public static int calculateAge(String rawDate){
		int age = 0;
		try{
			LocalDate dob = new LocalDate(rawDate);
			LocalDate systemDate = new LocalDate();
			Period period = new Period(dob, systemDate, PeriodType.yearMonthDay());
			age = period.getYears();

		} catch (ScriptExecutionException ex){

		}
		return age;
	}



}
