package com.wtg.empdept_api.service;

import com.wtg.empdept_api.dao.entity.Department;
import com.wtg.empdept_api.dao.entity.Employee;
import com.wtg.empdept_api.dao.repository.DepartmentRepository;
import com.wtg.empdept_api.dao.repository.EmployeeRepository;
import com.wtg.empdept_api.service.interfaces.IEmployeeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService implements IEmployeeService {

    private final EmployeeRepository employeeRepository;

    private final DepartmentRepository departmentRepository;

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
        validateDepartmentIds(employee.getDepartments());

        addMandatoryDepartments(employee);

        return employeeRepository.save(employee);
    }

    @Override
    public Employee getEmployeeById(Long id) throws EntityNotFoundException {
        return employeeRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Employee Not Found"));
    }

    @Override
    public Employee updateEmployee(Employee employeeRequest) throws EntityNotFoundException, InvalidParameterException {
        validateEmployeeNames(employeeRequest);
        validateEmployeeExist(employeeRequest.getId());
        validateDepartmentIds(employeeRequest.getDepartments());

        addMandatoryDepartments(employeeRequest);

        return employeeRepository.save(employeeRequest);
    }

    @Override
    public Boolean deleteEmployee(Long id) throws EntityNotFoundException {
        validateEmployeeExist(id);
        employeeRepository.deleteById(id);
        return Boolean.TRUE;
    }

    private void validateEmployeeExist(Long id) throws EntityNotFoundException {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee Not Found");
        }
    }

    private void validateEmployeeNames(Employee employee) throws InvalidParameterException {
        if (employee.getNameFirst() == null || employee.getNameFirst().isEmpty()) {
            throw new InvalidParameterException("First Name can't be null or empty");
        }
        if (employee.getNameLast() == null || employee.getNameLast().isEmpty()) {
            throw new InvalidParameterException("Last Name can't be null or empty");
        }
    }

    private void validateDepartmentIds(Set<Department> departmentList) throws DuplicateKeyException, EntityNotFoundException {
        if (Objects.isNull(departmentList)) {
            return;
        }
        Set<Long> departmentIdSet = departmentList.stream().map(Department::getId).collect(Collectors.toSet());
        if (departmentList.size() != departmentIdSet.size()) {
            throw new DuplicateKeyException("Department ids are duplicated");
        }
        List<Department> result = departmentRepository.findAllById(departmentIdSet);
        if (departmentList.size() != result.size()) {
            throw new EntityNotFoundException("One or more departments not found");
        }
    }

    private void addMandatoryDepartments(Employee employee) {
        List<Department> mandatoryDepartmentList = departmentRepository.findByMandatory(Boolean.TRUE);
        if (employee.getDepartments() == null) {
            employee.setDepartments(new HashSet<>());
        }

        Set<Long> requestDepartmentIdList = employee.getDepartments().stream().map(Department::getId).collect(Collectors.toSet());
        for (Department mandatoryDepartment : mandatoryDepartmentList) {
            if (!requestDepartmentIdList.contains(mandatoryDepartment.getId())) {
                employee.getDepartments().add(mandatoryDepartment);
            }
        }
    }
}
