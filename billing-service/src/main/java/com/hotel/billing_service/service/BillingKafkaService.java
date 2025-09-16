package com.hotel.billing_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hotel.billing_service.dto.BillDTO;
import com.hotel.billing_service.kafkaproducer.NotificationMessage;

@Service
public class BillingKafkaService {

    @Autowired
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public BillDTO createBill(Long orderId, Double amount) {
        // Assume you save the bill in the database here
        BillDTO bill = new BillDTO(1L, orderId, amount, "PAID");

        // Create notification
        NotificationMessage message = new NotificationMessage(
            "BILL_CREATED",
            "Bill #" + bill.getBillId() + " of amount $" + bill.getAmount() + " has been generated.",
            bill.getBillId()
        );

        // Send to Kafka topic
        kafkaTemplate.send("hotel-notifications", message);

        return bill;
    }
}
