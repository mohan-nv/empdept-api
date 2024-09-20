package com.wtg.mohanbootcamp.api;

import com.wtg.mohanbootcamp.persistence.Employee;
import com.wtg.mohanbootcamp.service.EmployeeService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

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
