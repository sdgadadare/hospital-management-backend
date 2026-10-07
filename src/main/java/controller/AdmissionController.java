package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.Admission;
import service.AdmissionService;

@RestController
@RequestMapping("/api/admissions")
@CrossOrigin(origins = "http://localhost:5173")
public class AdmissionController {

    private final AdmissionService admissionService;

    public AdmissionController(AdmissionService admissionService) {
        this.admissionService = admissionService;
    }

    // Admit Patient
    @PostMapping
    public ResponseEntity<Admission> admitPatient(
            @RequestBody Admission admission) {

        Admission savedAdmission =
                admissionService.admitPatient(admission);

        return new ResponseEntity<>(savedAdmission, HttpStatus.CREATED);
    }

    // Get All Admissions
    @GetMapping
    public ResponseEntity<List<Admission>> getAllAdmissions() {

        return ResponseEntity.ok(
                admissionService.getAllAdmissions());
    }

    // Get Admission By ID
    @GetMapping("/{id}")
    public ResponseEntity<Admission> getAdmissionById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                admissionService.getAdmissionById(id));
    }

    // Get Admissions By Patient ID
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Admission>> getAdmissionsByPatientId(
            @PathVariable Long patientId) {

        return ResponseEntity.ok(
                admissionService.getAdmissionsByPatientId(patientId));
    }

    // Get Admissions By Room ID
    @GetMapping("/room/{roomId}")
    public ResponseEntity<List<Admission>> getAdmissionsByRoomId(
            @PathVariable Long roomId) {

        return ResponseEntity.ok(
                admissionService.getAdmissionsByRoomId(roomId));
    }

    // Get Active Admissions
    @GetMapping("/active")
    public ResponseEntity<List<Admission>> getActiveAdmissions() {

        return ResponseEntity.ok(
                admissionService.getActiveAdmissions());
    }

    // Discharge Patient
    @PutMapping("/{id}/discharge")
    public ResponseEntity<Admission> dischargePatient(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                admissionService.dischargePatient(id));
    }

    // Update Admission Notes
    @PatchMapping("/{id}/notes")
    public ResponseEntity<Admission> updateAdmissionNotes(
            @PathVariable Long id,
            @RequestBody String notes) {

        return ResponseEntity.ok(
                admissionService.updateAdmissionNotes(id, notes));
    }

    // Delete Admission
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmission(
            @PathVariable Long id) {

        admissionService.deleteAdmission(id);

        return ResponseEntity.ok(
                "Admission deleted successfully");
    }
}