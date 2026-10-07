package repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Medicine;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Search medicines by name
    List<Medicine> findByNameContainingIgnoreCase(String name);

    // Find medicines by category
    List<Medicine> findByCategoryIgnoreCase(String category);

    // Find medicines by availability
    List<Medicine> findByAvailable(Boolean available);

    // Find medicines with low stock
    List<Medicine> findByStockQuantityLessThan(Integer quantity);
}