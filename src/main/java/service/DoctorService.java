
package service;

import java.util.List;

import org.springframework.stereotype.Service;

import entity.Doctor;
import exception.ResourceNotFoundException;
import repository.DoctorRepository;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    // Register a new doctor
    public Doctor registerDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    // Get all doctors
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // Get doctor by ID
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Doctor not found with ID: " + id
                ));
    }

    // Update doctor details
    public Doctor updateDoctor(Long id, Doctor updatedDoctor) {

        Doctor existingDoctor = getDoctorById(id);

        existingDoctor.setFullName(updatedDoctor.getFullName());
        existingDoctor.setSpecialization(updatedDoctor.getSpecialization());
        existingDoctor.setQualification(updatedDoctor.getQualification());
        existingDoctor.setPhoneNumber(updatedDoctor.getPhoneNumber());
        existingDoctor.setEmail(updatedDoctor.getEmail());
        existingDoctor.setConsultationFee(updatedDoctor.getConsultationFee());
        existingDoctor.setAvailable(updatedDoctor.isAvailable());

        return doctorRepository.save(existingDoctor);
    }

    // Delete doctor
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }
}
