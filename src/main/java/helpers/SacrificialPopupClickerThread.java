package atlas.cmr.automation.helpers;

import org.openqa.selenium.WebElement;

/**
 * This class can be used in order to deal with pop ups
 *
 */
public class SacrificialPopupClickerThread extends Thread {
	private WebElement element;
	public SacrificialPopupClickerThread(WebElement element){
		this.element = element;
	}
	public void run(){
		element.click();
	}
}