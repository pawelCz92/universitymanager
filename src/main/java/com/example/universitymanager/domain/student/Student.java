package com.example.universitymanager.domain.student;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "students")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    private String firstName;
    private String lastName;
    private String indexCode;
    private int studiesStartYear;


    public StudentDto toDto() {
        return StudentDto.builder()
                .id(this.id)
                .firstName(this.firstName)
                .lastName(this.lastName)
                .indexCode(this.indexCode)
                .studiesStartYear(this.studiesStartYear)
                .build();
    }
}
