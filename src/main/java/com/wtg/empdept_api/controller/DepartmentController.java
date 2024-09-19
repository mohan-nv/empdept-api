package com.wtg.empdept_api.controller;

import com.wtg.empdept_api.dao.entity.Department;
import com.wtg.empdept_api.service.interfaces.IDepartmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/department")
public class DepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    @PostMapping
    public Department createDepartment(@RequestBody @Valid @NotNull Department department) {
        return departmentService.createDepartment(department);
    }

    @GetMapping
    public Department getDepartment(@RequestParam @NotNull Long id) {
        return departmentService.getDepartmentById(id);
    }

    @PutMapping
    public Department updateDepartment(@RequestBody @Valid @NotNull Department department) {
        return departmentService.updateDepartment(department);
    }

    @DeleteMapping
    public Boolean deleteDepartment(@RequestParam @NotNull Long id) {
        return departmentService.deleteDepartment(id);
    }

    @GetMapping(path = "/all")
    public List<Department> getDepartments() {
        return departmentService.getAllDepartments();
    }
}
