package repository;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Appointment;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTime(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTime(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime
    );

    boolean existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndIdNot(
            Long doctorId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long appointmentId
    );

    boolean existsByPatientIdAndAppointmentDateAndAppointmentTimeAndIdNot(
            Long patientId,
            LocalDate appointmentDate,
            LocalTime appointmentTime,
            Long appointmentId
    );
}