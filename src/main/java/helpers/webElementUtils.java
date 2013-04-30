package atlas.cmr.automation.helpers;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Created with IntelliJ IDEA.
 * User: APattanayak
 * Date: 12/14/12
 * Time: 3:07 PM
 * To change this template use File | Settings | File Templates.
 */
public class webElementUtils {
	static Logger logger = Logger.getLogger(webElementUtils.class);
	public WebElementType webElementType;
	public WebElementType findElementType(WebDriver driver, String xPath){
		WebElementType[] types = WebElementType.values();

		driver.findElement(By.xpath(xPath)).getAttribute("type");

		return WebElementType.TEXTBOX;

	}

}
