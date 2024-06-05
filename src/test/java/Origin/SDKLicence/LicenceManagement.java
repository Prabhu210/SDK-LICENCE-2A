package Origin.SDKLicence;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.*;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LicenceManagement {
    private WebDriver driver;
    private static boolean loggedIn = false;
    private static final String REMINDER_EMAIL = "prabhu.m@acviss.com"; // Replace with recipient email

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        // options.addArguments("headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        if (!loggedIn) {
            try {
                driver.get("https://localhost:5001/");
                driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(50));
                driver.findElement(By.id("details-button")).click();
                WebElement element = driver.findElement(By.linkText("Proceed to localhost (unsafe)"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
                driver.findElement(By.id("Input_Username")).sendKeys("support@acviss.com");
                driver.findElement(By.id("Input_Password")).sendKeys("Acviss@12#$!");
                WebElement button = driver.findElement(By.xpath("//button[text()='Log in']"));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click()", button);
                loggedIn = true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test(priority = 1)
    public void executePopUpOrLicence() {
        if (isPopUpPresent()) {
            popUp();
        } else {
            licence();
        }
        navigateToNextAction();
    }

    public boolean isPopUpPresent() {
        try {
            driver.findElement(By.id("shift8"));
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    public void popUp() {
        try {
            driver.findElement(By.id("shift8")).click();
            driver.findElement(By.xpath("//button[text()='Next']")).click();
            driver.findElement(By.xpath("//button[text()='Submit']")).click();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void licence() {
        try {
            WebElement licenceElement = driver.findElement(By.xpath("//div/div/div/following::*/li[8]/div/div[1]/i[2]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", licenceElement);
            WebElement navigationItem = driver.findElement(By.xpath("(//span[@class='rz-navigation-item-text'])[26]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", navigationItem);
            WebElement expiryElement = driver.findElement(By.xpath("//div[3]/div[2]"));
            String text = expiryElement.getText();
            System.out.println(text);

            checkAndScheduleEmailReminder(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToNextAction() {
        System.out.println("Navigating to the next action...");
    }

    public void checkAndScheduleEmailReminder(String expiryDateText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
            LocalDate expiryDate = LocalDate.parse(expiryDateText, formatter);

            LocalDate reminderDate30Days = expiryDate.minusDays(30);
            LocalDate reminderDate29Days = expiryDate.minusDays(29);
            LocalDate reminderDate15Days = expiryDate.minusDays(15);
            LocalDate currentDate = LocalDate.now();

            if (currentDate.equals(reminderDate30Days) || currentDate.equals(reminderDate15Days) || currentDate.equals(reminderDate29Days)) {
                scheduleEmailReminder(expiryDateText, currentDate, reminderDate30Days, reminderDate29Days, reminderDate15Days);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleEmailReminder(String expiryDateText, LocalDate currentDate, LocalDate reminderDate30Days, LocalDate reminderDate29Days, LocalDate reminderDate15Days) {
        try {
            String subject = "License Expiry Reminder";
            String body = "";

            if (currentDate.equals(reminderDate30Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 30 days. Please renew it before then.";
            } else if (currentDate.equals(reminderDate29Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 29 days. Please renew it before then.";
            } else if (currentDate.equals(reminderDate15Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 15 days. Please renew it before then.";
            }

            EmailSender.sendEmail(REMINDER_EMAIL, subject, body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}


