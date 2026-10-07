package controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import entity.Department;
import service.DepartmentService;

@RestController
@RequestMapping("/api/departments")
@CrossOrigin(origins = "http://localhost:5173")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // Add Department
    @PostMapping
    public ResponseEntity<Department> addDepartment(
            @RequestBody Department department) {

        Department savedDepartment =
                departmentService.addDepartment(department);

        return new ResponseEntity<>(savedDepartment, HttpStatus.CREATED);
    }

    // Get All Departments
    @GetMapping
    public ResponseEntity<List<Department>> getAllDepartments() {

        return ResponseEntity.ok(
                departmentService.getAllDepartments());
    }

    // Get Department By ID
    @GetMapping("/{id}")
    public ResponseEntity<Department> getDepartmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                departmentService.getDepartmentById(id));
    }

    // Get Department By Name
    @GetMapping("/name/{name}")
    public ResponseEntity<Department> getDepartmentByName(
            @PathVariable String name) {

        return ResponseEntity.ok(
                departmentService.getDepartmentByName(name));
    }

    // Get Active Departments
    @GetMapping("/active")
    public ResponseEntity<List<Department>> getActiveDepartments() {

        return ResponseEntity.ok(
                departmentService.getActiveDepartments());
    }

    // Get Inactive Departments
    @GetMapping("/inactive")
    public ResponseEntity<List<Department>> getInactiveDepartments() {

        return ResponseEntity.ok(
                departmentService.getInactiveDepartments());
    }

    // Update Department
    @PutMapping("/{id}")
    public ResponseEntity<Department> updateDepartment(
            @PathVariable Long id,
            @RequestBody Department department) {

        return ResponseEntity.ok(
                departmentService.updateDepartment(id, department));
    }

    // Update Department Status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Department> updateDepartmentStatus(
            @PathVariable Long id,
            @RequestParam Boolean active) {

        return ResponseEntity.ok(
                departmentService.updateDepartmentStatus(id, active));
    }

    // Delete Department
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDepartment(
            @PathVariable Long id) {

        departmentService.deleteDepartment(id);

        return ResponseEntity.ok(
                "Department deleted successfully");
    }
}