package com.invoice.service.impl;

import com.invoice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String defaultFrom;

    @Override
    public void sendInvoiceEmail(String to, String subject, String body, ByteArrayOutputStream pdf, String filename) throws Exception {
        if (to == null || to.trim().isEmpty()) {
            throw new IllegalArgumentException("Recipient (to) is required");
        }

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject == null ? "Invoice" : subject);
        helper.setText(body == null ? "Please find attached invoice." : body, false);

        // set From if configured
        if (defaultFrom != null && !defaultFrom.isBlank()) {
            try {
                helper.setFrom(defaultFrom);
            } catch (Exception ex) {
                // ignore setFrom failure; JavaMailSender may supply a default
            }
        } else if (mailSender instanceof JavaMailSenderImpl) {
            String username = ((JavaMailSenderImpl) mailSender).getUsername();
            if (username != null && !username.isBlank()) {
                try { helper.setFrom(username); } catch (Exception ignore) {}
            }
        }

        if (pdf != null) {
            helper.addAttachment(filename == null ? "invoice.pdf" : filename, new ByteArrayResource(pdf.toByteArray()));
        }

        try {
            mailSender.send(msg);
        } catch (MailException me) {
            // rethrow with clearer message
            throw new Exception("Failed to send email: " + me.getMessage(), me);
        }
    }
}
