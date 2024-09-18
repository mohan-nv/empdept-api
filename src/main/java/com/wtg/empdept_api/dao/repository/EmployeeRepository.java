package com.wtg.empdept_api.dao.repository;

import com.wtg.empdept_api.dao.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
