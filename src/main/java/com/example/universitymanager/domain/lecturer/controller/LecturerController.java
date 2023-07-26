package com.example.universitymanager.domain.lecturer.controller;

import com.example.universitymanager.domain.lecturer.Lecturer;
import com.example.universitymanager.domain.lecturer.LecturerDto;
import com.example.universitymanager.domain.lecturer.LecturerService;
import com.example.universitymanager.domain.lecturer.controller.request.CreateLecturerRequest;
import com.example.universitymanager.domain.lecturer.controller.request.UpdateLecturerRequest;
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
@RequestMapping("api/v1/lecturers")
@RequiredArgsConstructor
public class LecturerController {

    private final LecturerService lecturerService;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public LecturerDto createLecturer(@RequestBody @Valid CreateLecturerRequest createLecturerRequest) {
        Lecturer lecturerToCreate = Lecturer.builder()
                .firstName(createLecturerRequest.getFirstName())
                .lastName(createLecturerRequest.getLastName())
                .title(createLecturerRequest.getTitle())
                .email(createLecturerRequest.getEmail())
                .build();
        return lecturerService.create(lecturerToCreate).toDto();
    }

    @GetMapping(path = "/{id}")
    public LecturerDto getLecturerById(@PathVariable Long id) {
        return lecturerService.getById(id).toDto();
    }

    @GetMapping
    public List<LecturerDto> getAllLecturers() {
        return lecturerService.getAll().stream()
                .map(Lecturer::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void updateLecturerById(@RequestBody @Valid UpdateLecturerRequest updateLecturerRequest) {
        Lecturer lecturerToUpdate = Lecturer.builder()
                .id(updateLecturerRequest.getId())
                .firstName(updateLecturerRequest.getFirstName())
                .lastName(updateLecturerRequest.getLastName())
                .title(updateLecturerRequest.getTitle())
                .email(updateLecturerRequest.getEmail())
                .build();
        lecturerService.update(lecturerToUpdate);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteLecturerById(@PathVariable Long id) {
        lecturerService.delete(id);
    }
}