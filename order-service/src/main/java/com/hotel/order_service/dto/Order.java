package com.hotel.order_service.dto;

import jakarta.persistence.*;

@Entity
@Table(name = "tbl_orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String item;
    private int quantity;
    private double price;
    private String status; // e.g., PENDING, COMPLETED, CANCELLED

    // Constructors, getters, setters
    public Order() {}

    public Order(String customerName, String item, int quantity, double price, String status) {
        this.customerName = customerName;
        this.item = item;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public String getItem() { return item; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getStatus() { return status; }

    public void setId(Long id) { this.id = id; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setItem(String item) { this.item = item; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setStatus(String status) { this.status = status; }
}
