package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Admission;

public interface AdmissionRepository
        extends JpaRepository<Admission, Long> {

    // Find admissions by patient ID
    List<Admission> findByPatientId(Long patientId);

    // Find admissions by room ID
    List<Admission> findByRoomId(Long roomId);

    // Find admissions by status
    List<Admission> findByStatusIgnoreCase(String status);

    // Find all admissions with a specific status
    List<Admission> findByStatus(String status);

    // Check whether a room has an active admission
    boolean existsByRoomIdAndStatus(
            Long roomId,
            String status
    );

    // Check whether a patient already has an active admission
    boolean existsByPatientIdAndStatus(
            Long patientId,
            String status
    );
}