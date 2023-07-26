package com.example.universitymanager.domain.course.controller;

import com.example.universitymanager.domain.course.Course;
import com.example.universitymanager.domain.course.CourseDto;
import com.example.universitymanager.domain.course.CourseService;
import com.example.universitymanager.domain.course.controller.request.CreateCourseRequest;
import com.example.universitymanager.domain.course.controller.request.UpdateCourseRequest;
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
@RequestMapping("api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseDto createCourse(@RequestBody @Valid CreateCourseRequest createCourseRequest) {
        return courseService.create(createCourseRequest).toDto();
    }

    @GetMapping
    public List<CourseDto> getAllCourses() {
        return courseService.getAll().stream()
                .map(Course::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public CourseDto getCourseById(@PathVariable Long id) {
        return courseService.getById(id).toDto();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourseById(@PathVariable Long id) {
        courseService.deleteById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public CourseDto updateCourse(@RequestBody @Valid UpdateCourseRequest updateCourseRequest) {
        return courseService.update(updateCourseRequest).toDto();
    }

}
