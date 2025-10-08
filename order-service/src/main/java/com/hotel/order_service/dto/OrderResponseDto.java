package com.hotel.order_service.dto;

import java.math.BigDecimal;

public class OrderResponseDto {
 private Long id;
 private String customerName;
 private BigDecimal totalAmount;
 
 
 public OrderResponseDto(Long id, String customerName, BigDecimal totalAmount) {
	super();
	this.id = id;
	this.customerName = customerName;
	this.totalAmount = totalAmount;
}
 public Long getId() {
	return id;
 }
 public void setId(Long id) {
	this.id = id;
 }
 public String getCustomerName() {
	return customerName;
 }
 public void setCustomerName(String customerName) {
	this.customerName = customerName;
 }
 public BigDecimal getTotalAmount() {
	return totalAmount;
 }
 public void setTotalAmount(BigDecimal totalAmount) {
	this.totalAmount = totalAmount;
 }
 
 
}

