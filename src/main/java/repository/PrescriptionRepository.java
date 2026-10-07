package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Prescription;

public interface PrescriptionRepository
        extends JpaRepository<Prescription, Long> {

    List<Prescription> findByPatientId(Long patientId);

    List<Prescription> findByDoctorId(Long doctorId);

    List<Prescription> findByStatusIgnoreCase(String status);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByAppointmentIdAndIdNot(
            Long appointmentId,
            Long prescriptionId
    );
}