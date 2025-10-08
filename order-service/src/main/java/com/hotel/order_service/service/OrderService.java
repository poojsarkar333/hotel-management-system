package com.hotel.order_service.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.order_service.client.RoomServiceClient;
import com.hotel.order_service.dto.Order;
import com.hotel.order_service.dto.OrderDTO;
import com.hotel.order_service.dto.OrderResponseDto;
import com.hotel.order_service.dto.RoomDTO;
import com.hotel.order_service.repo.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private RoomServiceClient roomServiceClient;

	public OrderDTO createOrder(String roomId) {
		RoomDTO room = roomServiceClient.getRoomById(roomId);
		// process order based on room
		return new OrderDTO(1L, room.getRoomId(), "CONFIRMED");
	}

	public List<Order> getAllOrders() {
		return orderRepository.findAll();
	}

	public Order createOrder(Order order) {
		order.setStatus("PENDING");
		return orderRepository.save(order);
	}

	// In your service layer
	public OrderResponseDto getOrderById(Long id) {
		Optional<Order> order = orderRepository.findById(id);
		//return new OrderResponseDto(order.getId(), order.getCustomerName(), order.getTotalAmount());
		return new OrderResponseDto(1l, "ABC", new BigDecimal(555));
	}

}
