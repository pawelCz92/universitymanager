package com.example.universitymanager.domain.course;

import com.example.universitymanager.domain.lecturer.Lecturer;
import com.example.universitymanager.domain.student.Student;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "courses")
@NoArgsConstructor
@Getter
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter
    private Long id;
    private String courseName;
    @OneToOne
    private Lecturer leadingLecturer;
    @OneToMany(orphanRemoval = true)
    private Set<Student> students;

    @Builder
    public Course(Long id, String courseName, Lecturer leadingLecturer) {
        this.id = id;
        this.courseName = courseName;
        this.leadingLecturer = leadingLecturer;
    }

    public void addStudent(Student student) {
        if (students == null) {
            students = new HashSet<>();
        }
        boolean setChanged = students.add(student);
        if (!setChanged) {
            throw new IllegalArgumentException("Student already exists in the students set.");
        }
    }

    public void removeStudent(Student student) {
        if (students == null) {
            throw new IllegalStateException("Students set is null.");
        }
        boolean setChanged = students.remove(student);
        if (!setChanged) {
            throw new IllegalArgumentException("Student does not exist in the course students set.");
        }
    }

    public Set<Student> getStudents() {
        if (students == null) {
            students = new HashSet<>();
        }
        return Collections.unmodifiableSet(students);
    }

    public CourseDto toDto() {
        return CourseDto.builder()
                .id(id)
                .courseName(courseName)
                .leadingLecturerDto(leadingLecturer.toDto())
                .studentDtos(students.stream()
                        .map(Student::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }
}
