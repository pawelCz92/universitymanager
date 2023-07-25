package com.example.universitymanager.domain.student;

import com.example.universitymanager.domain.common.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepo;

    public Student create(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        if (student.getId() != null) {
            throw new IllegalArgumentException("Student id should be null");
        }
        return studentRepo.save(student);
    }

    public Student getById(Long id) {
        return studentRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Student with id " + id + " not found"));
    }

    public List<Student> getAll() {
        return studentRepo.findAll();
    }

    public void update(Student updatedStudent) {
        if (updatedStudent == null) {
            throw new IllegalArgumentException("Student must not be null");
        }
        if (updatedStudent.getId() == null) {
            throw new IllegalArgumentException("Student id must not be null");
        }
        if (!studentRepo.existsById(updatedStudent.getId())) {
            throw new EntityNotFoundException("Update failed. Student with id " + updatedStudent.getId() + " not found");
        }
        studentRepo.save(updatedStudent);
    }

    public void delete(Long id) {
        if (!studentRepo.existsById(id)) {
            throw new EntityNotFoundException("Delete failed. Student with id " + id + " not found");
        }
        studentRepo.deleteById(id);
    }

}
