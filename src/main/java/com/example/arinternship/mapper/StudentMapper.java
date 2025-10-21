package com.example.arinternship.mapper;

import com.example.arinternship.dto.StudentDTO;
import com.example.arinternship.entity.Student;
import org.springframework.stereotype.Component;

@Component
public class StudentMapper {

    public StudentDTO toDTO(Student entity) {
        if (entity == null)
            return null;

        StudentDTO dto = new StudentDTO();
        dto.setId(entity.getId());
        dto.setFullname(entity.getFullname());
        dto.setUniversity(entity.getUniversity());
        dto.setFaculty(entity.getFaculty());
        dto.setMajor(entity.getMajor());
        dto.setContactNumber(entity.getContactNumber());
        dto.setEmail(entity.getEmail());
        dto.setInternDepartment(entity.getInternDepartment());
        dto.setInternDuration(entity.getInternDuration());
        dto.setAttachedProject(entity.getAttachedProject());
        dto.setGrade(entity.getGrade());
        dto.setCreatedBy(entity.getCreatedBy());
        dto.setProfileFile(entity.getProfileFile());
        dto.setProjectFile(entity.getProjectFile());

        return dto;
    }

    public Student toEntity(StudentDTO dto) {
        if (dto == null)
            return null;

        Student entity = new Student();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setFullname(dto.getFullname());
        entity.setUniversity(dto.getUniversity());
        entity.setFaculty(dto.getFaculty());
        entity.setMajor(dto.getMajor());
        entity.setContactNumber(dto.getContactNumber());
        entity.setEmail(dto.getEmail());
        entity.setInternDepartment(dto.getInternDepartment());
        entity.setInternDuration(dto.getInternDuration());
        entity.setAttachedProject(dto.getAttachedProject());
        entity.setGrade(dto.getGrade());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setProfileFile(dto.getProfileFile());
        entity.setProjectFile(dto.getProjectFile());

        return entity;
    }

    public void updateEntityFromDTO(StudentDTO dto, Student entity) {
        if (dto == null || entity == null)
            return;

        entity.setFullname(dto.getFullname());
        entity.setUniversity(dto.getUniversity());
        entity.setFaculty(dto.getFaculty());
        entity.setMajor(dto.getMajor());
        entity.setContactNumber(dto.getContactNumber());
        entity.setEmail(dto.getEmail());
        entity.setInternDepartment(dto.getInternDepartment());
        entity.setInternDuration(dto.getInternDuration());
        entity.setAttachedProject(dto.getAttachedProject());
        entity.setGrade(dto.getGrade());
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setProfileFile(dto.getProfileFile());
        entity.setProjectFile(dto.getProjectFile());
    }
}