
package service;

import entity.Patient;
import exception.ResourceNotFoundException;
import repository.PatientRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    // Constructor Injection
    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Register a new patient
    public Patient registerPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Patient not found with ID: " + id
                ));
    }

    // Update patient
    public Patient updatePatient(Long id, Patient updatedPatient) {

        Patient existingPatient = getPatientById(id);

        existingPatient.setFullName(updatedPatient.getFullName());
        existingPatient.setAge(updatedPatient.getAge());
        existingPatient.setGender(updatedPatient.getGender());
        existingPatient.setPhoneNumber(updatedPatient.getPhoneNumber());
        existingPatient.setEmail(updatedPatient.getEmail());
        existingPatient.setAddress(updatedPatient.getAddress());
        existingPatient.setBloodGroup(updatedPatient.getBloodGroup());

        return patientRepository.save(existingPatient);
    }

    // Delete patient
    public void deletePatient(Long id) {
        Patient patient = getPatientById(id);
        patientRepository.delete(patient);
    }
}
