package com.wtg.empdept_api.service;

import com.wtg.empdept_api.dao.entity.Employee;
import com.wtg.empdept_api.dao.repository.EmployeeRepository;
import com.wtg.empdept_api.service.interfaces.IEmployeeService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;

@Service
public class EmployeeService implements IEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee createEmployee(Employee employee) throws UnsupportedOperationException, InvalidParameterException {
        if (employee.getId() != null) {
            throw new UnsupportedOperationException("Please use update employee if id already exists");
        }
        validateEmployeeNames(employee);
        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) throws EntityNotFoundException {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee Not Found"));
    }

    @Override
    public Employee updateEmployee(Employee employeeRequest) throws EntityNotFoundException, InvalidParameterException {
        validateEmployeeNames(employeeRequest);
        // To ensure employee exists
        getEmployeeById(employeeRequest.getId());
        return employeeRepository.save(employeeRequest);
    }

    @Override
    public Boolean deleteEmployee(Long id) throws EntityNotFoundException {
        // To ensure employee exists
        getEmployeeById(id);

        employeeRepository.deleteById(id);
        return Boolean.TRUE;
    }

    private void validateEmployeeNames(Employee employee) throws InvalidParameterException {
        if (employee.getNameFirst() == null || employee.getNameFirst().isEmpty()) {
            throw new InvalidParameterException("First Name can't be null or empty");
        }
        if (employee.getNameLast() == null || employee.getNameLast().isEmpty()) {
            throw new InvalidParameterException("Last Name can't be null or empty");
        }
    }
}
