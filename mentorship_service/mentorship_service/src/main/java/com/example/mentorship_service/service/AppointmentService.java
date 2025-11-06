package com.example.mentorship_service.service;


import com.example.mentorship_service.model.Appointment;
import com.example.mentorship_service.repository.AppointmentRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository repo;

    public AppointmentService(AppointmentRepository repo) {
        this.repo = repo;
    }

    public Appointment createAppointment(Appointment appointment) {
        return repo.save(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return repo.findAll();
    }

    public List<Appointment> getAppointmentsByStudent(String studentName) {
        return repo.findByStudentName(studentName);
    }

    public List<Appointment> getAppointmentsByAlumni(String alumniName) {
        return repo.findByAlumniName(alumniName);
    }

    public Optional<Appointment> getById(Integer id) {
        return repo.findById(id);
    }

    public Appointment approveAppointment(Integer id) {
        Appointment appt = repo.findById(id).orElseThrow();
        appt.setStatus("APPROVED");
        return repo.save(appt);
    }
}
