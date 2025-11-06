package com.example.mentorship_service.repository;


import com.example.mentorship_service.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByStudentName(String studentName);
    List<Appointment> findByAlumniName(String alumniName);
}

