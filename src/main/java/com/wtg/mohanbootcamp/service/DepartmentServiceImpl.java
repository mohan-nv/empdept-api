package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.Employee;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;

    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }

    @Override
    public Department createDepartment(Department department) throws UnsupportedOperationException, InvalidParameterException, DuplicateKeyException {
        if (department.getId() != null) {
            throw new UnsupportedOperationException("Please use update department if id already exist");
        }
        if (department.getName() == null || department.getName().isEmpty()) {
            throw new InvalidParameterException("Department Name can't be null or empty");
        }
        if (department.getReadOnly() == null) {
            department.setReadOnly(Boolean.FALSE);
        }

        try {
            return departmentRepository.save(department);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Department Name should be unique");
        }
    }

    @Override
    public Department getDepartmentById(Long id) throws EntityNotFoundException {
        return departmentRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Department Not Found"));
    }

    @Override
    public Department updateDepartment(Department departmentRequest) throws EntityNotFoundException, UnsupportedOperationException {
        Department existingDepartment = getDepartmentById(departmentRequest.getId());

        if (existingDepartment.getReadOnly() && departmentRequest.getReadOnly() == null) {
            throw new UnsupportedOperationException("Cannot modify a readonly department");
        }

        if (departmentRequest.getReadOnly() == null) {
            departmentRequest.setReadOnly(Boolean.FALSE);
        }

        if (departmentRequest.getReadOnly() && existingDepartment.getReadOnly()) {
            throw new UnsupportedOperationException("Cannot modify a readonly department");
        }

        try {
            return departmentRepository.save(departmentRequest);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateKeyException("Department Name should be unique");
        }
    }

    @Override
    public Boolean deleteDepartment(Long id) throws EntityNotFoundException, UnsupportedOperationException {
        Department department = getDepartmentById(id);

        if (Boolean.TRUE.equals(department.getReadOnly())) {
            throw new UnsupportedOperationException("Cannot delete a readonly department");
        }

        for (Employee employee : department.getEmployees()) {
            employee.getDepartments().remove(department);
        }

        departmentRepository.deleteById(id);
        return Boolean.TRUE;
    }
}
