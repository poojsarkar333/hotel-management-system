package com.hotel.order_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.hotel.order_service.dto.RoomDTO;

@FeignClient(name = "localhost:8082", fallback = RoomServiceFallback.class)
public interface RoomServiceClient {

    @GetMapping("/rooms/getRoom/{roomId}")
	RoomDTO getRoomById(@PathVariable("roomId") String roomId);
}


