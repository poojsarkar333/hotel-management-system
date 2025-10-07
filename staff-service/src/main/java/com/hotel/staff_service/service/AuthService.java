package com.hotel.staff_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.repo.StaffRepository;

@Service
public class AuthService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public Staff login(String username, String rawPassword) {
    	
    	 return staffRepository.findByUsername(username)
                 .filter(staff -> passwordEncoder.matches(rawPassword, staff.getPassword()))
                 .orElse(null);
    }
}
