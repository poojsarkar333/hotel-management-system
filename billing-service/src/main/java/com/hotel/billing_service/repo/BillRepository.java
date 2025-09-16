package com.hotel.billing_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.billing_service.dto.Bill;

public interface BillRepository extends JpaRepository<Bill, Long> {
}
