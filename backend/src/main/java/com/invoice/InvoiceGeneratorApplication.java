package com.invoice;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class InvoiceGeneratorApplication {
    public static void main(String[] args) {
        // load .env from project root if present and populate system properties
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String smtpUser = dotenv.get("SMTP_USERNAME");
        String smtpPass = dotenv.get("SMTP_PASSWORD");
        if (smtpUser != null) System.setProperty("SMTP_USERNAME", smtpUser);
        if (smtpPass != null) System.setProperty("SMTP_PASSWORD", smtpPass);

        SpringApplication.run(InvoiceGeneratorApplication.class, args);
    }
}
