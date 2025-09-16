package com.hotel.billing_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hotel.billing_service.dto.Bill;
import com.hotel.billing_service.dto.OrderDTO;
import com.hotel.billing_service.repo.BillRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class BillingService {
	
	
    private final BillRepository billRepository;
    
    @Autowired
    private RestTemplate restTemplate;

    @CircuitBreaker(name = "orderServiceCircuitBreaker", fallbackMethod = "fallbackFetchOrder")
    public OrderDTO fetchOrder(Long orderId) {
        String url = "http://order-service/orders/" + orderId;
        return restTemplate.getForObject(url, OrderDTO.class);
    }

    public OrderDTO fallbackFetchOrder(Long orderId, Throwable ex) {
        // fallback logic
        return new OrderDTO(orderId, 0L, "UNKNOWN");
    }
    
    public BillingService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill createBill(Bill bill) {
        bill.setStatus("UNPAID");
        return billRepository.save(bill);
    }
    
}

