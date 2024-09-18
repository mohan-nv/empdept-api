package com.wtg.empdept_api.dao.repository;

import com.wtg.empdept_api.dao.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
