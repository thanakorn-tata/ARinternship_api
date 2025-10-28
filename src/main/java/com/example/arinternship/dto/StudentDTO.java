package com.example.arinternship.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class StudentDTO {
    private Long id;
    private String fullname;
    private String university;
    private String faculty;
    private String major;
    
    @JsonProperty("contact_number")
    private String contactNumber;
    
    private String email;
    
    @JsonProperty("intern_department")
    private String internDepartment;
    
    @JsonProperty("intern_duration")
    private String internDuration;
    
    @JsonProperty("attached_project")
    private String attachedProject;
    
    private String grade;
    
    @JsonProperty("profile_file")
    private String profileFile;
    
    @JsonProperty("project_file")
    private String projectFile;
    
    @JsonProperty("created_by")
    private Long createdBy;  // ← เปลี่ยนจาก Integer เป็น Long
}