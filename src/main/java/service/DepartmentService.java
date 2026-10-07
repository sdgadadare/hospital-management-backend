package service;

import java.util.List;

import org.springframework.stereotype.Service;

import entity.Department;
import repository.DepartmentRepository;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    // Add Department
    public Department addDepartment(Department department) {

        if (departmentRepository.existsByNameIgnoreCase(department.getName())) {
            throw new RuntimeException("Department already exists");
        }

        department.setActive(true);

        return departmentRepository.save(department);
    }

    // Get All Departments
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    // Get Department By ID
    public Department getDepartmentById(Long id) {

        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    // Get Department By Name
    public Department getDepartmentByName(String name) {

        return departmentRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    // Get Active Departments
    public List<Department> getActiveDepartments() {
        return departmentRepository.findByActive(true);
    }

    // Get Inactive Departments
    public List<Department> getInactiveDepartments() {
        return departmentRepository.findByActive(false);
    }

    // Update Department
    public Department updateDepartment(Long id, Department updatedDepartment) {

        Department existingDepartment = getDepartmentById(id);

        if (!existingDepartment.getName().equalsIgnoreCase(updatedDepartment.getName())
                && departmentRepository.existsByNameIgnoreCase(updatedDepartment.getName())) {

            throw new RuntimeException("Department name already exists");
        }

        existingDepartment.setName(updatedDepartment.getName());
        existingDepartment.setDescription(updatedDepartment.getDescription());
        existingDepartment.setLocation(updatedDepartment.getLocation());

        return departmentRepository.save(existingDepartment);
    }

    // Update Department Status
    public Department updateDepartmentStatus(Long id, Boolean active) {

        Department department = getDepartmentById(id);

        department.setActive(active);

        return departmentRepository.save(department);
    }

    // Delete Department
    public void deleteDepartment(Long id) {

        Department department = getDepartmentById(id);

        departmentRepository.delete(department);
    }
}