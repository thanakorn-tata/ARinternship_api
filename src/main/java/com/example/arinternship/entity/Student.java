package com.example.arinternship.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String university;
    private String faculty;
    private String major;
    private String phone;
    private String email;
    private String department;
    private String internshipPeriod;
    private String comment;
    private String profileFile;
    private String projectFile;
}
