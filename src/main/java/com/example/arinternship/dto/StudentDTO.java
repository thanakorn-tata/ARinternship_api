package com.example.arinternship.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentDTO {

    private Long id;
    private String fullname;
    private String university;
    private String faculty;
    private String major;
    
    @JsonProperty("contact_number")  // 🔥 ใช้ annotation เพื่อ map กับ JSON
    private String contactNumber;
    
    private String email;
    
    @JsonProperty("intern_department")
    private String internDepartment;
    
    @JsonProperty("intern_duration")
    private String internDuration;
    
    @JsonProperty("attached_project")
    private String attachedProject;
    
    private String grade;
    
    @JsonProperty("created_by")
    private Long createdBy;
    
    @JsonProperty("profile_file")
    private String profileFile;
    
    @JsonProperty("project_file")
    private String projectFile;
}