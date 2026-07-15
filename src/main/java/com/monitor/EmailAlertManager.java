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

    // Default configuration for Mailhog (local development)
    private static final String SMTP_HOST = "localhost";
    private static final String SMTP_PORT = "1025"; // Mailhog SMTP port

    // Credentials (not strictly required by Mailhog, but kept for compatibility)
    private static final String SENDER_EMAIL = "system-monitor@local.com";
    private static final String RECEIVER_EMAIL = "admin@local.com";

    /**
     * Sends an email notification to the administrator.
     */
    public static void sendEmailAlert(String subject, String messageText) {
        // Set up server properties for Mailhog (no TLS/Auth required for local testing)
        Properties prop = new Properties();
        prop.put("mail.smtp.host", SMTP_HOST);
        prop.put("mail.smtp.port", SMTP_PORT);
        prop.put("mail.smtp.auth", "false"); // Mailhog doesn't need auth
        prop.put("mail.smtp.starttls.enable", "false"); // Mailhog doesn't need TLS

        // Create a session
        Session session = Session.getInstance(prop);

        try {
            // Create a default MimeMessage object
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECEIVER_EMAIL));
            message.setSubject(subject);
            message.setText(messageText);

            // Send the email
            System.out.println("⚡ [Email Service] Attempting to send local alert via Mailhog...");
            Transport.send(message);
            System.out.println("✅ [Email Service] Local alert sent successfully!");

        } catch (MessagingException e) {
            System.err.println("❌ [Email Service] Failed to send email alert: " + e.getMessage());
            e.printStackTrace();
        }
    }
}