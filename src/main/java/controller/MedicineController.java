package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.Medicine;
import service.MedicineService;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "http://localhost:5173")
public class MedicineController {

    private final MedicineService medicineService;

    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // Add Medicine
    @PostMapping
    public ResponseEntity<Medicine> addMedicine(
            @RequestBody Medicine medicine) {

        Medicine savedMedicine = medicineService.addMedicine(medicine);

        return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
    }

    // Get All Medicines
    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {

        return ResponseEntity.ok(medicineService.getAllMedicines());
    }

    // Get Medicine By ID
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(
            @PathVariable Long id) {

        return ResponseEntity.ok(medicineService.getMedicineById(id));
    }

    // Search Medicines By Name
    @GetMapping("/search")
    public ResponseEntity<List<Medicine>> searchMedicines(
            @RequestParam String name) {

        return ResponseEntity.ok(medicineService.searchMedicines(name));
    }

    // Get Medicines By Category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Medicine>> getMedicinesByCategory(
            @PathVariable String category) {

        return ResponseEntity.ok(
                medicineService.getMedicinesByCategory(category));
    }

    // Get Available Medicines
    @GetMapping("/available")
    public ResponseEntity<List<Medicine>> getAvailableMedicines() {

        return ResponseEntity.ok(
                medicineService.getAvailableMedicines());
    }

    // Get Low Stock Medicines
    @GetMapping("/low-stock")
    public ResponseEntity<List<Medicine>> getLowStockMedicines(
            @RequestParam(defaultValue = "10") Integer threshold) {

        return ResponseEntity.ok(
                medicineService.getLowStockMedicines(threshold));
    }

    // Get Expired Medicines
    @GetMapping("/expired")
    public ResponseEntity<List<Medicine>> getExpiredMedicines() {

        return ResponseEntity.ok(
                medicineService.getExpiredMedicines());
    }

    // Update Medicine
    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(
            @PathVariable Long id,
            @RequestBody Medicine medicine) {

        return ResponseEntity.ok(
                medicineService.updateMedicine(id, medicine));
    }

    // Update Medicine Stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Medicine> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        return ResponseEntity.ok(
                medicineService.updateStock(id, quantity));
    }

    // Delete Medicine
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicine(
            @PathVariable Long id) {

        medicineService.deleteMedicine(id);

        return ResponseEntity.ok("Medicine deleted successfully");
    }
}