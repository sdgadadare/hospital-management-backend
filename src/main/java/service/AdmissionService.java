package service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Admission;
import entity.Patient;
import entity.Room;
import exception.ResourceNotFoundException;
import repository.AdmissionRepository;
import repository.PatientRepository;
import repository.RoomRepository;

@Service
public class AdmissionService {

    private final AdmissionRepository admissionRepository;
    private final PatientRepository patientRepository;
    private final RoomRepository roomRepository;

    public AdmissionService(
            AdmissionRepository admissionRepository,
            PatientRepository patientRepository,
            RoomRepository roomRepository) {

        this.admissionRepository = admissionRepository;
        this.patientRepository = patientRepository;
        this.roomRepository = roomRepository;
    }

    // =========================================================
    // ADMIT PATIENT
    // =========================================================

    @Transactional
    public Admission admitPatient(Admission admission) {

        if (admission.getPatient() == null
                || admission.getPatient().getId() == null) {

            throw new IllegalArgumentException(
                    "Patient is required."
            );
        }

        if (admission.getRoom() == null
                || admission.getRoom().getId() == null) {

            throw new IllegalArgumentException(
                    "Room is required."
            );
        }

        Long patientId =
                admission.getPatient().getId();

        Long roomId =
                admission.getRoom().getId();

        Patient patient =
                patientRepository.findById(patientId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with ID: "
                                                + patientId
                                )
                        );

        Room room =
                roomRepository.findById(roomId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Room not found with ID: "
                                                + roomId
                                )
                        );

        // -----------------------------------------------------
        // Room availability
        // -----------------------------------------------------

        if (!Boolean.TRUE.equals(room.getAvailable())) {

            throw new IllegalArgumentException(
                    "Room is currently occupied."
            );
        }

        // -----------------------------------------------------
        // Prevent multiple active admissions for same room
        // -----------------------------------------------------

        if (admissionRepository
                .existsByRoomIdAndStatus(roomId, "ADMITTED")) {

            throw new IllegalArgumentException(
                    "Room already has an active admission."
            );
        }

        // -----------------------------------------------------
        // Prevent same patient from having active admission
        // -----------------------------------------------------

        if (admissionRepository
                .existsByPatientIdAndStatus(
                        patientId,
                        "ADMITTED")) {

            throw new IllegalArgumentException(
                    "Patient already has an active admission."
            );
        }

        // -----------------------------------------------------
        // Admission date
        // -----------------------------------------------------

        LocalDate admissionDate =
                admission.getAdmissionDate();

        if (admissionDate == null) {

            admissionDate = LocalDate.now();

        } else if (admissionDate.isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Admission date cannot be in the past."
            );
        }

        admission.setAdmissionDate(admissionDate);

        // -----------------------------------------------------
        // Set admission data
        // -----------------------------------------------------

        admission.setPatient(patient);
        admission.setRoom(room);
        admission.setStatus("ADMITTED");
        admission.setDischargeDate(null);

        // -----------------------------------------------------
        // Occupy room
        // -----------------------------------------------------

        room.setAvailable(false);

        roomRepository.save(room);

        return admissionRepository.save(admission);
    }

    // =========================================================
    // GET ALL ADMISSIONS
    // =========================================================

    public List<Admission> getAllAdmissions() {

        return admissionRepository.findAll();
    }

    // =========================================================
    // GET ADMISSION BY ID
    // =========================================================

    public Admission getAdmissionById(Long id) {

        return admissionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Admission not found with ID: "
                                        + id
                        )
                );
    }

    // =========================================================
    // GET ADMISSIONS BY PATIENT
    // =========================================================

    public List<Admission> getAdmissionsByPatientId(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException(
                    "Patient not found with ID: "
                            + patientId
            );
        }

        return admissionRepository
                .findByPatientId(patientId);
    }

    // =========================================================
    // GET ADMISSIONS BY ROOM
    // =========================================================

    public List<Admission> getAdmissionsByRoomId(
            Long roomId) {

        if (!roomRepository.existsById(roomId)) {

            throw new ResourceNotFoundException(
                    "Room not found with ID: "
                            + roomId
            );
        }

        return admissionRepository
                .findByRoomId(roomId);
    }

    // =========================================================
    // GET ACTIVE ADMISSIONS
    // =========================================================

    public List<Admission> getActiveAdmissions() {

        return admissionRepository
                .findByStatus("ADMITTED");
    }

    // =========================================================
    // DISCHARGE PATIENT
    // =========================================================

    @Transactional
    public Admission dischargePatient(Long id) {

        Admission admission =
                getAdmissionById(id);

        if (!"ADMITTED".equalsIgnoreCase(
                admission.getStatus())) {

            throw new IllegalArgumentException(
                    "Patient is not currently admitted."
            );
        }

        LocalDate dischargeDate =
                LocalDate.now();

        if (dischargeDate.isBefore(
                admission.getAdmissionDate())) {

            throw new IllegalArgumentException(
                    "Discharge date cannot be before "
                            + "admission date."
            );
        }

        admission.setStatus("DISCHARGED");
        admission.setDischargeDate(dischargeDate);

        Room room = admission.getRoom();

        if (room != null) {
            room.setAvailable(true);
            roomRepository.save(room);
        }

        return admissionRepository.save(admission);
    }

    // =========================================================
    // UPDATE ADMISSION NOTES
    // =========================================================

    public Admission updateAdmissionNotes(
            Long id,
            String notes) {

        Admission admission =
                getAdmissionById(id);

        admission.setNotes(notes);

        return admissionRepository.save(admission);
    }

    // =========================================================
    // DELETE ADMISSION
    // =========================================================

    @Transactional
    public void deleteAdmission(Long id) {

        Admission admission =
                getAdmissionById(id);

        // Release room if admission is active
        if ("ADMITTED".equalsIgnoreCase(
                admission.getStatus())) {

            Room room = admission.getRoom();

            if (room != null) {
                room.setAvailable(true);
                roomRepository.save(room);
            }
        }

        admissionRepository.delete(admission);
    }
}