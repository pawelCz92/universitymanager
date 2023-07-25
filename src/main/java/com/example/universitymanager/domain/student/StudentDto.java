package com.example.universitymanager.domain.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class StudentDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String indexCode;
    private int studiesStartYear;
}
