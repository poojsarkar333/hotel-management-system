package com.hotel.order_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.order_service.dto.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
