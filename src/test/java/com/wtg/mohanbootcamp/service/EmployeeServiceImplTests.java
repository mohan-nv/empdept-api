package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import com.wtg.mohanbootcamp.persistence.Employee;
import com.wtg.mohanbootcamp.persistence.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.InvalidParameterException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTests {

    public static final Long ID_EMPLOYEE_EXISTING = 1L;
    public static final Long ID_EMPLOYEE_NON_EXISTING = 100L;
    public static final Long ID_DEPARTMENT_MANDATORY = 1L;
    public static final Long ID_DEPARTMENT_NON_MANDATORY = 2L;
    public static final String NAME_FIRST_EMPLOYEE_EXISTING = "Carl";
    public static final String NAME_LAST_EMPLOYEE_EXISTING = "Sagan";
    public static final String NAME_FIRST_NEW_EMPLOYEE = "CV";
    public static final String NAME_LAST_NEW_EMPLOYEE = "Raman";

    private EmployeeService ref;

    @InjectMocks
    private EmployeeServiceImpl concreteRef;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    private Employee existingEmployee;
    private Employee newEmployeeRequest;
    private Employee existingEmployeeUpdateRequest;
    private Department mandatoryDepartment;
    private Department nonMandatoryDepartment;

    @BeforeEach
    public void setUp() {
        ref = concreteRef;
        setupDepartments();
        setupEmployees();
    }

    @AfterEach
    public void tearDown() {
        ref = null;
        concreteRef = null;

        existingEmployee = null;
        newEmployeeRequest = null;
        existingEmployeeUpdateRequest = null;
        mandatoryDepartment = null;
        nonMandatoryDepartment = null;
    }

    private void setupDepartments() {
        mandatoryDepartment = Department.builder().id(ID_DEPARTMENT_MANDATORY).name("HR").readOnly(Boolean.FALSE).mandatory(Boolean.TRUE).build();
        nonMandatoryDepartment = Department.builder().id(ID_DEPARTMENT_NON_MANDATORY).name("Finance").readOnly(Boolean.TRUE).mandatory(Boolean.FALSE).build();
    }

    private void setupEmployees() {
        existingEmployee = Employee.builder().id(ID_EMPLOYEE_EXISTING).nameFirst(NAME_FIRST_EMPLOYEE_EXISTING).nameLast(NAME_LAST_EMPLOYEE_EXISTING).departments(Set.of(mandatoryDepartment)).build();
        Set<Department> nonMandatoryDepartments = new HashSet<>();
        nonMandatoryDepartments.add(nonMandatoryDepartment);
        existingEmployeeUpdateRequest = Employee.builder().id(ID_EMPLOYEE_EXISTING).nameFirst(NAME_FIRST_EMPLOYEE_EXISTING).nameLast(NAME_LAST_EMPLOYEE_EXISTING).departments(nonMandatoryDepartments).build();
        newEmployeeRequest = Employee.builder().nameFirst(NAME_FIRST_NEW_EMPLOYEE).nameLast(NAME_LAST_NEW_EMPLOYEE).build();
    }

    @Test
    public void testGetAllEmployees() {
        when(employeeRepository.findAll()).thenReturn(List.of(existingEmployee));

        List<Employee> result = ref.getAllEmployees();

        assertEquals(1, result.size());
        assertEquals(NAME_FIRST_EMPLOYEE_EXISTING, result.get(0).getNameFirst());
    }

    @Test
    public void testCreateEmployeeSuccess() {
        when(departmentRepository.findByMandatory(true)).thenReturn(List.of(mandatoryDepartment));
        when(employeeRepository.save(newEmployeeRequest)).thenReturn(newEmployeeRequest);

        Employee result = ref.createEmployee(newEmployeeRequest);

        assertNotNull(result);
        assertEquals(NAME_FIRST_NEW_EMPLOYEE, result.getNameFirst());
        assertTrue(result.getDepartments().contains(mandatoryDepartment));  // Mandatory department is added
    }

    @Test
    public void testCreateEmployeeThrowsUnsupportedOperationException() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            ref.createEmployee(existingEmployee);
        });

        assertEquals("Please use update employee if id already exists", exception.getMessage());
    }

    @Test
    public void testGetEmployeeByIdSuccess() {
        when(employeeRepository.findById(ID_EMPLOYEE_EXISTING)).thenReturn(Optional.of(existingEmployee));

        Employee result = ref.getEmployeeById(ID_EMPLOYEE_EXISTING);

        assertNotNull(result);
        assertEquals(NAME_FIRST_EMPLOYEE_EXISTING, result.getNameFirst());
    }

    @Test
    public void testGetEmployeeByIdThrowsEntityNotFoundException() {
        when(employeeRepository.findById(ID_EMPLOYEE_NON_EXISTING)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ref.getEmployeeById(ID_EMPLOYEE_NON_EXISTING);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }

    @Test
    public void testUpdateEmployeeSuccess() {
        when(employeeRepository.findById(ID_EMPLOYEE_EXISTING)).thenReturn(Optional.of(existingEmployee));
        when(departmentRepository.findAllById(Set.of(ID_DEPARTMENT_NON_MANDATORY))).thenReturn(List.of(nonMandatoryDepartment));
        when(departmentRepository.findByMandatory(true)).thenReturn(List.of(mandatoryDepartment));
        when(employeeRepository.save(existingEmployeeUpdateRequest)).thenReturn(existingEmployeeUpdateRequest);

        Employee result = ref.updateEmployee(existingEmployeeUpdateRequest);

        assertNotNull(result);
        assertEquals(NAME_FIRST_EMPLOYEE_EXISTING, result.getNameFirst());
        assertTrue(result.getDepartments().contains(nonMandatoryDepartment));  // New department in request should be present
        assertTrue(result.getDepartments().contains(mandatoryDepartment));  // Mandatory department should still be present
    }

    @Test
    public void testDeleteEmployeeSuccess() {
        when(employeeRepository.existsById(ID_EMPLOYEE_EXISTING)).thenReturn(Boolean.TRUE);

        boolean result = ref.deleteEmployee(ID_EMPLOYEE_EXISTING);

        assertTrue(result);
        verify(employeeRepository, times(1)).deleteById(ID_EMPLOYEE_EXISTING);
    }

    @Test
    public void testDeleteEmployeeThrowsEntityNotFoundException() {
        when(employeeRepository.existsById(ID_EMPLOYEE_NON_EXISTING)).thenReturn(Boolean.FALSE);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ref.deleteEmployee(ID_EMPLOYEE_NON_EXISTING);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }

    @Test
    public void testCreateEmployeeThrowsInvalidParameterExceptionForEmptyFirstName() {
        newEmployeeRequest.setNameFirst("");

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
            ref.createEmployee(newEmployeeRequest);
        });

        assertEquals("First Name can't be null or empty", exception.getMessage());
    }
}
