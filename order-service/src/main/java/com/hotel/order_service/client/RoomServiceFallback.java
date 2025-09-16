package com.hotel.order_service.client;

import org.springframework.stereotype.Component;

import com.hotel.order_service.dto.RoomDTO;

@Component
public class RoomServiceFallback implements RoomServiceClient {

    @Override
    public RoomDTO getRoomById(Long roomId) {
        // Provide default behavior when room-service is down
        return new RoomDTO(roomId, "Unavailable", "Fallback description", 0);
    }
}


