package com.example.arinternship.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(nullable = false)
    private String university;

    @Column(nullable = false)
    private String faculty;

    @Column(nullable = false)
    private String major;

    @Column(name = "contact_number", nullable = false)
    private String contactNumber;

    @Column(nullable = false)
    private String email;

    @Column(name = "intern_department", nullable = false)
    private String internDepartment;

    @Column(name = "intern_duration", nullable = false)
    private String internDuration;

    @Column(name = "attached_project")
    private String attachedProject;

    @Column(length = 10)
    private String grade;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "profile_file")
    private String profileFile;

    @Column(name = "project_file")
    private String projectFile;
}