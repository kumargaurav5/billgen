package com.invoice.model;

import javax.persistence.Embeddable;
import java.math.BigDecimal;

@Embeddable
public class InvoiceItem {
    private String description;
    private Integer quantity;
    private BigDecimal rate;

    public InvoiceItem() {
    }

    public InvoiceItem(String description, Integer quantity, BigDecimal rate) {
        this.description = description;
        this.quantity = quantity;
        this.rate = rate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        if (quantity == null || rate == null) return BigDecimal.ZERO;
        return rate.multiply(new BigDecimal(quantity));
    }
}
