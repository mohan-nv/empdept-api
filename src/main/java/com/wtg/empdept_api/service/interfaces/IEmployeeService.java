package com.wtg.empdept_api.service.interfaces;

import com.wtg.empdept_api.dao.entity.Employee;
import jakarta.persistence.EntityNotFoundException;

import java.security.InvalidParameterException;
import java.util.List;

public interface IEmployeeService {

    List<Employee> getAllEmployees();

    Employee createEmployee(Employee employee) throws UnsupportedOperationException, InvalidParameterException;

    Employee getEmployeeById(Long id) throws EntityNotFoundException;

    Employee updateEmployee(Employee employeeRequest) throws EntityNotFoundException, InvalidParameterException;

    Boolean deleteEmployee(Long id) throws EntityNotFoundException;
}
