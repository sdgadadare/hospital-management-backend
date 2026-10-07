
package controller;

import entity.Patient;
import service.PatientService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:5173")
public class PatientController {

    private final PatientService patientService;

    // Constructor Injection
    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Register a new patient
    @PostMapping
    public ResponseEntity<Patient> registerPatient(
            @Valid @RequestBody Patient patient) {

        Patient savedPatient = patientService.registerPatient(patient);

        return new ResponseEntity<>(savedPatient, HttpStatus.CREATED);
    }

    // Get all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {

        return ResponseEntity.ok(patientService.getAllPatients());
    }

    // Get patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(
            @PathVariable Long id) {

        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    // Update patient
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody Patient patient) {

        Patient updatedPatient =
                patientService.updatePatient(id, patient);

        return ResponseEntity.ok(updatedPatient);
    }

    // Delete patient
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePatient(
            @PathVariable Long id) {

        patientService.deletePatient(id);

        return ResponseEntity.ok("Patient deleted successfully");
    }
}
