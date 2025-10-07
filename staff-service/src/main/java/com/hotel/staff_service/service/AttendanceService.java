package com.hotel.staff_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.staff_service.entity.Attendance;
import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.repo.AttendanceRepository;
import com.hotel.staff_service.repo.StaffRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private StaffRepository staffRepository;

    public Attendance markAttendance(Long staffId, String status) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Attendance record = new Attendance();
        record.setStaff(staff);
        record.setDate(LocalDate.now());
        record.setCheckIn(LocalTime.now());
        record.setStatus(status);
        return attendanceRepository.save(record);
    }

    public List<Attendance> getAttendance(Long staffId) {
        return attendanceRepository.findByStaffId(staffId);
    }
}

