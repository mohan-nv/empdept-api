package com.wtg.mohanbootcamp.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EMPLOYEE")
@Getter
@Setter
@ToString
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_EMPLOYEE")
    @SequenceGenerator(name = "SEQ_EMPLOYEE", sequenceName = "SEQ_EMPLOYEE", allocationSize = 1)
    private Long id;

    @Column(name = "NAME_FIRST", nullable = false)
    private String nameFirst;

    @Column(name = "NAME_LAST", nullable = false)
    private String nameLast;

    @ManyToMany
    @JoinTable(
            name = "MAP_EMPLOYEE_DEPARTMENT",
            joinColumns = @JoinColumn(name = "ID_EMPLOYEE"),
            inverseJoinColumns = @JoinColumn(name = "ID_DEPARTMENT")
    )
    private Set<Department> departments = new HashSet<>();
}
