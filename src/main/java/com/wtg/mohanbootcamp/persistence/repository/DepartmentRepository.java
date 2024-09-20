package com.wtg.mohanbootcamp.persistence.repository;

import com.wtg.mohanbootcamp.persistence.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByMandatory(boolean b);
}
