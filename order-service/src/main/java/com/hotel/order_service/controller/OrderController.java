package com.hotel.order_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.order_service.dto.Order;
import com.hotel.order_service.dto.OrderResponseDto;
import com.hotel.order_service.service.OrderKafkaService;
import com.hotel.order_service.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	
	@Autowired
    private  OrderService orderService;
	
	@Autowired
	private OrderKafkaService orderKafkaService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping("/get/{id}")
    public ResponseEntity<OrderResponseDto> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/getAll")
    public List<Order> getOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/create")
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }
    
    @PostMapping("/createKafkaOrder/{roomId}")
    public String createKafkaOrder(@PathVariable String roomId, @RequestBody Order order) {
        return orderKafkaService.createOrder(roomId, order);
    }
}

