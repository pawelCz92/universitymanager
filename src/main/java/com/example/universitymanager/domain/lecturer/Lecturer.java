package com.example.universitymanager.domain.lecturer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "lecturers")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@EqualsAndHashCode
public class Lecturer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    private String firstName;
    private String lastName;
    private String title;
    private String email;


    public LecturerDto toDto() {
        return LecturerDto.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .email(email)
                .build();
    }
}
