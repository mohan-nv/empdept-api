package com.wtg.empdept_api.dao.repository;

import com.wtg.empdept_api.dao.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByMandatory(boolean b);
}
