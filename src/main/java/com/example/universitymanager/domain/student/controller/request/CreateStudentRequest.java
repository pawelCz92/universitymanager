package com.example.universitymanager.domain.student.controller.request;

import lombok.Getter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
public class CreateStudentRequest {

    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @NotBlank(message = "Index code cannot be empty")
    private String indexCode;
    @Min(value = 2000, message = "Studies start year should not be less than 2000")
    @Max(value = 2099, message = "Studies start year should not be greater than 2099")
    private int studiesStartYear;
}
