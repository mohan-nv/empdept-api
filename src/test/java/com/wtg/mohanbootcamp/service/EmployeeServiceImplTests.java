package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import com.wtg.mohanbootcamp.persistence.Employee;
import com.wtg.mohanbootcamp.persistence.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmployeeServiceImplTests {

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private Employee employee;
    private Department hrDepartment;
    private Department financeDepartment;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Departments
        hrDepartment = new Department();
        hrDepartment.setId(1L);
        hrDepartment.setName("HR");
        hrDepartment.setMandatory(true);

        financeDepartment = new Department();
        financeDepartment.setId(2L);
        financeDepartment.setName("Finance");
        financeDepartment.setMandatory(false);

        Set<Department> departments = new HashSet<>();
        departments.add(hrDepartment);

        // Initialize Employee
        employee = new Employee();
        employee.setId(1L);
        employee.setNameFirst("John");
        employee.setNameLast("Doe");
        employee.setDepartments(departments);
    }

    @Test
    public void testGetAllEmployees() {
        // Arrange
        when(employeeRepository.findAll()).thenReturn(List.of(employee));

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertEquals(1, result.size());
        assertEquals("John", result.get(0).getNameFirst());
    }

    @Test
    public void testCreateEmployeeSuccess() {
        // Arrange
        Employee newEmployee = new Employee();
        newEmployee.setNameFirst("Jane");
        newEmployee.setNameLast("Smith");

        when(departmentRepository.findByMandatory(true)).thenReturn(List.of(hrDepartment));
        when(employeeRepository.save(newEmployee)).thenReturn(newEmployee);

        // Act
        Employee result = employeeService.createEmployee(newEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("Jane", result.getNameFirst());
        assertTrue(result.getDepartments().contains(hrDepartment));  // Mandatory department is added
    }

    @Test
    public void testCreateEmployeeThrowsUnsupportedOperationException() {
        // Arrange
        employee.setId(2L);

        // Act & Assert
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            employeeService.createEmployee(employee);
        });

        assertEquals("Please use update employee if id already exists", exception.getMessage());
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        // Act
        Employee result = employeeService.getEmployeeById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getNameFirst());
    }

    @Test
    public void testGetEmployeeByIdThrowsEntityNotFoundException() {
        // Arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeService.getEmployeeById(1L);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }

    @Test
    public void testUpdateEmployeeSuccess() {
        // Arrange
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setNameFirst("John");
        updatedEmployee.setNameLast("Doe");
        updatedEmployee.setDepartments(new HashSet<>(Set.of(financeDepartment)));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findAllById(Set.of(2L))).thenReturn(List.of(financeDepartment));
        when(departmentRepository.findByMandatory(true)).thenReturn(List.of(hrDepartment));
        when(employeeRepository.save(updatedEmployee)).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(updatedEmployee);

        // Assert
        assertNotNull(result);
        assertEquals("John", result.getNameFirst());
        assertTrue(result.getDepartments().contains(hrDepartment));  // Mandatory department should still be present
    }

    @Test
    public void testDeleteEmployeeSuccess() {
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = employeeService.deleteEmployee(1L);

        // Assert
        assertTrue(result);
        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteEmployeeThrowsEntityNotFoundException() {
        // Arrange
        when(employeeRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            employeeService.deleteEmployee(1L);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }

    @Test
    public void testCreateEmployeeThrowsInvalidParameterExceptionForEmptyFirstName() {
        // Arrange
        Employee invalidEmployee = new Employee();
        invalidEmployee.setNameFirst("");
        invalidEmployee.setNameLast("Doe");

        // Act & Assert
        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
            employeeService.createEmployee(invalidEmployee);
        });

        assertEquals("First Name can't be null or empty", exception.getMessage());
    }
}
