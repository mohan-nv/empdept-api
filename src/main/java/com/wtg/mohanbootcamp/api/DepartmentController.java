package com.wtg.mohanbootcamp.api;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.service.DepartmentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @PostMapping
    public Department createDepartment(@RequestBody @NotNull Department department) {
        return departmentService.createDepartment(department);
    }

    @GetMapping("/{id}")
    public Department getDepartment(@PathVariable @NotNull Long id) {
        return departmentService.getDepartmentById(id);
    }

    @PutMapping
    public Department updateDepartment(@RequestBody @NotNull Department department) {
        return departmentService.updateDepartment(department);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteDepartment(@PathVariable @NotNull Long id) {
        return departmentService.deleteDepartment(id);
    }

    @GetMapping
    public List<Department> getDepartments() {
        return departmentService.getAllDepartments();
    }
}
