package com.example.universitymanager.domain.course.controller.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Builder
public class CreateCourseRequest {

    @NotBlank(message = "Course name must not be empty")
    private String courseName;
    @NotNull(message = "Leading lecturer id must not be null")
    private long leadingLecturerId;
    @NotNull(message = "Students ids must not be null")
    private Set<Long> studentsIds;

}
