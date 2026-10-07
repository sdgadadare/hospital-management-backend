package service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import exception.ResourceNotFoundException;
import repository.AppointmentRepository;
import repository.DoctorRepository;
import repository.PatientRepository;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository) {

        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    // =========================================================
    // BOOK NEW APPOINTMENT
    // =========================================================

    public Appointment bookAppointment(Appointment appointment) {

        if (appointment.getPatient() == null
                || appointment.getPatient().getId() == null) {

            throw new IllegalArgumentException(
                    "Patient is required."
            );
        }

        if (appointment.getDoctor() == null
                || appointment.getDoctor().getId() == null) {

            throw new IllegalArgumentException(
                    "Doctor is required."
            );
        }

        if (appointment.getAppointmentDate() == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (appointment.getAppointmentTime() == null) {

            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        Long patientId =
                appointment.getPatient().getId();

        Long doctorId =
                appointment.getDoctor().getId();

        LocalDate appointmentDate =
                appointment.getAppointmentDate();

        LocalTime appointmentTime =
                appointment.getAppointmentTime();

        // -----------------------------------------------------
        // Date validation
        // -----------------------------------------------------

        if (appointmentDate.isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        // -----------------------------------------------------
        // Patient validation
        // -----------------------------------------------------

        Patient patient =
                patientRepository.findById(patientId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with ID: "
                                                + patientId
                                )
                        );

        // -----------------------------------------------------
        // Doctor validation
        // -----------------------------------------------------

        Doctor doctor =
                doctorRepository.findById(doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with ID: "
                                                + doctorId
                                )
                        );

        // -----------------------------------------------------
        // Doctor availability
        // -----------------------------------------------------

        if (!doctor.isAvailable()) {

            throw new IllegalArgumentException(
                    "Doctor is currently unavailable."
            );
        }

        // -----------------------------------------------------
        // Doctor appointment conflict
        // -----------------------------------------------------

        boolean doctorAlreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTime(
                                doctorId,
                                appointmentDate,
                                appointmentTime
                        );

        if (doctorAlreadyBooked) {

            throw new IllegalArgumentException(
                    "Doctor already has an appointment "
                            + "at this date and time."
            );
        }

        // -----------------------------------------------------
        // Patient appointment conflict
        // -----------------------------------------------------

        boolean patientAlreadyBooked =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTime(
                                patientId,
                                appointmentDate,
                                appointmentTime
                        );

        if (patientAlreadyBooked) {

            throw new IllegalArgumentException(
                    "Patient already has an appointment "
                            + "at this date and time."
            );
        }

        // -----------------------------------------------------
        // Set managed entities
        // -----------------------------------------------------

        appointment.setPatient(patient);
        appointment.setDoctor(doctor);

        // -----------------------------------------------------
        // Default status
        // -----------------------------------------------------

        if (appointment.getStatus() == null
                || appointment.getStatus().trim().isEmpty()) {

            appointment.setStatus("SCHEDULED");
        } else {

            appointment.setStatus(
                    normalizeStatus(
                            appointment.getStatus()
                    )
            );
        }

        return appointmentRepository.save(appointment);
    }

    // =========================================================
    // GET ALL APPOINTMENTS
    // =========================================================

    public List<Appointment> getAllAppointments() {

        return appointmentRepository.findAll();
    }

    // =========================================================
    // GET APPOINTMENT BY ID
    // =========================================================

    public Appointment getAppointmentById(Long id) {

        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found with ID: "
                                        + id
                        )
                );
    }

    // =========================================================
    // UPDATE APPOINTMENT
    // =========================================================

    public Appointment updateAppointment(
            Long id,
            Appointment updatedAppointment) {

        Appointment existingAppointment =
                getAppointmentById(id);

        if (updatedAppointment.getAppointmentDate() == null) {

            throw new IllegalArgumentException(
                    "Appointment date is required."
            );
        }

        if (updatedAppointment.getAppointmentTime() == null) {

            throw new IllegalArgumentException(
                    "Appointment time is required."
            );
        }

        LocalDate appointmentDate =
                updatedAppointment.getAppointmentDate();

        LocalTime appointmentTime =
                updatedAppointment.getAppointmentTime();

        // -----------------------------------------------------
        // Date validation
        // -----------------------------------------------------

        if (appointmentDate.isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Appointment date cannot be in the past."
            );
        }

        Long doctorId =
                existingAppointment.getDoctor().getId();

        Long patientId =
                existingAppointment.getPatient().getId();

        // -----------------------------------------------------
        // Doctor conflict check
        // Exclude current appointment
        // -----------------------------------------------------

        boolean doctorAlreadyBooked =
                appointmentRepository
                        .existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndIdNot(
                                doctorId,
                                appointmentDate,
                                appointmentTime,
                                id
                        );

        if (doctorAlreadyBooked) {

            throw new IllegalArgumentException(
                    "Doctor already has another appointment "
                            + "at this date and time."
            );
        }

        // -----------------------------------------------------
        // Patient conflict check
        // Exclude current appointment
        // -----------------------------------------------------

        boolean patientAlreadyBooked =
                appointmentRepository
                        .existsByPatientIdAndAppointmentDateAndAppointmentTimeAndIdNot(
                                patientId,
                                appointmentDate,
                                appointmentTime,
                                id
                        );

        if (patientAlreadyBooked) {

            throw new IllegalArgumentException(
                    "Patient already has another appointment "
                            + "at this date and time."
            );
        }

        // -----------------------------------------------------
        // Update fields
        // -----------------------------------------------------

        existingAppointment.setAppointmentDate(
                appointmentDate
        );

        existingAppointment.setAppointmentTime(
                appointmentTime
        );

        if (updatedAppointment.getStatus() == null
                || updatedAppointment.getStatus().trim().isEmpty()) {

            existingAppointment.setStatus("SCHEDULED");

        } else {

            existingAppointment.setStatus(
                    normalizeStatus(
                            updatedAppointment.getStatus()
                    )
            );
        }

        existingAppointment.setReason(
                updatedAppointment.getReason()
        );

        return appointmentRepository.save(
                existingAppointment
        );
    }

    // =========================================================
    // CANCEL APPOINTMENT
    // =========================================================

    public Appointment cancelAppointment(Long id) {

        Appointment appointment =
                getAppointmentById(id);

        appointment.setStatus("CANCELLED");

        return appointmentRepository.save(
                appointment
        );
    }

    // =========================================================
    // DELETE APPOINTMENT
    // =========================================================

    public void deleteAppointment(Long id) {

        Appointment appointment =
                getAppointmentById(id);

        appointmentRepository.delete(appointment);
    }

    // =========================================================
    // STATUS VALIDATION
    // =========================================================

    private String normalizeStatus(String status) {

        String normalized =
                status.trim().toUpperCase();

        switch (normalized) {

            case "SCHEDULED":
            case "CONFIRMED":
            case "COMPLETED":
            case "CANCELLED":

                return normalized;

            default:

                throw new IllegalArgumentException(
                        "Invalid appointment status. "
                                + "Allowed values: "
                                + "SCHEDULED, CONFIRMED, "
                                + "COMPLETED, CANCELLED."
                );
        }
    }
}