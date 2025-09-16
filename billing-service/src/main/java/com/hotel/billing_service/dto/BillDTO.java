package com.hotel.billing_service.dto;

public class BillDTO {
    private Long billId;
    private Long orderId;
    private Double amount;
    private String status;

    public BillDTO() {}

    public BillDTO(Long billId, Long orderId, Double amount, String status) {
        this.billId = billId;
        this.orderId = orderId;
        this.amount = amount;
        this.status = status;
    }

    // Getters and setters
    public Long getBillId() { return billId; }
    public void setBillId(Long billId) { this.billId = billId; }
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}

