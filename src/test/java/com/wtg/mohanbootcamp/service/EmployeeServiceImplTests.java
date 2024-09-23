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

import static com.wtg.mohanbootcamp.service.DepartmentServiceImplTests.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTests {

    public static final Long ID_EMPLOYEE_EXISTING = 1L;
    public static final Long ID_EMPLOYEE_NON_EXISTING = 100L;
    public static final String NAME_FIRST_EMPLOYEE_EXISTING = "NAME_FIRST_EMPLOYEE_EXISTING";
    public static final String NAME_FIRST_EMPLOYEE_EXISTING_UPDATED = "NAME_FIRST_EMPLOYEE_EXISTING_UPDATED";
    public static final String NAME_LAST_EMPLOYEE_EXISTING = "NAME_LAST_EMPLOYEE_EXISTING";
    public static final String NAME_FIRST_EMPLOYEE_NEW = "NAME_FIRST_EMPLOYEE_NEW";
    public static final String NAME_LAST_EMPLOYEE_NEW = "NAME_LAST_EMPLOYEE_NEW";

    private EmployeeService ref;

    @InjectMocks
    private EmployeeServiceImpl concreteRef;

    @Mock
    private EmployeeRepository mockEmployeeRepository;

    @Mock
    private DepartmentRepository mockDepartmentRepository;

    private Employee employeeExisting;
    private Employee employeeCreateRequest;
    private Employee employeeExistingUpdateRequest;
    private Department departmentMandatory;
    private Department departmentNonMandatory;

    @BeforeEach
    public void setUp() {
        ref = concreteRef;
        setupDepartments();
        setupEmployees();

        lenient().when(mockEmployeeRepository.findAll()).thenReturn(List.of(employeeExisting));
        lenient().when(mockEmployeeRepository.findById(ID_EMPLOYEE_EXISTING)).thenReturn(Optional.of(employeeExisting));
        lenient().when(mockEmployeeRepository.findById(ID_EMPLOYEE_NON_EXISTING)).thenReturn(Optional.empty());
        lenient().when(mockEmployeeRepository.existsById(ID_EMPLOYEE_EXISTING)).thenReturn(Boolean.TRUE);
        lenient().when(mockEmployeeRepository.existsById(ID_EMPLOYEE_NON_EXISTING)).thenReturn(Boolean.FALSE);
        lenient().when(mockEmployeeRepository.save(employeeCreateRequest)).thenReturn(employeeCreateRequest);
        lenient().when(mockEmployeeRepository.save(employeeExistingUpdateRequest)).thenReturn(employeeExistingUpdateRequest);

        lenient().when(mockDepartmentRepository.findByMandatory(Boolean.TRUE)).thenReturn(List.of(departmentMandatory));
        lenient().when(mockDepartmentRepository.findAllById(Set.of(ID_DEPARTMENT_READONLY_NON_MANDATORY))).thenReturn(List.of(departmentNonMandatory));

    }

    @AfterEach
    public void tearDown() {
        ref = null;
        concreteRef = null;

        employeeExisting = null;
        employeeCreateRequest = null;
        employeeExistingUpdateRequest = null;
        departmentMandatory = null;
        departmentNonMandatory = null;
    }

    private void setupDepartments() {
        departmentMandatory = Department.builder().id(ID_DEPARTMENT_NOT_READONLY_MANDATORY).name(NAME_DEPARTMENT_NOT_READONLY_MANDATORY).readOnly(Boolean.FALSE).mandatory(Boolean.TRUE).build();
        departmentNonMandatory = Department.builder().id(ID_DEPARTMENT_READONLY_NON_MANDATORY).name(NAME_DEPARTMENT_READONLY_NON_MANDATORY).readOnly(Boolean.TRUE).mandatory(Boolean.FALSE).build();
    }

    private void setupEmployees() {
        employeeExisting = Employee.builder().id(ID_EMPLOYEE_EXISTING).nameFirst(NAME_FIRST_EMPLOYEE_EXISTING).nameLast(NAME_LAST_EMPLOYEE_EXISTING).departments(new HashSet<>(Set.of(departmentMandatory))).build();
        employeeExistingUpdateRequest = Employee.builder().id(ID_EMPLOYEE_EXISTING).nameFirst(NAME_FIRST_EMPLOYEE_EXISTING_UPDATED).nameLast(NAME_LAST_EMPLOYEE_EXISTING).departments(new HashSet<>(Set.of(departmentNonMandatory))).build();
        employeeCreateRequest = Employee.builder().nameFirst(NAME_FIRST_EMPLOYEE_NEW).nameLast(NAME_LAST_EMPLOYEE_NEW).build();
    }

    @Test
    public void testGetAllEmployees_success() {
        List<Employee> result = ref.getAllEmployees();

        assertEquals(1, result.size());
        assertTrue(result.contains(employeeExisting));
    }

    @Test
    public void testCreateEmployee_validInput_noDepartmentsInRequest_success() {
        Employee result = ref.createEmployee(employeeCreateRequest);

        assertNotNull(result);
        assertEquals(NAME_FIRST_EMPLOYEE_NEW, result.getNameFirst());
        assertTrue(result.getDepartments().contains(departmentMandatory));  // Mandatory department is added
    }

    @Test
    public void testCreateEmployee_employeeIdNotNull_exception() {
        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            ref.createEmployee(employeeExisting);
        });

        assertEquals("Please use update employee if id already exists", exception.getMessage());
    }

    @Test
    public void testCreateEmployee_emptyFirstName_exception() {
        employeeCreateRequest.setNameFirst("");

        InvalidParameterException exception = assertThrows(InvalidParameterException.class, () -> {
            ref.createEmployee(employeeCreateRequest);
        });

        assertEquals("First Name can't be null or empty", exception.getMessage());
    }

    @Test
    public void testGetEmployeeById_employeeExist_success() {
        Employee result = ref.getEmployeeById(ID_EMPLOYEE_EXISTING);

        assertNotNull(result);
        assertEquals(employeeExisting, result);
    }

    @Test
    public void testGetEmployeeById_employeeDoesNotExist_exception() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ref.getEmployeeById(ID_EMPLOYEE_NON_EXISTING);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }

    @Test
    public void testUpdateEmployee_nonMandatoryDepartmentInRequest_success() {
        Employee result = ref.updateEmployee(employeeExistingUpdateRequest);

        assertNotNull(result);
        assertEquals(NAME_FIRST_EMPLOYEE_EXISTING_UPDATED, result.getNameFirst());
        assertTrue(result.getDepartments().contains(departmentNonMandatory));  // New department in request should be present
        assertTrue(result.getDepartments().contains(departmentMandatory));  // Mandatory department should still be present
    }

    @Test
    public void testDeleteEmployee_employeeExist_success() {
        boolean result = ref.deleteEmployee(ID_EMPLOYEE_EXISTING);

        assertTrue(result);
        verify(mockEmployeeRepository, times(1)).deleteById(ID_EMPLOYEE_EXISTING);
    }

    @Test
    public void testDeleteEmployee_employeeDoesNotExist_exception() {
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            ref.deleteEmployee(ID_EMPLOYEE_NON_EXISTING);
        });

        assertEquals("Employee Not Found", exception.getMessage());
    }
}
