package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.MedicalRecord;
import service.MedicalRecordService;

@RestController
@RequestMapping("/api/medical-records")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    // Create Medical Record
    @PostMapping
    public ResponseEntity<MedicalRecord> createMedicalRecord(
            @RequestBody MedicalRecord record) {

        MedicalRecord savedRecord =
                medicalRecordService.createMedicalRecord(record);

        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);
    }

    // Get All Medical Records
    @GetMapping
    public ResponseEntity<List<MedicalRecord>> getAllMedicalRecords() {

        return ResponseEntity.ok(
                medicalRecordService.getAllMedicalRecords());
    }

    // Get Medical Record By ID
    @GetMapping("/{id}")
    public ResponseEntity<MedicalRecord> getMedicalRecordById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                medicalRecordService.getMedicalRecordById(id));
    }

    // Get Medical Records By Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByPatientId(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                medicalRecordService.getRecordsByPatientId(patientId));
    }

    // Get Medical Records By Doctor ID
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<MedicalRecord>> getRecordsByDoctorId(
            @PathVariable Long doctorId) {

        return ResponseEntity.ok(
                medicalRecordService.getRecordsByDoctorId(doctorId));
    }

    // Update Medical Record
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateMedicalRecord(
            @PathVariable Long id,
            @RequestBody MedicalRecord record) {

        return ResponseEntity.ok(
                medicalRecordService.updateMedicalRecord(id, record));
    }

    // Delete Medical Record
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicalRecord(
            @PathVariable Long id) {

        medicalRecordService.deleteMedicalRecord(id);

        return ResponseEntity.ok(
                "Medical record deleted successfully");
    }
}