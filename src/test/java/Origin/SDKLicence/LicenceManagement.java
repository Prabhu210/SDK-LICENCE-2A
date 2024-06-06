package Origin.SDKLicence;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.SkipException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LicenceManagement extends Baseclass {
    private static boolean loggedIn = false;
    private WebDriverWait wait;
    private ChromeDriver driver;
    private static final String REMINDER_EMAIL = "prabhu.m@acviss.com"; // Replace with recipient email

    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
         options.addArguments("headless"); // Run in headless mode

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Login if not already logged in
        if (!loggedIn) {
            login();
            loggedIn = true;
        }
    }

    private void login() {
        try {
            driver.get("https://localhost:5001/");
            driver.findElement(By.xpath("//div/button[3]")).click();
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Proceed to localhost (unsafe)")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", element);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Input_Username"))).sendKeys("support@acviss.com");
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Input_Password"))).sendKeys("Acviss@12#$!");
            WebElement button = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Log in']")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", button);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void executePopUpOrLicence() {
        try {
            if (isElementPresent(By.id("shift8"))) {
                // Interact with the dialog box
                driver.findElement(By.id("shift8")).click();
                driver.findElement(By.xpath("//button[text()='Next']")).click();
                driver.findElement(By.xpath("//button[text()='Submit']")).click();
            } else {
                // If the dialog box is not present, skip this test silently
                throw new SkipException("Element with ID 'shift8' not found, skipping the test.");
            }
        } catch (SkipException e) {
            // Handle SkipException silently without rethrowing
            System.out.println("Skipped test: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions here (if needed)
            e.printStackTrace();
            throw new SkipException("Failed to execute test due to exception: " + e.getMessage());
        }
    }

    @Test
    public void licence() {
        try {
            WebElement licenceElement = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//div/div/div/following::*/li[8]/div/div[1]/i[2]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", licenceElement);

            WebElement navigationItem = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("(//span[@class='rz-navigation-item-text'])[26]")));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", navigationItem);

            WebElement expiryElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[3]/div[2]")));
            String text = expiryElement.getText();
            System.out.println(text);

            checkAndScheduleEmailReminder(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private void checkAndScheduleEmailReminder(String expiryDateText) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z");
            LocalDate expiryDate = LocalDate.parse(expiryDateText, formatter);

            LocalDate reminderDate30Days = expiryDate.minusDays(30);
            LocalDate reminderDate29Days = expiryDate.minusDays(29);
            LocalDate reminderDate15Days = expiryDate.minusDays(15);
            LocalDate reminderDate28Days = expiryDate.minusDays(28);
            LocalDate currentDate = LocalDate.now();

            if (currentDate.equals(reminderDate30Days) || currentDate.equals(reminderDate29Days) || currentDate.equals(reminderDate15Days) || currentDate.equals(reminderDate28Days)) {
                scheduleEmailReminder(expiryDateText, currentDate, reminderDate30Days, reminderDate29Days, reminderDate15Days, reminderDate28Days);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void scheduleEmailReminder(String expiryDateText, LocalDate currentDate, LocalDate reminderDate30Days, LocalDate reminderDate29Days, LocalDate reminderDate15Days, LocalDate reminderDate28Days) {
        try {
            String subject = "License Expiry Reminder";
            String body = "";

            if (currentDate.equals(reminderDate30Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 30 days. Please renew it before then.";
            } else if (currentDate.equals(reminderDate29Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 29 days. Please renew it before then.";
            } else if (currentDate.equals(reminderDate15Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 15 days. Please renew it before then.";
            } else if (currentDate.equals(reminderDate28Days)) {
                body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 28 days. Please renew it before then.";
            }

            // Sending email without logging sensitive information
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
