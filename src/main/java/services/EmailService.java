package services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String FROM_EMAIL = "montahabenjabalah@gmail.com"; // User's email
    private static final String EMAIL_PASSWORD = "alyg zwco arqp diod"; // User's app password

    public static void sendEmail(String toEmail, String subject, String body) throws MessagingException {
        System.out.println("Preparing to send email to: " + toEmail);
        if (toEmail == null || toEmail.trim().isEmpty()) {
            throw new MessagingException("Email address cannot be empty");
        }
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, EMAIL_PASSWORD);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(body);

        try {
            System.out.println("Attempting to send email...");
            Transport.send(message);
            System.out.println("Email sent successfully to: " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    public static void sendMedicationReminder(String toEmail, String patientName, String medicationName, 
            String dosage, String schedule) throws MessagingException {
        String subject = "Medication Reminder: " + medicationName;
        String body = String.format("""
            Dear %s,
                        
            This is a reminder for your medication:
            
            Medication: %s
            Dosage: %s
            Schedule: %s
                        
            Please take your medication as prescribed. If you have any questions,
            contact your healthcare provider.
                        
            Best regards,
            Your Healthcare Team
            """, patientName, medicationName, dosage, schedule);
                        
        sendEmail(toEmail, subject, body);
    }

    public static void sendMedicationReport(String toEmail, String patientName, String reportPath) throws MessagingException {
        String subject = "Your Medication Report";
        String body = String.format("""
            Dear %s,
                        
            Please find attached your medication report. This report contains important
            information about your prescribed medications and schedule.
                        
            If you have any questions about the report, please contact your healthcare provider.
                        
            Best regards,
            Your Healthcare Team
            """, patientName);
                        
        sendEmail(toEmail, subject, body);
    }
}
