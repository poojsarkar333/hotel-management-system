package com.hotel.order_service.kafkaproducer;

public class NotificationMessage {
    private String type;
    private String message;
    private Long referenceId;

    // Constructors
    public NotificationMessage() {}

    public NotificationMessage(String type, String message, Long referenceId) {
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
    }

    // Getters & Setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getReferenceId() { return referenceId; }
    public void setReferenceId(Long referenceId) { this.referenceId = referenceId; }
}
