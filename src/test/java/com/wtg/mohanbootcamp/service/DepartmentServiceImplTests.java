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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTests {

    private static final long ID_DEPARTMENT_NOT_READONLY_MANDATORY = 1L;
    private static final long ID_DEPARTMENT_READONLY_MANDATORY = 2L;
    private static final long ID_DEPARTMENT_CREATE_REQUEST = 100L;
    private static final long IT_DEPARTMENT_DONT_EXIST = 200L;
    private static final String NAME_DEPARTMENT_NOT_READONLY_MANDATORY = "HR";
    private static final String NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED = "HR Updated";
    private static final String NAME_DEPARTMENT_READONLY_MANDATORY = "Finance";
    private static final String NAME_DEPARTMENT_CREATE_REQUEST = "Marketing";

    private DepartmentService ref;

    @InjectMocks
    private DepartmentServiceImpl concreteRef;

    @Mock
    private DepartmentRepository departmentRepository;

    private Department notReadonlyMandatoryDepartment;
    private Department notReadonlyMandatoryDepartmentUpdated;
    private Department readonlyMandatoryDepartment;
    private Department departmentCreateRequest;
    private Department departmentCreateResponse;
    private Set<Department> departmentSet;
    private Employee employee;

    @BeforeEach
    public void setUp() {
        ref = concreteRef;
        setupDepartments();
        setupEmployee();
    }

    @AfterEach
    public void tearDown() {
        ref = null;
        concreteRef = null;

        notReadonlyMandatoryDepartment = null;
        notReadonlyMandatoryDepartmentUpdated = null;
        readonlyMandatoryDepartment = null;
        departmentCreateRequest = null;
        departmentCreateResponse = null;
        departmentSet = null;
        employee = null;
    }

    private void setupDepartments() {
        notReadonlyMandatoryDepartment = Department.builder().id(ID_DEPARTMENT_NOT_READONLY_MANDATORY).name(NAME_DEPARTMENT_NOT_READONLY_MANDATORY).readOnly(Boolean.FALSE).mandatory(Boolean.TRUE).build();
        readonlyMandatoryDepartment = Department.builder().id(ID_DEPARTMENT_READONLY_MANDATORY).name(NAME_DEPARTMENT_READONLY_MANDATORY).readOnly(Boolean.TRUE).mandatory(Boolean.TRUE).build();
        notReadonlyMandatoryDepartmentUpdated = Department.builder().id(ID_DEPARTMENT_NOT_READONLY_MANDATORY).name(NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED).readOnly(Boolean.FALSE).mandatory(Boolean.TRUE).build();

        departmentCreateRequest = Department.builder().name(NAME_DEPARTMENT_CREATE_REQUEST).readOnly(Boolean.FALSE).mandatory(Boolean.FALSE).build();
        departmentCreateResponse = Department.builder().id(ID_DEPARTMENT_CREATE_REQUEST).name(NAME_DEPARTMENT_CREATE_REQUEST).readOnly(Boolean.FALSE).mandatory(Boolean.FALSE).build();

        departmentSet = new LinkedHashSet<>(List.of(notReadonlyMandatoryDepartment, readonlyMandatoryDepartment));
    }

    private void setupEmployee() {
        employee = Employee.builder().id(1L).departments(departmentSet).build();
        notReadonlyMandatoryDepartment.setEmployees(new HashSet<>(Set.of(employee)));
    }

    @Test
    public void testGetAllDepartments() {
        when(departmentRepository.findAll()).thenReturn(new ArrayList<>(departmentSet));

        List<Department> result = ref.getAllDepartments();

        assertEquals(2, result.size());
        assertDepartment(result.get(0), ID_DEPARTMENT_NOT_READONLY_MANDATORY, NAME_DEPARTMENT_NOT_READONLY_MANDATORY, Boolean.FALSE, Boolean.TRUE);
        assertDepartment(result.get(1), ID_DEPARTMENT_READONLY_MANDATORY, NAME_DEPARTMENT_READONLY_MANDATORY, Boolean.TRUE, Boolean.TRUE);
    }

    @Test
    public void testCreateDepartmentSuccess() {
        when(departmentRepository.save(departmentCreateRequest)).thenReturn(departmentCreateResponse);

        Department result = ref.createDepartment(departmentCreateRequest);

        assertDepartment(result, ID_DEPARTMENT_CREATE_REQUEST, NAME_DEPARTMENT_CREATE_REQUEST, result.getReadOnly(), result.getMandatory());
    }

    @Test
    public void testCreateDepartmentThrowsUnsupportedOperationExceptionWhenRequestContainsId() {
        assertUnsupportedOperationException(() -> ref.createDepartment(notReadonlyMandatoryDepartment),
                "Please use update department if id already exist");
    }

    @Test
    public void testCreateDepartmentThrowsDuplicateKeyExceptionIfNameIsDuplicated() {
        when(departmentRepository.save(departmentCreateRequest)).thenThrow(new DataIntegrityViolationException(""));

        assertDuplicateKeyException(() -> ref.createDepartment(departmentCreateRequest),
                "Department Name should be unique");
    }

    @Test
    public void testGetDepartmentByIdSuccess() {
        when(departmentRepository.findById(ID_DEPARTMENT_NOT_READONLY_MANDATORY)).thenReturn(Optional.of(notReadonlyMandatoryDepartment));

        Department result = ref.getDepartmentById(ID_DEPARTMENT_NOT_READONLY_MANDATORY);

        assertDepartment(result, ID_DEPARTMENT_NOT_READONLY_MANDATORY, NAME_DEPARTMENT_NOT_READONLY_MANDATORY, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testGetDepartmentByIdThrowsEntityNotFoundExceptionForNonExistingDepartment() {
        when(departmentRepository.findById(IT_DEPARTMENT_DONT_EXIST)).thenReturn(Optional.empty());

        assertEntityNotFoundException(() -> ref.getDepartmentById(IT_DEPARTMENT_DONT_EXIST), "Department Not Found");
    }

    @Test
    public void testUpdateDepartmentSuccessForNotReadOnly() {
        when(departmentRepository.findById(ID_DEPARTMENT_NOT_READONLY_MANDATORY)).thenReturn(Optional.of(notReadonlyMandatoryDepartment));
        when(departmentRepository.save(notReadonlyMandatoryDepartmentUpdated)).thenReturn(notReadonlyMandatoryDepartmentUpdated);

        Department result = ref.updateDepartment(notReadonlyMandatoryDepartmentUpdated);

        assertDepartment(result, ID_DEPARTMENT_NOT_READONLY_MANDATORY, NAME_DEPARTMENT_NOT_READONLY_MANDATORY_UPDATED, Boolean.FALSE, Boolean.TRUE);
    }

    @Test
    public void testUpdateDepartmentThrowsUnsupportedOperationExceptionForReadOnly() {
        when(departmentRepository.findById(ID_DEPARTMENT_READONLY_MANDATORY)).thenReturn(Optional.of(readonlyMandatoryDepartment));

        assertUnsupportedOperationException(() -> ref.updateDepartment(readonlyMandatoryDepartment),
                "Cannot modify a readonly department");
    }

    @Test
    public void testDeleteDepartmentSuccessForNotReadOnly() {
        when(departmentRepository.findById(ID_DEPARTMENT_NOT_READONLY_MANDATORY)).thenReturn(Optional.of(notReadonlyMandatoryDepartment));

        boolean result = ref.deleteDepartment(ID_DEPARTMENT_NOT_READONLY_MANDATORY);

        assertTrue(result);
    }

    @Test
    public void testDeleteDepartmentThrowsUnsupportedOperationExceptionForReadOnly() {
        when(departmentRepository.findById(ID_DEPARTMENT_READONLY_MANDATORY)).thenReturn(Optional.of(readonlyMandatoryDepartment));

        assertUnsupportedOperationException(() -> ref.deleteDepartment(ID_DEPARTMENT_READONLY_MANDATORY),
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
