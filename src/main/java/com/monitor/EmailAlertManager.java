package com.monitor;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailAlertManager {

    // Configuration for Gmail SMTP Server
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";

    // Read credentials from environment variables
    private static final String SENDER_EMAIL = System.getenv("EMAIL_USER") != null ? System.getenv("EMAIL_USER") : "farzanehnasrabadii@gmail.com";
    private static final String APP_PASSWORD = System.getenv("EMAIL_PASS") != null ? System.getenv("EMAIL_PASS") : "yqdznwqlzzaafxuy";
    private static final String RECEIVER_EMAIL = System.getenv("EMAIL_RECEIVER") != null ? System.getenv("EMAIL_RECEIVER") : "faranehnasrabadii@gmail.com";

    /**
     * Sends an email notification to the administrator.
     */
    public static void sendEmailAlert(String subject, String messageText) {
        // Set up server properties
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // Secure connection requirement

        // Create a session with authentication
        Session session = Session.getInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
            }
        });

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECEIVER_EMAIL));
            message.setSubject(subject);
            message.setText(messageText);

            // Send the email
            System.out.println("Attempting to send an email alert...");
            Transport.send(message);
            System.out.println("Email alert sent successfully!");

        } catch (MessagingException e) {
            System.err.println("Failed to send email alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
}