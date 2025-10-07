package com.hotel.staff_service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.hotel.staff_service.dto.StaffRequestDTO;
import com.hotel.staff_service.dto.StaffResponseDTO;
import com.hotel.staff_service.service.StaffService;

import jakarta.transaction.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StaffServiceTest {

    @Autowired
    private StaffService staffService;

    @Test
    void testCreateStaff() {
        StaffRequestDTO dto = new StaffRequestDTO();
        dto.setName("Test User");
        dto.setEmail("test@hotel.com");
        dto.setPassword("password123");
        dto.setRole("WAITER");

        StaffResponseDTO created = staffService.createStaff(dto);

        //assertNotNull(created.getId());
        //assertEquals("Test User", created.getName());
        //assertEquals("WAITER", created.getRole());
    }
}

