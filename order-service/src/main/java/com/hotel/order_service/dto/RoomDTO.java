package com.hotel.order_service.dto;

public class RoomDTO {
    private String roomId;
    private String name;
    private String description;
    private int capacity;

    // constructors
    public RoomDTO() {}
    public RoomDTO(String roomId, String name, String description, int capacity) {
        this.roomId = roomId;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
    }
    // getters & setters
	public String getRoomId() {
		return roomId;
	}
	public void setRoomId(String roomId) {
		this.roomId = roomId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCapacity() {
		return capacity;
	}
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
    
}

