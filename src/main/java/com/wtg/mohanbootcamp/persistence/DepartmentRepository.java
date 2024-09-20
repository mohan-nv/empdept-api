package com.wtg.mohanbootcamp.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> findByMandatory(boolean b);
}
