package com.example.universitymanager.domain.course;

import com.example.universitymanager.domain.lecturer.LecturerDto;
import com.example.universitymanager.domain.student.StudentDto;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;

@Getter
@Builder
public class CourseDto {

    private Long id;
    private String courseName;
    private LecturerDto leadingLecturerDto;
    private Set<StudentDto> studentDtos;
}
