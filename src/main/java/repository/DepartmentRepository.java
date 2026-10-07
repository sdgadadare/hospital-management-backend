package repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // Find department by name
    Optional<Department> findByNameIgnoreCase(String name);

    // Check whether department name already exists
    boolean existsByNameIgnoreCase(String name);

    // Find active or inactive departments
    List<Department> findByActive(Boolean active);

}