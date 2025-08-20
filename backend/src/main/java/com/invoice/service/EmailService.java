package com.invoice.service;

import java.io.ByteArrayOutputStream;

public interface EmailService {
    void sendInvoiceEmail(String to, String subject, String body, ByteArrayOutputStream pdf, String filename) throws Exception;
}
