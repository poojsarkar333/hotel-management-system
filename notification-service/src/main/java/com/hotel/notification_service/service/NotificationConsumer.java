package com.hotel.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.hotel.notification_service.dto.NotificationMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class NotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(NotificationConsumer.class);

    @KafkaListener(topics = "hotel-notifications", groupId = "notification-group")
    public void consume(NotificationMessage message) {
        try {
            logger.info("Received Notification: type={}, message={}, refId={}",
                    message.getType(), message.getMessage(), message.getReferenceId());

            // Example: trigger email or push notification
            processNotification(message);

        } catch (Exception e) {
            logger.error("Error processing notification: {}", message, e);
            throw e; // allows retry mechanism to trigger
        }
    }

    private void processNotification(NotificationMessage message) {
        // Example business logic
        if ("BILL_CREATED".equals(message.getType())) {
            logger.info("Sending email for Bill ID: {}", message.getReferenceId());
            // integrate actual email sending logic here
        } else if ("ORDER_CREATED".equals(message.getType())) {
            logger.info("Sending order notification for Order ID: {}", message.getReferenceId());
        }
    }
}
