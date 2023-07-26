package com.example.universitymanager.domain.course;

import com.example.universitymanager.domain.common.exceptions.EntityNotFoundException;
import com.example.universitymanager.domain.course.controller.request.CreateCourseRequest;
import com.example.universitymanager.domain.course.controller.request.UpdateCourseRequest;
import com.example.universitymanager.domain.lecturer.Lecturer;
import com.example.universitymanager.domain.lecturer.LecturerService;
import com.example.universitymanager.domain.student.Student;
import com.example.universitymanager.domain.student.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;
    private final StudentService studentService;
    private final LecturerService lecturerService;

    @Transactional
    public Course create(CreateCourseRequest createCourseRequest) {
        if (createCourseRequest == null) {
            throw new IllegalArgumentException("CreateCourseRequest must not be null");
        }
        Lecturer lecturer = lecturerService.getById(createCourseRequest.getLeadingLecturerId());
        Course courseForCreate = Course.builder()
                .courseName(createCourseRequest.getCourseName())
                .leadingLecturer(lecturer)
                .build();
        createCourseRequest.getStudentsIds().stream()
                .map(studentService::getById)
                .forEach(courseForCreate::addStudent);
        return courseRepo.save(courseForCreate);
    }

    public Course getById(Long id) {
        return courseRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Course with id " + id + " not found"));
    }

    public List<Course> getAll() {
        return courseRepo.findAll();
    }

    public void deleteById(Long id) {
        if (!courseRepo.existsById(id)) {
            throw new EntityNotFoundException("Delete failed. Course with id " + id + " not found");
        }
        courseRepo.deleteById(id);
    }

    @Transactional
    public Course update(UpdateCourseRequest updateCourseRequest) {
        if (updateCourseRequest == null) {
            throw new IllegalArgumentException("UpdateCourseRequest must not be null");
        }
        Lecturer lecturer = lecturerService.getById(updateCourseRequest.getLeadingLecturerId());
        Set<Student> studentsByIdsFromRequest = updateCourseRequest.getStudentsIds().stream()
                .map(studentService::getById)
                .collect(Collectors.toSet());
        Course updatedCourse = Course.builder()
                .id(updateCourseRequest.getId())
                .courseName(updateCourseRequest.getCourseName())
                .leadingLecturer(lecturer)
                .build();
        studentsByIdsFromRequest.forEach(updatedCourse::addStudent);
        return courseRepo.save(updatedCourse);
    }
}
