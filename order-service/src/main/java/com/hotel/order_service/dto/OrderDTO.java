package com.hotel.order_service.dto;

public class OrderDTO {
    private Long orderId;
    private Long roomId;
    private String status;

    // constructors
    public OrderDTO() {}
    public OrderDTO(Long orderId, Long roomId, String status) {
        this.orderId = orderId;
        this.roomId = roomId;
        this.status = status;
    }
    // getters & setters
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public Long getRoomId() {
		return roomId;
	}
	public void setRoomId(Long roomId) {
		this.roomId = roomId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
    
}

