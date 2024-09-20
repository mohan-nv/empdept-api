package com.wtg.mohanbootcamp.persistence.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

@Entity
@Table(name = "DEPARTMENT")
@Getter
@Setter
@ToString
@JsonIgnoreProperties({"employees"})
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ_DEPARTMENT")
    @SequenceGenerator(name = "SEQ_DEPARTMENT", sequenceName = "SEQ_DEPARTMENT", allocationSize = 1)
    private Long id;

    @Column(name = "NAME", nullable = false, unique = true)
    private String name;

    @Column(name = "READ_ONLY")
    private Boolean readOnly;

    @Column(name = "MANDATORY")
    private Boolean mandatory = Boolean.FALSE;

    @ManyToMany(mappedBy = "departments")
    private Set<Employee> employees;

}
