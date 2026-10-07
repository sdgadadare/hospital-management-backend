
package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.PrescriptionItem;
import service.PrescriptionItemService;

@RestController
@RequestMapping("/api/prescription-items")
@CrossOrigin(origins = "http://localhost:5173")
public class PrescriptionItemController {

    private final PrescriptionItemService prescriptionItemService;

    public PrescriptionItemController(
            PrescriptionItemService prescriptionItemService) {
        this.prescriptionItemService = prescriptionItemService;
    }

    @PostMapping
    public ResponseEntity<PrescriptionItem> addPrescriptionItem(
            @RequestBody PrescriptionItem item) {

        PrescriptionItem saved =
                prescriptionItemService.addPrescriptionItem(item);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<PrescriptionItem>> getAllPrescriptionItems() {
        return ResponseEntity.ok(
                prescriptionItemService.getAllPrescriptionItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionItem> getPrescriptionItemById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                prescriptionItemService.getPrescriptionItemById(id));
    }

    @GetMapping("/prescription/{prescriptionId}")
    public ResponseEntity<List<PrescriptionItem>> getItemsByPrescription(
            @PathVariable Long prescriptionId) {

        return ResponseEntity.ok(
                prescriptionItemService.getItemsByPrescription(prescriptionId));
    }

    @GetMapping("/medicine/{medicineId}")
    public ResponseEntity<List<PrescriptionItem>> getItemsByMedicine(
            @PathVariable Long medicineId) {

        return ResponseEntity.ok(
                prescriptionItemService.getItemsByMedicine(medicineId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionItem> updatePrescriptionItem(
            @PathVariable Long id,
            @RequestBody PrescriptionItem item) {

        return ResponseEntity.ok(
                prescriptionItemService.updatePrescriptionItem(id, item));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescriptionItem(
            @PathVariable Long id) {

        prescriptionItemService.deletePrescriptionItem(id);

        return ResponseEntity.noContent().build();
    }
}
