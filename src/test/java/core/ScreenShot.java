package atlas.cmr.automation.test.core;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.TestListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class ScreenShot extends TestListenerAdapter {

	@Override
	public void onTestFailure(ITestResult result) {
		File file = new File("");
		Reporter.setCurrentTestResult(result);
		DateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_zzz");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}

		String ipString = ip == null? "" : ip.getHostAddress();
		String folderPath = file.getAbsolutePath() + 
				"/reports" + "/" + 
				dateString + "/" + 
				ipString + "/";
		folderPath = new File(folderPath).toURI().getPath();
		new File(folderPath).mkdirs();
		Robot robot;
		try {
			robot = new Robot();
			Toolkit toolKit = Toolkit.getDefaultToolkit();

			// Testing Screen Shot //
			WebDriver driver = null;
			List<ITestContext> contexts = this.getTestContexts();

			Iterator<ITestContext> ci = contexts.iterator();
			driver = (WebDriver)ci.next().getAttribute("Driver");
			File imageFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			BufferedImage bufferedImage = ImageIO.read(imageFile);
			String imagePath = folderPath + "/" + result.getName() + ".png";
			ImageIO.write(bufferedImage, "png", new File(imagePath));

			Reporter.log("Screen shot saved at " + folderPath);
			Reporter.log("\n\n Browser used :" + driver);
			String imageTag = "<img src=\"" + imagePath + "\" height=\"400\" width=\"500\"/>";
			Reporter.log(imageTag);

			// Testing Done //
		} catch (AWTException e1) {
		}
		catch (IOException e) {
		}
		Reporter.setCurrentTestResult(null);
	}
	
	public static void main(String[] args) throws IOException{
		File file = new File("");
		DateFormat dateFormat = new SimpleDateFormat("dd_MMM_yyyy_hh_mm_ss_zzz");
		Date date = new Date();
		String dateString = dateFormat.format(date);
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
		}
		String ipString = ip == null? "" : ip.getHostAddress();
		String fileNamePath = file.getAbsolutePath() +
				"/reports" + "/" +
				dateString + "/" +
				ipString + "/";
		String rawPath = new File(fileNamePath).toURI().getPath();
		java.lang.System.out.println(rawPath);
		java.lang.System.out.println(new File(rawPath).mkdirs());
	}
}