package com.wtg.empdept_api.controller;

import com.wtg.empdept_api.dao.entity.Employee;
import com.wtg.empdept_api.service.interfaces.IEmployeeService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/api/v1/employee")
public class EmployeeController {

    @Autowired
    private IEmployeeService employeeService;

    @PostMapping
    public Employee createEmployee(@RequestBody @NotNull Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @GetMapping("/{id}")
    public Employee getEmployee(@PathVariable @NotNull Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping
    public Employee updateEmployee(@RequestBody @NotNull Employee employee) {
        return employeeService.updateEmployee(employee);
    }

    @DeleteMapping("/{id}")
    public Boolean deleteEmployee(@PathVariable @NotNull Long id) {
        return employeeService.deleteEmployee(id);
    }

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }
}
