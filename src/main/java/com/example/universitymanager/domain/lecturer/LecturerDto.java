package com.example.universitymanager.domain.lecturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class LecturerDto {

    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String email;
}
