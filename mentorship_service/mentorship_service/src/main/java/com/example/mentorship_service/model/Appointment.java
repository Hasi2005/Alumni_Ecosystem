package com.example.mentorship_service.model;



import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "student_name", nullable = false)
    private String studentName;

    @Column(name = "alumni_name", nullable = false)
    private String alumniName;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointment_time", nullable = false)
    private LocalTime appointmentTime;

    @Column(name = "notes")
    private String notes;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @PrePersist
    void prePersist() {
        if (this.status == null || this.status.isBlank()) {
            this.status = "SCHEDULED";
        }
    }
}
