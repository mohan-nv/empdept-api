package com.wtg.empdept_api.service;

import com.wtg.empdept_api.dao.entity.Department;
import com.wtg.empdept_api.dao.repository.DepartmentRepository;
import com.wtg.empdept_api.service.interfaces.IDepartmentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

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
        setDefaults(department);

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
        setDefaults(departmentRequest);

        if (Boolean.TRUE.equals(departmentRequest.getReadOnly()) && Boolean.TRUE.equals(existingDepartment.getReadOnly())) {
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

        departmentRepository.deleteById(id);
        return Boolean.TRUE;
    }

    private void setDefaults(Department department) {
        department.setReadOnly(Optional.ofNullable(department.getReadOnly()).orElse(false));
        department.setMandatory(Optional.ofNullable(department.getMandatory()).orElse(false));
    }
}
