package com.invoice.service.impl;

import com.invoice.model.Invoice;
import com.invoice.model.InvoiceItem;
import com.invoice.repo.InvoiceRepository;
import com.invoice.service.InvoiceService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private InvoiceRepository repo;

    private static final BigDecimal GST_PERCENT = new BigDecimal("0.18"); // 18% GST example

    @Override
    public Invoice create(Invoice invoice) {
        calculateTotals(invoice);
        return repo.save(invoice);
    }

    @Override
    public Invoice update(Long id, Invoice invoice) {
        Optional<Invoice> ex = repo.findById(id);
        if (!ex.isPresent()) throw new RuntimeException("Invoice not found");
        invoice.setId(id);
        calculateTotals(invoice);
        return repo.save(invoice);
    }

    @Override
    public Invoice get(Long id) {
        return repo.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Override
    public List<Invoice> list() {
        return repo.findAll();
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }

    private void calculateTotals(Invoice invoice) {
        BigDecimal sub = BigDecimal.ZERO;
        if (invoice.getItems() != null) {
            for (InvoiceItem it : invoice.getItems()) {
                BigDecimal amt = it.getAmount();
                sub = sub.add(amt == null ? BigDecimal.ZERO : amt);
            }
        }
        invoice.setSubTotal(sub);
        BigDecimal tax = sub.multiply(GST_PERCENT);
        invoice.setTax(tax);
        invoice.setTotal(sub.add(tax));
    }

    @Override
    public ByteArrayOutputStream generatePdf(Invoice invoice) throws Exception {
        calculateTotals(invoice);
        PDDocument doc = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        doc.addPage(page);

        PDPageContentStream cs = new PDPageContentStream(doc, page);
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 16);
        cs.newLineAtOffset(50, 750);
        cs.showText("Invoice");
        cs.endText();

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA, 12);
        cs.newLineAtOffset(50, 720);
        cs.showText("Client: " + invoice.getClientName());
        cs.newLineAtOffset(0, -15);
        cs.showText("Project: " + invoice.getProjectName());
        cs.newLineAtOffset(0, -15);
        cs.showText("Date: " + (invoice.getInvoiceDate() == null ? "" : invoice.getInvoiceDate().toString()));
        cs.endText();

        float y = 650;
        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.newLineAtOffset(50, y);
        cs.showText("Description");
        cs.newLineAtOffset(200, 0);
        cs.showText("Qty");
        cs.newLineAtOffset(50, 0);
        cs.showText("Rate");
        cs.newLineAtOffset(50, 0);
        cs.showText("Amount");
        cs.endText();

        y -= 20;
        for (int i = 0; i < invoice.getItems().size(); i++) {
            InvoiceItem it = invoice.getItems().get(i);
            cs.beginText();
            cs.setFont(PDType1Font.HELVETICA, 11);
            cs.newLineAtOffset(50, y);
            cs.showText(it.getDescription() == null ? "" : it.getDescription());
            cs.newLineAtOffset(200, 0);
            cs.showText(String.valueOf(it.getQuantity()));
            cs.newLineAtOffset(50, 0);
            cs.showText(it.getRate() == null ? "" : it.getRate().toString());
            cs.newLineAtOffset(50, 0);
            cs.showText(it.getAmount().toString());
            cs.endText();
            y -= 15;
        }

        cs.beginText();
        cs.setFont(PDType1Font.HELVETICA_BOLD, 12);
        cs.newLineAtOffset(50, y - 20);
        cs.showText("Subtotal: " + invoice.getSubTotal());
        cs.newLineAtOffset(0, -15);
        cs.showText("Tax (GST): " + invoice.getTax());
        cs.newLineAtOffset(0, -15);
        cs.showText("Total: " + invoice.getTotal());
        cs.endText();

        cs.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        doc.save(baos);
        doc.close();
        return baos;
    }
}
