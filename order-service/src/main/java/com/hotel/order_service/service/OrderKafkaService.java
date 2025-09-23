package com.hotel.order_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.hotel.order_service.client.RoomServiceClient;
import com.hotel.order_service.dto.Order;
import com.hotel.order_service.dto.OrderDTO;
import com.hotel.order_service.dto.RoomDTO;
import com.hotel.order_service.kafkaproducer.NotificationMessage;
import com.hotel.order_service.repo.OrderRepository;

@Service
public class OrderKafkaService {

    @Autowired
    private RoomServiceClient roomServiceClient;
    
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private KafkaTemplate<String, NotificationMessage> kafkaTemplate;

    public String createOrder(String roomId, Order order) {
        RoomDTO room = roomServiceClient.getRoomById(roomId);
        order.setStatus("PENDING");
        order.setCustomerName(roomId);
        orderRepository.save(order);
        OrderDTO orderDto = new OrderDTO(1L, room.getRoomId(), "CONFIRMED");

        // Send notification
        NotificationMessage message = new NotificationMessage(
            "ORDER_CREATED",
            "Order " + orderDto.getOrderId() + " created for room " + room.getName(),
            orderDto.getOrderId()
        );
        kafkaTemplate.send("hotel-notifications", message);

        return "Order Created";
    }
}

