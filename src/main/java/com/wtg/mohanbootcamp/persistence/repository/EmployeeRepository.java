package com.wtg.mohanbootcamp.persistence.repository;

import com.wtg.mohanbootcamp.persistence.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
