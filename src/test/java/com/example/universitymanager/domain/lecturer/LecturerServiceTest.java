package com.example.universitymanager.domain.lecturer;

import com.example.universitymanager.domain.common.exceptions.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LecturerServiceTest {

    @Mock
    private LecturerRepository lecturerRepo;

    @InjectMocks
    private LecturerService lecturerService;


    @Test
    void create_lecturerWithoutId_createsLecturer() {
        //given
        Lecturer lecturerForCreate = Lecturer.builder()
                .firstName("John")
                .lastName("Smith")
                .title("professor")
                .email("example@com.pl")
                .build();
        ArgumentCaptor<Lecturer> lecturerCaptor = ArgumentCaptor.forClass(Lecturer.class);
        when(lecturerRepo.save(any(Lecturer.class))).thenReturn(lecturerForCreate);

        //when
        lecturerService.create(lecturerForCreate);

        //then
        verify(lecturerRepo, times(1)).save(lecturerCaptor.capture());
        Lecturer passedToSave = lecturerCaptor.getValue();
        assertEquals(lecturerForCreate, passedToSave);
    }

    @Test
    void create_lecturerWithId_throwsException() {
        //given
        Lecturer lecturerForCreate = Lecturer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .title("professor")
                .email("example@email.com")
                .build();

        //when //then
        assertThrows(IllegalArgumentException.class, () -> lecturerService.create(lecturerForCreate));
        verify(lecturerRepo, times(0)).save(any(Lecturer.class));
    }

    @Test
    void create_lecturerNull_throwsException() {
        //given //when //then
        assertThrows(IllegalArgumentException.class, () -> lecturerService.create(null));
        verify(lecturerRepo, times(0)).save(any(Lecturer.class));
    }

    @Test
    void getById_existingLecturerWithGivenId_returnsLecturer() {
        //given
        long lecturerId = 1L;
        Lecturer foundedLecturer = Lecturer.builder()
                .id(lecturerId)
                .firstName("John")
                .lastName("Smith")
                .title("professor")
                .email("example@email.com")
                .build();
        when(lecturerRepo.findById(eq(lecturerId))).thenReturn(Optional.of(foundedLecturer));

        //when
        Lecturer fetchedLecturer = lecturerService.getById(lecturerId);

        //then
        verify(lecturerRepo, times(1)).findById(lecturerId);
        assertEquals(foundedLecturer, fetchedLecturer);

    }

    @Test
    void getById_lecturerWithIdNotExists_throwsException() {
        //given
        long notExistingLecturerId = 1L;
        when(lecturerRepo.findById(eq(notExistingLecturerId))).thenReturn(Optional.empty());
        //when //then
        assertThrows(EntityNotFoundException.class, () -> lecturerService.getById(notExistingLecturerId));
        verify(lecturerRepo, times(1)).findById(notExistingLecturerId);
    }

    @Test
    void getAll_lecturersExistsInDb_returnsAllLecturersList() {
        //given
        Lecturer lecturer1 = Lecturer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .title("professor")
                .email("example@email.com")
                .build();
        Lecturer lecturer2 = Lecturer.builder()
                .id(2L)
                .firstName("Jack")
                .lastName("Johnson")
                .title("doctor")
                .email("example.x@email.com")
                .build();
        List<Lecturer> allLecturers = List.of(lecturer1, lecturer2);
        when(lecturerRepo.findAll()).thenReturn(allLecturers);

        //when

        List<Lecturer> fetchedLecturers = lecturerService.getAll();

        //then
        verify(lecturerRepo, times(1)).findAll();
        assertEquals(allLecturers.size(), fetchedLecturers.size());
        assertTrue(fetchedLecturers.containsAll(allLecturers));
    }

    @Test
    void getAll_noLecturersInDb_returnsEmptyList() {
        //given
        when(lecturerRepo.findAll()).thenReturn(Collections.emptyList());

        //when
        List<Lecturer> fetchedLecturers = lecturerService.getAll();

        //then
        verify(lecturerRepo, times(1)).findAll();
        assertTrue(fetchedLecturers.isEmpty());
    }

    @Test
    void update_givenExistingUpdatedLecturerWithId_updatesLecturer() {
        //given
        Lecturer updatedLecturer = Lecturer.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("Johnson")
                .title("professor")
                .email("example@email.com")
                .build();
        when(lecturerRepo.existsById(updatedLecturer.getId())).thenReturn(true);
        when(lecturerRepo.save(any(Lecturer.class))).thenReturn(updatedLecturer);

        //when
        lecturerService.update(updatedLecturer);

        //then
        verify(lecturerRepo, times(1)).save(updatedLecturer);
    }

    @Test
    void update_givenLecturerWithNotExistingId_throwsException() {
        //given
        Lecturer updatedLecturer = Lecturer.builder()
                .id(16L)
                .firstName("Jack")
                .lastName("Johnson")
                .title("professor")
                .email("example@email.com")
                .build();
        when(lecturerRepo.existsById(updatedLecturer.getId())).thenReturn(false);

        //when //then
        assertThrows(EntityNotFoundException.class, () -> lecturerService.update(updatedLecturer));
        verify(lecturerRepo, times(1)).existsById(updatedLecturer.getId());
        verify(lecturerRepo, times(0)).save(any(Lecturer.class));
    }

    @Test
    void update_givenUpdatedLecturerWithNullId_throwsException() {
        //given
        Lecturer updatedLecturer = Lecturer.builder()
                .id(null)
                .firstName("Jack")
                .lastName("Johnson")
                .title("doctor")
                .email("example@email.com")
                .build();

        //when //then
        assertThrows(IllegalArgumentException.class, () -> lecturerService.update(updatedLecturer));
        verify(lecturerRepo, times(0)).existsById(any());
        verify(lecturerRepo, times(0)).save(any());
    }

    @Test
    void update_lecturerNull_throwsException() {
        //given //when //then
        assertThrows(IllegalArgumentException.class, () -> lecturerService.update(null));
        verify(lecturerRepo, times(0)).existsById(any());
        verify(lecturerRepo, times(0)).save(any());
    }

    @Test
    void delete_lecturerWithGivenIdExists_deleteLecturer() {
        //given
        long lecturerId = 1L;
        when(lecturerRepo.existsById(lecturerId)).thenReturn(true);

        //when
        lecturerService.delete(lecturerId);

        //then
        verify(lecturerRepo, times(1)).deleteById(lecturerId);
    }

    @Test
    void delete_lecturerWithGivenIdNotExists_throwsException() {
        //given
        long notExistingId = 199999L;
        when(lecturerRepo.existsById(notExistingId)).thenReturn(false);

        //when //then
        assertThrows(EntityNotFoundException.class, () -> lecturerService.delete(notExistingId));
        verify(lecturerRepo, times(0)).deleteById(any());
    }

}