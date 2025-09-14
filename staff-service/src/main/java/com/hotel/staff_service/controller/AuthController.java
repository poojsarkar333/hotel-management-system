package com.hotel.staff_service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.staff_service.entity.JwtResponse;
import com.hotel.staff_service.entity.LoginRequest;
import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.jwt.JwtUtil;
import com.hotel.staff_service.repo.StaffRepository;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Staff staff = staffRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getPassword().equals(staff.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(staff.getUsername(), staff.getRole());
        return ResponseEntity.ok(new JwtResponse(token));
    }
}

