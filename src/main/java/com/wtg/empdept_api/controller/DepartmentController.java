package com.wtg.empdept_api.controller;

import com.wtg.empdept_api.dao.entity.Department;
import com.wtg.empdept_api.service.interfaces.IDepartmentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {

    private final IDepartmentService departmentService;

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
