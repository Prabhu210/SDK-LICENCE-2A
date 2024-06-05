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
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class LicenceManagement {
    private WebDriver driver;
    private static boolean loggedIn = false;
    private static final String REMINDER_EMAIL = "prabhu.m@acviss.com"; // Replace with recipient email
    private static final String FROM_EMAIL = "prabhu.m@acviss.com"; // Replace with your email
    private static final String EMAIL_PASSWORD = "xdqy feic tspf fxxn"; // Replace with your email password

    @BeforeClass
    public void setUp() {
        // Set up WebDriver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
      //  options.addArguments("headless");

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Login if not already logged in
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

    @Test
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
            // Implement your license handling logic here
            // Example: Clicking on license elements and extracting expiry date
            WebElement licenceElement = driver.findElement(By.xpath("//div/div/div/following::*/li[8]/div/div[1]/i[2]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", licenceElement);
            WebElement navigationItem = driver.findElement(By.xpath("(//span[@class='rz-navigation-item-text'])[26]"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].click()", navigationItem);
            WebElement expiryElement = driver.findElement(By.xpath("//div[3]/div[2]"));
            String text = expiryElement.getText();
            System.out.println(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void navigateToNextAction() {
        System.out.println("Navigating to the next action...");
    }

    @Test(dependsOnMethods = "executePopUpOrLicence")
    public void checkAndScheduleEmailReminder() {
        try {
            WebElement expiryDateElement = driver.findElement(By.xpath("//div[3]/div[2]"));
            String expiryDateText = expiryDateElement.getText().trim();
            System.out.println("Expiry Date: " + expiryDateText);

            // Parse the expiry date string to LocalDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z"); // Adjust the format as per your application
            LocalDate expiryDate = LocalDate.parse(expiryDateText, formatter);

            // Calculate reminder dates
            LocalDate reminderDate30Days = expiryDate.minusDays(30);
            LocalDate reminderDate29Days = expiryDate.minusDays(29);
            
            LocalDate reminderDate15Days = expiryDate.minusDays(15);

            // Get today's date
            LocalDate currentDate = LocalDate.now();

            // Send reminders if today is the reminder date
            if (currentDate.equals(reminderDate30Days) || currentDate.equals(reminderDate15Days)||currentDate.equals(reminderDate29Days)) {
                scheduleEmailReminder(expiryDateText);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void scheduleEmailReminder(String expiryDateText) {
        try {
            String subject = "License Expiry Reminder";
            String body = "Your license is expiring on: " + expiryDateText + ". Your SDK will expire after 30 days. Please renew it before then.";

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

class EmailSender {
    public static void sendEmail(String to, String subject, String body) {
        final String username = "prabhu.m@acviss.com";
        final String password = "xdqy feic tspf fxxn"; // Replace with your email password

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(prop, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}