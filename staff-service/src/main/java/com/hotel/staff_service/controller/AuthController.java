package com.hotel.staff_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.staff_service.entity.LoginRequest;
import com.hotel.staff_service.entity.LoginResponse;
import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.jwt.JwtUtil;
import com.hotel.staff_service.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

	@Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        logger.info("Incoming request URI: {} {}", request.getUsername(), request.getPassword());

        Staff staff = authService.login(request.getUsername(), request.getPassword());
        
        if (staff == null) {
            // Credentials invalid
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }

        String token = jwtUtil.generateToken(staff);

        return ResponseEntity.ok(new LoginResponse(token));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            // You can blacklist this token in Redis or in-memory store if desired
            // For now, we simply inform the frontend to remove it
        }
        return ResponseEntity.ok("Logout successful");
    }

}

