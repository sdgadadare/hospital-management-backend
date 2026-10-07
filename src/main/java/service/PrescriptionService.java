package service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Appointment;
import entity.Doctor;
import entity.Patient;
import entity.Prescription;
import exception.ResourceNotFoundException;
import repository.AppointmentRepository;
import repository.DoctorRepository;
import repository.PatientRepository;
import repository.PrescriptionRepository;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public PrescriptionService(
            PrescriptionRepository prescriptionRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {

        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public Prescription createPrescription(
            Prescription prescription) {

        if (prescription == null) {
            throw new IllegalArgumentException(
                    "Prescription data is required."
            );
        }

        if (prescription.getPatient() == null
                || prescription.getPatient().getId() == null) {

            throw new IllegalArgumentException(
                    "Patient is required."
            );
        }

        if (prescription.getDoctor() == null
                || prescription.getDoctor().getId() == null) {

            throw new IllegalArgumentException(
                    "Doctor is required."
            );
        }

        Long patientId =
                prescription.getPatient().getId();

        Long doctorId =
                prescription.getDoctor().getId();

        Patient patient =
                patientRepository.findById(patientId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Patient not found with ID: "
                                                + patientId
                                )
                        );

        Doctor doctor =
                doctorRepository.findById(doctorId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Doctor not found with ID: "
                                                + doctorId
                                )
                        );

        LocalDate prescribedDate =
                prescription.getPrescribedDate();

        if (prescribedDate == null) {
            prescribedDate = LocalDate.now();
        }

        if (prescribedDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Prescription date cannot be in the future."
            );
        }

        prescription.setPrescribedDate(
                prescribedDate
        );

        prescription.setPatient(patient);
        prescription.setDoctor(doctor);

        if (prescription.getAppointment() != null
                && prescription.getAppointment().getId() != null) {

            Long appointmentId =
                    prescription.getAppointment().getId();

            Appointment appointment =
                    appointmentRepository.findById(appointmentId)
                            .orElseThrow(() ->
                                    new ResourceNotFoundException(
                                            "Appointment not found with ID: "
                                                    + appointmentId
                                    )
                            );

            if (!appointment.getPatient().getId()
                    .equals(patientId)
                    || !appointment.getDoctor().getId()
                    .equals(doctorId)) {

                throw new IllegalArgumentException(
                        "Appointment does not match the selected patient and doctor."
                );
            }

            if (prescriptionRepository
                    .existsByAppointmentId(appointmentId)) {

                throw new IllegalArgumentException(
                        "A prescription already exists for this appointment."
                );
            }

            prescription.setAppointment(
                    appointment
            );

        } else {

            prescription.setAppointment(null);
        }

        prescription.setStatus(
                normalizeStatus(
                        prescription.getStatus()
                )
        );

        return prescriptionRepository.save(
                prescription
        );
    }

    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }

    public Prescription getPrescriptionById(Long id) {

        return prescriptionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Prescription not found with ID: "
                                        + id
                        )
                );
    }

    public List<Prescription> getPrescriptionsByPatient(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException(
                    "Patient not found with ID: "
                            + patientId
            );
        }

        return prescriptionRepository
                .findByPatientId(patientId);
    }

    public List<Prescription> getPrescriptionsByDoctor(
            Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {

            throw new ResourceNotFoundException(
                    "Doctor not found with ID: "
                            + doctorId
            );
        }

        return prescriptionRepository
                .findByDoctorId(doctorId);
    }

    public List<Prescription> getPrescriptionsByStatus(
            String status) {

        if (status == null
                || status.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Prescription status is required."
            );
        }

        return prescriptionRepository
                .findByStatusIgnoreCase(
                        normalizeStatus(status)
                );
    }

    @Transactional
    public Prescription updatePrescription(
            Long id,
            Prescription updatedPrescription) {

        if (updatedPrescription == null) {

            throw new IllegalArgumentException(
                    "Prescription data is required."
            );
        }

        Prescription existingPrescription =
                getPrescriptionById(id);

        LocalDate prescribedDate =
                updatedPrescription.getPrescribedDate();

        if (prescribedDate == null) {
            prescribedDate = LocalDate.now();
        }

        if (prescribedDate.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Prescription date cannot be in the future."
            );
        }

        existingPrescription.setPrescribedDate(
                prescribedDate
        );

        existingPrescription.setDiagnosis(
                updatedPrescription.getDiagnosis()
        );

        existingPrescription.setInstructions(
                updatedPrescription.getInstructions()
        );

        existingPrescription.setStatus(
                normalizeStatus(
                        updatedPrescription.getStatus()
                )
        );

        return prescriptionRepository.save(
                existingPrescription
        );
    }

    public void deletePrescription(Long id) {

        Prescription prescription =
                getPrescriptionById(id);

        prescriptionRepository.delete(
                prescription
        );
    }

    private String normalizeStatus(String status) {

        if (status == null
                || status.trim().isEmpty()) {

            return "ACTIVE";
        }

        String normalized =
                status.trim().toUpperCase();

        switch (normalized) {

            case "ACTIVE":
            case "COMPLETED":
            case "CANCELLED":
                return normalized;

            default:
                throw new IllegalArgumentException(
                        "Invalid prescription status. Allowed values: " +
                        "ACTIVE, COMPLETED, CANCELLED."
                );
        }
    }
}