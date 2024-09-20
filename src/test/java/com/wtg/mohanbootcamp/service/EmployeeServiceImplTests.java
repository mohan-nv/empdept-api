package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Employee;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import com.wtg.mohanbootcamp.persistence.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeServiceImplTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    public EmployeeServiceImplTests() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllEmployees() {
        // Arrange
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setNameFirst("Galileo");
        employee1.setNameLast("Galilei");

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setNameFirst("Charles");
        employee2.setNameLast("Darwin");

        Mockito.when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        // Act
        List<Employee> employees = employeeService.getAllEmployees();

        // Assert
        assertEquals(2, employees.size());
        assertEquals("Galileo", employees.get(0).getNameFirst());
        assertEquals("Galilei", employees.get(0).getNameLast());
        assertEquals("Charles", employees.get(1).getNameFirst());
        assertEquals("Darwin", employees.get(1).getNameLast());
    }

    @Test
    void testCreateEmployee() {
        // Arrange
        Employee employee = new Employee();
        employee.setNameFirst("Carl");
        employee.setNameLast("Sagan");

        Mockito.when(employeeRepository.save(employee)).thenReturn(employee);

        // Act
        Employee createdEmployee = employeeService.createEmployee(employee);

        // Assert
        assertEquals("Carl", createdEmployee.getNameFirst());
        assertEquals("Sagan", createdEmployee.getNameLast());
    }
}
