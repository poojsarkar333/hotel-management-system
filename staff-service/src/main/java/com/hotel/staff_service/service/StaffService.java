package com.hotel.staff_service.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.hotel.staff_service.dto.StaffRequestDTO;
import com.hotel.staff_service.dto.StaffResponseDTO;
import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.repo.StaffRepository;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public StaffResponseDTO createStaff(StaffRequestDTO request) {
        Staff staff = new Staff();
        staff.setUsername(request.getUsername());
        staff.setEmail(request.getEmail());
        staff.setPassword(passwordEncoder.encode(request.getPassword()));
        staff.setRole(request.getRole().toUpperCase());
        staff.setFirstname(request.getFirstname());
        staff.setLastname(request.getLastname());        
        staff.setDateOfJoining(request.getDateOfJoining());
        staff.setDateOfBirth(request.getDateOfBirth());
        Staff saved = staffRepository.save(staff);
        return mapToResponse(saved);
    }

    public StaffResponseDTO getStaff(Long id) {
        return staffRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
    }

    public List<StaffResponseDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }

    public void deactivateStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Staff not found"));
        staff.setActive(false);
        staffRepository.save(staff);
    }

    private StaffResponseDTO mapToResponse(Staff s) {
        StaffResponseDTO dto = new StaffResponseDTO();
        dto.setId(s.getId());
        dto.setUsername(s.getUsername());
        dto.setEmail(s.getEmail());
        dto.setRole(s.getRole());
        dto.setFirstname(s.getFirstname());
        dto.setLastname(s.getLastname());
        dto.setActive(s.getActive());
        dto.setDateOfJoining(s.getDateOfJoining());
        return dto;
    }
}

