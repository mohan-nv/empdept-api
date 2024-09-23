package com.wtg.mohanbootcamp.service;

import com.wtg.mohanbootcamp.persistence.Department;
import com.wtg.mohanbootcamp.persistence.DepartmentRepository;
import com.wtg.mohanbootcamp.persistence.Employee;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.util.*;

import static com.wtg.mohanbootcamp.service.EmployeeServiceImplTests.ID_EMPLOYEE_EXISTING;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTests {

    public static final long ID_DEPARTMENT_NOT_READONLY_MANDATORY = 1L;
    public static final long ID_DEPARTMENT_READONLY_NON_MANDATORY = 2L;
    public static final long ID_DEPARTMENT_CREATE_REQUEST = 100L;
    public static final long ID_DEPARTMENT_DOES_NOT_EXIST = 200L;
    public static final String NAME_DEPARTMENT_NOT_READONLY_MANDATORY = "NAME_DEPARTMENT_NOT_READONLY_MANDATORY";
    public static final String NAME_DEPARTMENT_READONLY_NON_MANDATORY = "NAME_DEPARTMENT_READONLY_NON_MANDATORY";
    public static final String NAME_DEPARTMENT_CREATE_REQUEST = "NAME_DEPARTMENT_CREATE_REQUEST";
    public static final String NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED = "NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED";
    public static final String NAME_DEPARTMENT_DUPLICATE = "NAME_DEPARTMENT_DUPLICATE";

    private DepartmentService ref;

    @InjectMocks
    private DepartmentServiceImpl concreteRef;

    @Mock
    private DepartmentRepository mockDepartmentRepository;

    private Department departmentNotReadonlyMandatory;
    private Department departmentReadonlyNonMandatory;
    private Department departmentCreateRequest;
    private Department departmentCreateResponse;
    private Department departmentDuplicateName;
    private Employee employeeExisting;

    @BeforeEach
    public void setUp() {
        ref = concreteRef;
        setupDepartments();
        setupEmployee();

        lenient().when(mockDepartmentRepository.findAll()).thenReturn(new ArrayList<>(List.of(departmentNotReadonlyMandatory, departmentReadonlyNonMandatory)));
        lenient().when(mockDepartmentRepository.findById(ID_DEPARTMENT_READONLY_NON_MANDATORY)).thenReturn(Optional.of(departmentReadonlyNonMandatory));
        lenient().when(mockDepartmentRepository.findById(ID_DEPARTMENT_NOT_READONLY_MANDATORY)).thenReturn(Optional.of(departmentNotReadonlyMandatory));
        lenient().when(mockDepartmentRepository.findById(ID_DEPARTMENT_DOES_NOT_EXIST)).thenReturn(Optional.empty());
        lenient().when(mockDepartmentRepository.save(departmentCreateRequest)).thenReturn(departmentCreateResponse);
        lenient().when(mockDepartmentRepository.save(departmentDuplicateName)).thenThrow(new DataIntegrityViolationException(""));
        lenient().when(mockDepartmentRepository.save(departmentNotReadonlyMandatory)).thenReturn(departmentNotReadonlyMandatory);
    }

    @AfterEach
    public void tearDown() {
        ref = null;
        concreteRef = null;

        departmentNotReadonlyMandatory = null;
        departmentReadonlyNonMandatory = null;
        departmentCreateRequest = null;
        departmentCreateResponse = null;
        departmentDuplicateName = null;
        employeeExisting = null;
    }

    private void setupDepartments() {
        departmentNotReadonlyMandatory = Department.builder().id(ID_DEPARTMENT_NOT_READONLY_MANDATORY).name(NAME_DEPARTMENT_NOT_READONLY_MANDATORY).readOnly(Boolean.FALSE).mandatory(Boolean.TRUE).build();
        departmentReadonlyNonMandatory = Department.builder().id(ID_DEPARTMENT_READONLY_NON_MANDATORY).name(NAME_DEPARTMENT_READONLY_NON_MANDATORY).readOnly(Boolean.TRUE).mandatory(Boolean.FALSE).build();

        departmentCreateRequest = Department.builder().name(NAME_DEPARTMENT_CREATE_REQUEST).readOnly(Boolean.FALSE).mandatory(Boolean.FALSE).build();
        departmentCreateResponse = Department.builder().id(ID_DEPARTMENT_CREATE_REQUEST).name(NAME_DEPARTMENT_CREATE_REQUEST).readOnly(Boolean.FALSE).mandatory(Boolean.FALSE).build();

        departmentDuplicateName = Department.builder().name(NAME_DEPARTMENT_DUPLICATE).readOnly(Boolean.FALSE).mandatory(Boolean.FALSE).build();
    }

    private void setupEmployee() {
        employeeExisting = Employee.builder().id(ID_EMPLOYEE_EXISTING).departments(new HashSet<>(Set.of(departmentNotReadonlyMandatory))).build();
        departmentNotReadonlyMandatory.setEmployees(new HashSet<>(Set.of(employeeExisting)));
    }

    @Test
    public void testGetAllDepartments_success() {
        List<Department> result = ref.getAllDepartments();

        assertEquals(2, result.size());
        assertTrue(result.contains(departmentNotReadonlyMandatory));
        assertTrue(result.contains(departmentReadonlyNonMandatory));
    }

    @Test
    public void testCreateDepartment_validInput_success() {
        Department result = ref.createDepartment(departmentCreateRequest);
        assertEquals(result, departmentCreateResponse);
    }

    @Test
    public void testCreateDepartment_departmentIdNotNull_exception() {
        assertUnsupportedOperationException(() -> ref.createDepartment(departmentReadonlyNonMandatory),
                "Please use update department if id already exist");
    }

    @Test
    public void testCreateDepartment_departmentNameDuplicate_exception() {
        assertDuplicateKeyException(() -> ref.createDepartment(departmentDuplicateName),
                "Department Name should be unique");
    }

    @Test
    public void testGetDepartmentById_departmentExist_success() {
        Department result = ref.getDepartmentById(ID_DEPARTMENT_READONLY_NON_MANDATORY);

        assertEquals(departmentReadonlyNonMandatory, result);
    }

    @Test
    public void testGetDepartmentById_departmentDoesNotExist_exception() {
        assertEntityNotFoundException(() -> ref.getDepartmentById(ID_DEPARTMENT_DOES_NOT_EXIST), "Department Not Found");
    }

    @Test
    public void testUpdateDepartment_readOnlyFalse_success() {
        departmentNotReadonlyMandatory.setName(NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED);
        Department result = ref.updateDepartment(departmentNotReadonlyMandatory);
        assertDepartment(result, ID_DEPARTMENT_NOT_READONLY_MANDATORY, NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testUpdateDepartment_readOnlyTrue_exception() {
        assertUnsupportedOperationException(() -> ref.updateDepartment(departmentReadonlyNonMandatory),
                "Cannot modify a readonly department");
    }

    @Test
    public void testUpdateDepartment_departmentDoesNotExist_exception() {
        assertEntityNotFoundException(() -> ref.getDepartmentById(ID_DEPARTMENT_DOES_NOT_EXIST), "Department Not Found");
    }

    @Test
    public void testDeleteDepartment_readOnlyFalse_success() {
        boolean result = ref.deleteDepartment(ID_DEPARTMENT_NOT_READONLY_MANDATORY);
        assertTrue(result);
        verify(mockDepartmentRepository, times(1)).deleteById(ID_DEPARTMENT_NOT_READONLY_MANDATORY);
    }

    @Test
    public void testDeleteDepartment_readOnlyTrue_exception() {
        assertUnsupportedOperationException(() -> ref.deleteDepartment(ID_DEPARTMENT_READONLY_NON_MANDATORY),
                "Cannot delete a readonly department");
    }

    // Helper Methods for Common Assertions
    private void assertDepartment(Department department, long expectedId, String expectedName, Boolean expectedReadOnly, Boolean expectedMandatory) {
        assertNotNull(department);
        assertEquals(expectedId, department.getId());
        assertEquals(expectedName, department.getName());
        assertEquals(expectedReadOnly, department.getReadOnly());
        assertEquals(expectedMandatory, department.getMandatory());
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
