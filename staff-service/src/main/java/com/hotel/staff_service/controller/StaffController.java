package com.hotel.staff_service.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.staff_service.dto.StaffRequestDTO;
import com.hotel.staff_service.dto.StaffResponseDTO;
import com.hotel.staff_service.entity.Attendance;
import com.hotel.staff_service.entity.Shift;
import com.hotel.staff_service.service.AttendanceService;
import com.hotel.staff_service.service.ShiftService;
import com.hotel.staff_service.service.StaffService;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    @Autowired
    private StaffService staffService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private ShiftService shiftService;

    // --- Staff Management ---
    @PostMapping("/create")
    public ResponseEntity<StaffResponseDTO> createStaff(@RequestBody StaffRequestDTO request) {
        return ResponseEntity.ok(staffService.createStaff(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StaffResponseDTO> getStaff(@PathVariable Long id) {
        return ResponseEntity.ok(staffService.getStaff(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<StaffResponseDTO>> getAllStaff() {
        return ResponseEntity.ok(staffService.getAllStaff());
    }

    @PutMapping("/deactivate/{id}")
    public ResponseEntity<Void> deactivateStaff(@PathVariable Long id) {
        staffService.deactivateStaff(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.noContent().build();
    }

    // --- Attendance ---
    @PostMapping("/{id}/attendance")
    public ResponseEntity<Attendance> markAttendance(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PRESENT") String status) {
        return ResponseEntity.ok(attendanceService.markAttendance(id, status));
    }

    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<Attendance>> getAttendance(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendance(id));
    }

    // --- Shifts ---
    @PostMapping("/{id}/shift")
    public ResponseEntity<Shift> assignShift(
            @PathVariable Long id,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam String type) {
        return ResponseEntity.ok(shiftService.assignShift(
                id,
                LocalDate.parse(date),
                LocalTime.parse(startTime),
                LocalTime.parse(endTime),
                type
        ));
    }

    @GetMapping("/{id}/shifts")
    public ResponseEntity<List<Shift>> getShifts(@PathVariable Long id) {
        return ResponseEntity.ok(shiftService.getShifts(id));
    }
}
