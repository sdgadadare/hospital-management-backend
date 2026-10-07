package service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import entity.Medicine;
import entity.Prescription;
import entity.PrescriptionItem;
import exception.ResourceNotFoundException;
import repository.MedicineRepository;
import repository.PrescriptionItemRepository;
import repository.PrescriptionRepository;

@Service
public class PrescriptionItemService {

    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final MedicineRepository medicineRepository;

    public PrescriptionItemService(
            PrescriptionItemRepository prescriptionItemRepository,
            PrescriptionRepository prescriptionRepository,
            MedicineRepository medicineRepository) {

        this.prescriptionItemRepository =
                prescriptionItemRepository;

        this.prescriptionRepository =
                prescriptionRepository;

        this.medicineRepository =
                medicineRepository;
    }

    @Transactional
    public PrescriptionItem addPrescriptionItem(
            PrescriptionItem item) {

        if (item == null) {
            throw new IllegalArgumentException(
                    "Prescription item is required."
            );
        }

        if (item.getPrescription() == null
                || item.getPrescription().getId() == null) {

            throw new IllegalArgumentException(
                    "Prescription is required."
            );
        }

        if (item.getMedicine() == null
                || item.getMedicine().getId() == null) {

            throw new IllegalArgumentException(
                    "Medicine is required."
            );
        }

        validateItem(item);

        Long prescriptionId =
                item.getPrescription().getId();

        Long medicineId =
                item.getMedicine().getId();

        Prescription prescription =
                prescriptionRepository.findById(
                        prescriptionId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Prescription not found with ID: "
                                        + prescriptionId
                        )
                );

        Medicine medicine =
                medicineRepository.findById(
                        medicineId
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Medicine not found with ID: "
                                        + medicineId
                        )
                );

        if (!Boolean.TRUE.equals(
                medicine.getAvailable())) {

            throw new IllegalArgumentException(
                    "Medicine is currently unavailable."
            );
        }

        Integer stock =
                medicine.getStockQuantity();

        if (stock == null || stock < 0) {
            throw new IllegalArgumentException(
                    "Invalid medicine stock."
            );
        }

        if (stock < item.getQuantity()) {

            throw new IllegalArgumentException(
                    "Insufficient medicine stock. Available: "
                            + stock
            );
        }

        medicine.setStockQuantity(
                stock - item.getQuantity()
        );

        medicine.setAvailable(
                medicine.getStockQuantity() > 0
        );

        medicineRepository.save(medicine);

        item.setPrescription(prescription);
        item.setMedicine(medicine);

        return prescriptionItemRepository.save(item);
    }

    public List<PrescriptionItem> getAllPrescriptionItems() {
        return prescriptionItemRepository.findAll();
    }

    public PrescriptionItem getPrescriptionItemById(
            Long id) {

        return prescriptionItemRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Prescription item not found with ID: "
                                        + id
                        )
                );
    }

    public List<PrescriptionItem> getItemsByPrescription(
            Long prescriptionId) {

        if (!prescriptionRepository
                .existsById(prescriptionId)) {

            throw new ResourceNotFoundException(
                    "Prescription not found with ID: "
                            + prescriptionId
            );
        }

        return prescriptionItemRepository
                .findByPrescriptionId(prescriptionId);
    }

    public List<PrescriptionItem> getItemsByMedicine(
            Long medicineId) {

        if (!medicineRepository.existsById(medicineId)) {

            throw new ResourceNotFoundException(
                    "Medicine not found with ID: "
                            + medicineId
            );
        }

        return prescriptionItemRepository
                .findByMedicineId(medicineId);
    }

    @Transactional
    public PrescriptionItem updatePrescriptionItem(
            Long id,
            PrescriptionItem updatedItem) {

        if (updatedItem == null) {

            throw new IllegalArgumentException(
                    "Prescription item is required."
            );
        }

        validateItem(updatedItem);

        PrescriptionItem existingItem =
                getPrescriptionItemById(id);

        int oldQuantity =
                existingItem.getQuantity();

        int newQuantity =
                updatedItem.getQuantity();

        int quantityDifference =
                newQuantity - oldQuantity;

        Medicine medicine =
                existingItem.getMedicine();

        if (quantityDifference > 0) {

            Integer stock =
                    medicine.getStockQuantity();

            if (stock == null || stock < quantityDifference) {

                throw new IllegalArgumentException(
                        "Insufficient medicine stock for quantity increase."
                );
            }

            medicine.setStockQuantity(
                    stock - quantityDifference
            );

        } else if (quantityDifference < 0) {

            int restoredQuantity =
                    Math.abs(quantityDifference);

            Integer stock =
                    medicine.getStockQuantity() == null
                            ? 0
                            : medicine.getStockQuantity();

            medicine.setStockQuantity(
                    stock + restoredQuantity
            );
        }

        medicine.setAvailable(
                medicine.getStockQuantity() > 0
        );

        medicineRepository.save(medicine);

        existingItem.setDosage(
                updatedItem.getDosage()
        );

        existingItem.setFrequency(
                updatedItem.getFrequency()
        );

        existingItem.setDurationDays(
                updatedItem.getDurationDays()
        );

        existingItem.setQuantity(
                newQuantity
        );

        existingItem.setInstructions(
                updatedItem.getInstructions()
        );

        return prescriptionItemRepository.save(
                existingItem
        );
    }

    @Transactional
    public void deletePrescriptionItem(Long id) {

        PrescriptionItem item =
                getPrescriptionItemById(id);

        Medicine medicine =
                item.getMedicine();

        if (medicine != null) {

            Integer stock =
                    medicine.getStockQuantity() == null
                            ? 0
                            : medicine.getStockQuantity();

            int restoredQuantity =
                    item.getQuantity() == null
                            ? 0
                            : item.getQuantity();

            medicine.setStockQuantity(
                    stock + restoredQuantity
            );

            medicine.setAvailable(
                    medicine.getStockQuantity() > 0
            );

            medicineRepository.save(medicine);
        }

        prescriptionItemRepository.delete(item);
    }

    private void validateItem(
            PrescriptionItem item) {

        if (item.getDosage() == null
                || item.getDosage().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Dosage is required."
            );
        }

        if (item.getFrequency() == null
                || item.getFrequency().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Frequency is required."
            );
        }

        if (item.getDurationDays() == null
                || item.getDurationDays() < 1) {

            throw new IllegalArgumentException(
                    "Duration must be at least one day."
            );
        }

        if (item.getQuantity() == null
                || item.getQuantity() < 1) {

            throw new IllegalArgumentException(
                    "Quantity must be at least one."
            );
        }

        item.setDosage(
                item.getDosage().trim()
        );

        item.setFrequency(
                item.getFrequency().trim()
        );

        if (item.getInstructions() != null) {
            item.setInstructions(
                    item.getInstructions().trim()
            );
        }
    }
}