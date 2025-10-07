package com.hotel.staff_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hotel.staff_service.entity.Shift;
import com.hotel.staff_service.entity.Staff;
import com.hotel.staff_service.repo.ShiftRepository;
import com.hotel.staff_service.repo.StaffRepository;

@Service
public class ShiftService {

    @Autowired
    private ShiftRepository shiftRepository;

    @Autowired
    private StaffRepository staffRepository;

    public Shift assignShift(Long staffId, LocalDate date, LocalTime start, LocalTime end, String type) {
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        Shift shift = new Shift();
        shift.setStaff(staff);
        shift.setDate(date);
        shift.setStartTime(start);
        shift.setEndTime(end);
        shift.setShiftType(type);

        return shiftRepository.save(shift);
    }

    public List<Shift> getShifts(Long staffId) {
        return shiftRepository.findByStaffId(staffId);
    }
}

