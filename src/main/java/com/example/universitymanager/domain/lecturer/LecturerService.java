package com.example.universitymanager.domain.lecturer;

import com.example.universitymanager.domain.common.exceptions.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LecturerService {

    private final LecturerRepository lecturerRepo;

    public Lecturer create(Lecturer lecturer) {
        if (lecturer == null) {
            throw new IllegalArgumentException("Lecturer must not be null");
        }
        if (lecturer.getId() != null) {
            throw new IllegalArgumentException("Lecturer id should be null");
        }
        return lecturerRepo.save(lecturer);
    }

    public Lecturer getById(Long id) {
        return lecturerRepo.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Lecturer with id " + id + " not found"));
    }

    public List<Lecturer> getAll() {
        return lecturerRepo.findAll();
    }

    public void update(Lecturer updatedLecturer) {
        if (updatedLecturer == null) {
            throw new IllegalArgumentException("Lecturer must not be null");
        }
        if (updatedLecturer.getId() == null) {
            throw new IllegalArgumentException("Lecturer id must not be null");
        }
        if (!lecturerRepo.existsById(updatedLecturer.getId())) {
            throw new EntityNotFoundException("Update failed. Lecturer with id " + updatedLecturer.getId() + " not found");
        }
        lecturerRepo.save(updatedLecturer);
    }

    public void delete(Long id) {
        if (!lecturerRepo.existsById(id)) {
            throw new EntityNotFoundException("Delete failed. Lecturer with id " + id + " not found");
        }
        lecturerRepo.deleteById(id);
    }
}
