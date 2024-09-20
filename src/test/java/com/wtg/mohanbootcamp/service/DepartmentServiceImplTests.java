package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DepartmentServiceImplTests {
    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    public DepartmentServiceImplTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllDepartments() {
        // Arrange
        Department department1 = new Department();
        department1.setId(1L);
        department1.setName("Finance");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("HR");

        Mockito.when(departmentRepository.findAll()).thenReturn(Arrays.asList(department1, department2));

        // Act
        List<Department> departments = departmentService.getAllDepartments();

        // Assert
        assertEquals(2, departments.size());
        assertEquals("Finance", departments.get(0).getName());
        assertEquals("HR", departments.get(1).getName());
    }

    @Test
    void testCreateDepartment() {
        // Arrange
        Department department = new Department();
        department.setName("IT");
        department.setReadOnly(false);

        Mockito.when(departmentRepository.save(department)).thenReturn(department);

        // Act
        Department createdDepartment = departmentService.createDepartment(department);

        // Assert
        assertEquals("IT", createdDepartment.getName());
    }
}
