package Origin.SDKLicence;

import java.io.InputStream;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailSender {
    private static Properties emailProperties = new Properties();
    
    static {
        try (InputStream input = EmailSender.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find email.properties");
                throw new RuntimeException("Email properties file not found");
            }
            emailProperties.load(input);
            System.out.println("Loaded email.properties: " + emailProperties);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize EmailSender", e);
        }
    }

    public static void sendEmail(String to, String subject, String body) {
        final String username = emailProperties.getProperty("username");
        final String password = emailProperties.getProperty("password");

        Properties prop = new Properties();
        prop.put("mail.smtp.host", emailProperties.getProperty("mail.smtp.host"));
        prop.put("mail.smtp.port", emailProperties.getProperty("mail.smtp.port"));
        prop.put("mail.smtp.auth", emailProperties.getProperty("mail.smtp.auth"));
        prop.put("mail.smtp.starttls.enable", emailProperties.getProperty("mail.smtp.starttls.enable"));

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

            System.out.println("Attempting to send email...");
            Transport.send(message);
            System.out.println("Email sent successfully");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
