package com.example.universitymanager.domain.student.controller;

import com.example.universitymanager.domain.student.Student;
import com.example.universitymanager.domain.student.StudentDto;
import com.example.universitymanager.domain.student.StudentService;
import com.example.universitymanager.domain.student.controller.request.CreateStudentRequest;
import com.example.universitymanager.domain.student.controller.request.UpdateStudentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public StudentDto createStudent(@RequestBody @Valid CreateStudentRequest createStudentRequest) {
        Student studentToCreate = Student.builder()
                .firstName(createStudentRequest.getFirstName())
                .lastName(createStudentRequest.getLastName())
                .indexCode(createStudentRequest.getIndexCode())
                .studiesStartYear(createStudentRequest.getStudiesStartYear())
                .build();
        return studentService.create(studentToCreate).toDto();
    }

    @GetMapping(path = "/{id}")
    public StudentDto getStudentById(@PathVariable Long id) {
        return studentService.getById(id).toDto();
    }

    @GetMapping
    public List<StudentDto> getAllStudents() {
        return studentService.getAll().stream()
                .map(Student::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateStudentById(@RequestBody @Valid UpdateStudentRequest updateStudentRequest) {
        Student studentToUpdate = Student.builder()
                .id(updateStudentRequest.getId())
                .firstName(updateStudentRequest.getFirstName())
                .lastName(updateStudentRequest.getLastName())
                .indexCode(updateStudentRequest.getIndexCode())
                .studiesStartYear(updateStudentRequest.getStudiesStartYear())
                .build();
        studentService.update(studentToUpdate);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteStudentById(@PathVariable Long id) {
        studentService.delete(id);
    }
}
