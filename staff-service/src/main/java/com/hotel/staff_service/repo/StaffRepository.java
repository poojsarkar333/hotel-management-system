package com.hotel.staff_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hotel.staff_service.entity.Staff;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
	
    Optional<Staff> findByUsername(String username);
    
    Optional<Staff> findByEmail(String email);

}

