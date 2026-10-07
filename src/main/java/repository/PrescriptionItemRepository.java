package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.PrescriptionItem;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItem, Long> {

    List<PrescriptionItem> findByPrescriptionId(Long prescriptionId);

    List<PrescriptionItem> findByMedicineId(Long medicineId);
}