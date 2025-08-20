package com.invoice.service;

import com.invoice.model.Invoice;

import java.io.ByteArrayOutputStream;
import java.util.List;

public interface InvoiceService {
    Invoice create(Invoice invoice);
    Invoice update(Long id, Invoice invoice);
    Invoice get(Long id);
    List<Invoice> list();
    void delete(Long id);
    ByteArrayOutputStream generatePdf(Invoice invoice) throws Exception;
}
