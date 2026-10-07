package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.MedicalRecord;

public interface MedicalRecordRepository
        extends JpaRepository<MedicalRecord, Long> {

    List<MedicalRecord> findByPatientId(Long patientId);

    List<MedicalRecord> findByDoctorId(Long doctorId);

    boolean existsByAppointmentId(Long appointmentId);

    boolean existsByAppointmentIdAndIdNot(
            Long appointmentId,
            Long medicalRecordId
    );
}