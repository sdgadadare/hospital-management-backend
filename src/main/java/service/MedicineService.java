package service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import entity.Medicine;
import repository.MedicineRepository;
import exception.ResourceNotFoundException;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    // Add Medicine
    public Medicine addMedicine(Medicine medicine) {

        medicine.setAvailable(
                medicine.getStockQuantity() != null
                        && medicine.getStockQuantity() > 0
        );

        return medicineRepository.save(medicine);
    }

    // Get All Medicines
    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    // Get Medicine By ID
    public Medicine getMedicineById(Long id) {
        return medicineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Medicine not found with ID: " + id
                ));
    }

    // Search Medicines By Name
    public List<Medicine> searchMedicines(String name) {
        return medicineRepository.findByNameContainingIgnoreCase(name);
    }

    // Get Medicines By Category
    public List<Medicine> getMedicinesByCategory(String category) {
        return medicineRepository.findByCategoryIgnoreCase(category);
    }

    // Get Available Medicines
    public List<Medicine> getAvailableMedicines() {
        return medicineRepository.findByAvailable(true);
    }

    // Get Low Stock Medicines
    public List<Medicine> getLowStockMedicines(Integer threshold) {
        return medicineRepository.findByStockQuantityLessThan(threshold);
    }

    // Get Expired Medicines
    public List<Medicine> getExpiredMedicines() {

        return medicineRepository.findAll()
                .stream()
                .filter(medicine ->
                        medicine.getExpiryDate() != null
                                && medicine.getExpiryDate().isBefore(LocalDate.now()))
                .toList();
    }

    // Update Medicine
    public Medicine updateMedicine(Long id, Medicine updatedMedicine) {

        Medicine existingMedicine = getMedicineById(id);

        existingMedicine.setName(updatedMedicine.getName());
        existingMedicine.setCategory(updatedMedicine.getCategory());
        existingMedicine.setManufacturer(updatedMedicine.getManufacturer());
        existingMedicine.setBatchNumber(updatedMedicine.getBatchNumber());
        existingMedicine.setPrice(updatedMedicine.getPrice());
        existingMedicine.setStockQuantity(updatedMedicine.getStockQuantity());
        existingMedicine.setExpiryDate(updatedMedicine.getExpiryDate());
        existingMedicine.setManufacturingDate(updatedMedicine.getManufacturingDate());
        existingMedicine.setDescription(updatedMedicine.getDescription());

        existingMedicine.setAvailable(
                updatedMedicine.getStockQuantity() != null
                        && updatedMedicine.getStockQuantity() > 0
        );

        return medicineRepository.save(existingMedicine);
    }

    // Update Stock
    public Medicine updateStock(Long id, Integer quantity) {

        Medicine medicine = getMedicineById(id);

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(
                    "Stock quantity cannot be negative");
        }

        medicine.setStockQuantity(quantity);
        medicine.setAvailable(quantity > 0);

        return medicineRepository.save(medicine);
    }

    // Delete Medicine
    public void deleteMedicine(Long id) {

        Medicine medicine = getMedicineById(id);

        medicineRepository.delete(medicine);
    }
}