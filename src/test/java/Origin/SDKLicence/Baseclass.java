package Origin.SDKLicence;

import java.awt.AWTException;
import java.awt.Robot;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Alert;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import io.github.bonigarcia.wdm.WebDriverManager;

public class Baseclass {
	public static WebDriver driver;
	public static TakesScreenshot ts;
	public static JavascriptExecutor js;
	public static Actions a;
	public static Robot r;
	public static File f;
	public static FileInputStream fl;
	public static Select ss;
	public static Object obj;
	public static String title;
	public static String url;
	
	public static void select(WebElement Element,String value)
	{
		ss=new Select(Element);
		ss.selectByValue(value);
	}
	
	
	public static void browserconfig() 
	{
		//WebDriverManager.chromedriver().setup();
		ChromeOptions options=new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
	   // options.addArguments("headless");
		driver=new ChromeDriver(options);
	}
	public static void launchbrowser(String url)
	{
		driver.get(url);
	}
	public static void maxiwindow() 
	{
		driver.manage().window().maximize();
	}
	public static String currenturl()
	{
		url = driver.getCurrentUrl();
		return url;
	}
	public static String title()
	{
		  title = driver.getTitle();
		  return title;
	}
	
	public static void filtextbox(WebElement Element,String txt)
	{
		Element.sendKeys(txt);
	}
	public static void click(WebElement Element)
	{
		Element.click();
	}
	public static void takesScreenshot(String location) throws IOException 
	{
		ts=(TakesScreenshot)driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		File dist=new File(location);
		FileUtils.copyFile(source, dist);
	}
	public static Object javaScript(String script,WebElement Element)
	{
		js=(JavascriptExecutor)driver;
		js.executeScript(script,Element);
		Object obj=js.executeScript(script,Element);
		return obj;
	}
	public static void movetoElement(WebElement Element)
	{
		a=new Actions(driver);
		a.moveToElement(Element).build().perform();
	}
	public static void doubleClick(WebElement Element)
	{
		a=new Actions(driver);
		a.doubleClick(Element).perform();
	}
	public static void rightClick(WebElement Element)
	{
		a=new Actions(driver);
		a.contextClick(Element).perform();
	}
	public static void dragAndDrop(WebElement Element,WebElement Element1)
	{
		a=new Actions(driver);
		a.dragAndDrop(Element, Element1);
	}
	public static void alertAccept()
	{
		Alert a = driver.switchTo().alert();
		a.accept();
	}
	public static void alertDismiss()
	{
		Alert a = driver.switchTo().alert();
		a.dismiss();
	}
	public static void robot(int keyEvent) throws AWTException
	{
		r=new Robot();
		r.keyPress(keyEvent);
		r.keyRelease(keyEvent);
	}
	
	
	public static void date()
	{
		Date d=new Date();
		System.out.println(d);
		
	}

}
