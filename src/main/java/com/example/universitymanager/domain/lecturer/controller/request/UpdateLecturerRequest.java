package com.example.universitymanager.domain.lecturer.controller.request;

import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Getter
@Builder
public class UpdateLecturerRequest {

    @NotNull(message = "Student id must not be null")
    @Min(value = 1, message = "Student id should not be less than 1")
    private Long id;
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @NotBlank(message = "Title name cannot be empty")
    private String title;
    @NotBlank(message = "Email name cannot be empty")
    @Email(message = "Email should be valid")
    private String email;
}
