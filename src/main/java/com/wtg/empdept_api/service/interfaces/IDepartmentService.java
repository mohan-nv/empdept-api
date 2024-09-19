package com.wtg.empdept_api.service.interfaces;

import com.wtg.empdept_api.dao.entity.Department;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;

import java.security.InvalidParameterException;
import java.util.List;

public interface IDepartmentService {

    List<Department> getAllDepartments();

    Department createDepartment(Department department) throws UnsupportedOperationException, InvalidParameterException, DuplicateKeyException;

    Department getDepartmentById(Long id) throws EntityNotFoundException;

    Department updateDepartment(Department departmentRequest) throws EntityNotFoundException, UnsupportedOperationException;

    Boolean deleteDepartment(Long id) throws EntityNotFoundException, UnsupportedOperationException;
}
