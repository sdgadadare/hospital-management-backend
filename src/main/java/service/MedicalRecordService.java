package service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Appointment;
import entity.Doctor;
import entity.MedicalRecord;
import entity.Patient;
import exception.ResourceNotFoundException;
import repository.AppointmentRepository;
import repository.DoctorRepository;
import repository.MedicalRecordRepository;
import repository.PatientRepository;

@Service
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;

    public MedicalRecordService(
            MedicalRecordRepository medicalRecordRepository,
            PatientRepository patientRepository,
            DoctorRepository doctorRepository,
            AppointmentRepository appointmentRepository) {

        this.medicalRecordRepository = medicalRecordRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
    }

    @Transactional
    public MedicalRecord createMedicalRecord(
            MedicalRecord record) {

        if (record == null) {
            throw new IllegalArgumentException(
                    "Medical record data is required."
            );
        }

        if (record.getPatient() == null
                || record.getPatient().getId() == null) {

            throw new IllegalArgumentException(
                    "Patient is required."
            );
        }

        if (record.getDoctor() == null
                || record.getDoctor().getId() == null) {

            throw new IllegalArgumentException(
                    "Doctor is required."
            );
        }

        Long patientId =
                record.getPatient().getId();

        Long doctorId =
                record.getDoctor().getId();

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

        validateRecordDate(
                record.getRecordDate()
        );

        validateTextFields(record);

        record.setPatient(patient);
        record.setDoctor(doctor);

        if (record.getAppointment() != null
                && record.getAppointment().getId() != null) {

            Long appointmentId =
                    record.getAppointment().getId();

            Appointment appointment =
                    appointmentRepository.findById(
                            appointmentId
                    ).orElseThrow(() ->
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

            if (medicalRecordRepository
                    .existsByAppointmentId(appointmentId)) {

                throw new IllegalArgumentException(
                        "A medical record already exists for this appointment."
                );
            }

            record.setAppointment(appointment);

        } else {

            record.setAppointment(null);
        }

        return medicalRecordRepository.save(record);
    }

    public List<MedicalRecord> getAllMedicalRecords() {

        return medicalRecordRepository.findAll();
    }

    public MedicalRecord getMedicalRecordById(
            Long id) {

        return medicalRecordRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medical record not found with ID: "
                                        + id
                        )
                );
    }

    public List<MedicalRecord> getRecordsByPatientId(
            Long patientId) {

        if (!patientRepository.existsById(patientId)) {

            throw new ResourceNotFoundException(
                    "Patient not found with ID: "
                            + patientId
            );
        }

        return medicalRecordRepository
                .findByPatientId(patientId);
    }

    public List<MedicalRecord> getRecordsByDoctorId(
            Long doctorId) {

        if (!doctorRepository.existsById(doctorId)) {

            throw new ResourceNotFoundException(
                    "Doctor not found with ID: "
                            + doctorId
            );
        }

        return medicalRecordRepository
                .findByDoctorId(doctorId);
    }

    @Transactional
    public MedicalRecord updateMedicalRecord(
            Long id,
            MedicalRecord updatedRecord) {

        if (updatedRecord == null) {

            throw new IllegalArgumentException(
                    "Medical record data is required."
            );
        }

        MedicalRecord existingRecord =
                getMedicalRecordById(id);

        validateRecordDate(
                updatedRecord.getRecordDate()
        );

        validateTextFields(updatedRecord);

        existingRecord.setDiagnosis(
                updatedRecord.getDiagnosis()
        );

        existingRecord.setPrescription(
                updatedRecord.getPrescription()
        );

        existingRecord.setTreatment(
                updatedRecord.getTreatment()
        );

        existingRecord.setNotes(
                updatedRecord.getNotes()
        );

        existingRecord.setRecordDate(
                updatedRecord.getRecordDate()
        );

        return medicalRecordRepository.save(
                existingRecord
        );
    }

    public void deleteMedicalRecord(Long id) {

        MedicalRecord record =
                getMedicalRecordById(id);

        medicalRecordRepository.delete(record);
    }

    private void validateRecordDate(
            LocalDate recordDate) {

        if (recordDate == null) {

            throw new IllegalArgumentException(
                    "Record date is required."
            );
        }

        if (recordDate.isAfter(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Medical record date cannot be in the future."
            );
        }
    }

    private void validateTextFields(
            MedicalRecord record) {

        if (record.getDiagnosis() == null
                || record.getDiagnosis().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Diagnosis is required."
            );
        }

        if (record.getTreatment() == null
                || record.getTreatment().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Treatment is required."
            );
        }

        record.setDiagnosis(
                record.getDiagnosis().trim()
        );

        record.setTreatment(
                record.getTreatment().trim()
        );

        if (record.getPrescription() != null) {

            record.setPrescription(
                    record.getPrescription().trim()
            );
        }

        if (record.getNotes() != null) {

            record.setNotes(
                    record.getNotes().trim()
            );
        }
    }
}