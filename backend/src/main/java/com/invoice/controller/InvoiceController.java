package com.invoice.controller;

import com.invoice.model.Invoice;
import com.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.invoice.dto.EmailRequest;
import com.invoice.service.EmailService;

@RestController
@CrossOrigin(origins = "http://localhost:4201")
@RequestMapping("/api/invoices")
public class InvoiceController {

    @Autowired
    private InvoiceService service;

    @Autowired
    private EmailService emailService;

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) {
        return service.create(invoice);
    }

    @GetMapping
    public List<Invoice> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public Invoice get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public Invoice update(@PathVariable Long id, @RequestBody Invoice invoice) {
        return service.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long id) throws Exception {
        Invoice inv = service.get(id);
        ByteArrayOutputStream baos = service.generatePdf(inv);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(baos.toByteArray());
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<String> sendInvoiceEmail(@PathVariable Long id, @RequestBody EmailRequest req) throws Exception {
        Invoice inv = service.get(id);
        ByteArrayOutputStream baos = service.generatePdf(inv);
        String filename = "invoice-" + id + ".pdf";
        try {
            emailService.sendInvoiceEmail(req.getTo(), req.getSubject() == null ? "Invoice" : req.getSubject(), req.getBody() == null ? "Please find attached invoice." : req.getBody(), baos, filename);
            return ResponseEntity.ok("Email sent");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Email error: " + e.toString());
        }
    }
}
