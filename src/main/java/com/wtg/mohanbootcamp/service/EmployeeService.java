package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Employee;
import jakarta.persistence.EntityNotFoundException;

import java.security.InvalidParameterException;
import java.util.List;

public interface EmployeeService {

    List<Employee> getAllEmployees();

    Employee createEmployee(Employee employee) throws UnsupportedOperationException, InvalidParameterException;

    Employee getEmployeeById(Long id) throws EntityNotFoundException;

    Employee updateEmployee(Employee employeeRequest) throws EntityNotFoundException, InvalidParameterException;

    Boolean deleteEmployee(Long id) throws EntityNotFoundException;
}
