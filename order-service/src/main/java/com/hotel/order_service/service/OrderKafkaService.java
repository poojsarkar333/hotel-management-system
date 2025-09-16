package com.hotel.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hotel.order_service.client.RoomServiceClient;
import com.hotel.order_service.dto.OrderDTO;
import com.hotel.order_service.dto.RoomDTO;
import com.hotel.order_service.kafkaproducer.NotificationMessage;

@Service
public class OrderKafkaService {

    @Autowired
    private RoomServiceClient roomServiceClient;

    @Autowired
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public OrderDTO createOrder(Long roomId) {
        RoomDTO room = roomServiceClient.getRoomById(roomId);
        OrderDTO order = new OrderDTO(1L, room.getRoomId(), "CONFIRMED");

        // Send notification
        NotificationMessage message = new NotificationMessage(
            "ORDER_CREATED",
            "Order " + order.getOrderId() + " created for room " + room.getName(),
            order.getOrderId()
        );
        kafkaTemplate.send("hotel-notifications", message);

        return order;
    }
}

