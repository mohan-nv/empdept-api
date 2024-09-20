package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import com.wtg.mohanbootcamp.persistence.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DepartmentServiceImplTests {

    private static final long HR_ID = 1L;
    private static final long FINANCE_ID = 2L;
    private static final long MARKETING_ID = 100L;
    private static final long NO_DEPARTMENT_EXIST_ID = 200L;
    private static final String HR_NAME = "HR";
    private static final String HR_NAME_UPDATED = "HR Updated";
    private static final String FINANCE_NAME = "Finance";
    private static final String MARKETING_NAME = "Marketing";

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Mock
    private DepartmentRepository departmentRepository;

    private Department hrDepartment;
    private Department hrDepartmentUpdated;
    private Department financeDepartment;
    private Department marketingDepartmentRequest;
    private Department marketingDepartmentResponse;
    private Set<Department> departmentSet;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setupDepartments();
        setupEmployee();
    }

    private void setupDepartments() {
        hrDepartment = createDepartment(HR_ID, HR_NAME, false);
        financeDepartment = createDepartment(FINANCE_ID, FINANCE_NAME, true);
        hrDepartmentUpdated = createDepartment(HR_ID, HR_NAME_UPDATED, false);
        marketingDepartmentRequest = createDepartment(null, MARKETING_NAME, true);
        marketingDepartmentResponse = createDepartment(MARKETING_ID, MARKETING_NAME, true);

        departmentSet = new LinkedHashSet<>(List.of(hrDepartment, financeDepartment));
    }

    private void setupEmployee() {
        employee = new Employee();
        employee.setId(1L);
        employee.setDepartments(departmentSet);
        hrDepartment.setEmployees(new HashSet<>(Set.of(employee)));
    }

    private Department createDepartment(Long id, String name, boolean readOnly) {
        Department department = new Department();
        department.setId(id);
        department.setName(name);
        department.setReadOnly(readOnly);
        return department;
    }

    @Test
    public void testGetAllDepartments() {
        // Arrange
        when(departmentRepository.findAll()).thenReturn(new ArrayList<>(departmentSet));

        // Act
        List<Department> result = departmentService.getAllDepartments();

        // Assert
        assertEquals(2, result.size());
        assertDepartment(result.get(0), HR_ID, HR_NAME);
        assertDepartment(result.get(1), FINANCE_ID, FINANCE_NAME);
    }

    @Test
    public void testCreateDepartmentSuccess() {
        // Arrange
        when(departmentRepository.save(marketingDepartmentRequest)).thenReturn(marketingDepartmentResponse);

        // Act
        Department result = departmentService.createDepartment(marketingDepartmentRequest);

        // Assert
        assertDepartment(result, MARKETING_ID, MARKETING_NAME);
    }

    @Test
    public void testCreateDepartmentThrowsUnsupportedOperationException() {
        // Act & Assert
        assertUnsupportedOperationException(() -> departmentService.createDepartment(hrDepartment),
                "Please use update department if id already exist");
    }

    @Test
    public void testCreateDepartmentThrowsDuplicateKeyException() {
        // Arrange
        when(departmentRepository.save(marketingDepartmentRequest)).thenThrow(new DataIntegrityViolationException(""));

        // Act & Assert
        assertDuplicateKeyException(() -> departmentService.createDepartment(marketingDepartmentRequest),
                "Department Name should be unique");
    }

    @Test
    public void testGetDepartmentByIdSuccess() {
        // Arrange
        when(departmentRepository.findById(HR_ID)).thenReturn(Optional.of(hrDepartment));

        // Act
        Department result = departmentService.getDepartmentById(HR_ID);

        // Assert
        assertDepartment(result, HR_ID, HR_NAME);
    }

    @Test
    public void testGetDepartmentByIdThrowsEntityNotFoundException() {
        // Arrange
        when(departmentRepository.findById(NO_DEPARTMENT_EXIST_ID)).thenReturn(Optional.empty());

        // Act & Assert
        assertEntityNotFoundException(() -> departmentService.getDepartmentById(NO_DEPARTMENT_EXIST_ID), "Department Not Found");
    }

    @Test
    public void testUpdateDepartmentSuccess() {
        // Arrange
        when(departmentRepository.findById(HR_ID)).thenReturn(Optional.of(hrDepartment));
        when(departmentRepository.save(hrDepartmentUpdated)).thenReturn(hrDepartmentUpdated);

        // Act
        Department result = departmentService.updateDepartment(hrDepartmentUpdated);

        // Assert
        assertDepartment(result, HR_ID, HR_NAME_UPDATED);
    }

    @Test
    public void testUpdateDepartmentThrowsUnsupportedOperationExceptionForReadOnly() {
        // Arrange
        when(departmentRepository.findById(FINANCE_ID)).thenReturn(Optional.of(financeDepartment));

        // Act & Assert
        assertUnsupportedOperationException(() -> departmentService.updateDepartment(financeDepartment),
                "Cannot modify a readonly department");
    }

    @Test
    public void testDeleteDepartmentSuccess() {
        // Arrange
        when(departmentRepository.findById(HR_ID)).thenReturn(Optional.of(hrDepartment));

        // Act
        boolean result = departmentService.deleteDepartment(HR_ID);

        // Assert
        assertTrue(result);
    }

    @Test
    public void testDeleteDepartmentThrowsUnsupportedOperationException() {
        // Arrange
        when(departmentRepository.findById(FINANCE_ID)).thenReturn(Optional.of(financeDepartment));

        // Act & Assert
        assertUnsupportedOperationException(() -> departmentService.deleteDepartment(FINANCE_ID),
                "Cannot delete a readonly department");
    }

    // Helper Methods for Common Assertions
    private void assertDepartment(Department department, long expectedId, String expectedName) {
        assertNotNull(department);
        assertEquals(expectedId, department.getId());
        assertEquals(expectedName, department.getName());
    }

    private void assertUnsupportedOperationException(Executable executable, String expectedMessage) {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertDuplicateKeyException(Executable executable, String expectedMessage) {
        DuplicateKeyException exception = assertThrows(DuplicateKeyException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }

    private void assertEntityNotFoundException(Executable executable, String expectedMessage) {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, executable);
        assertEquals(expectedMessage, exception.getMessage());
    }
}
