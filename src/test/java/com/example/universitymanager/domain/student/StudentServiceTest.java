package com.example.universitymanager.domain.student;

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
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepo;

    @InjectMocks
    private StudentService studentService;


    @Test
    void create_studentWithoutId_createsStudent() {
        //given
        Student studentForCreate = Student.builder()
                .firstName("John")
                .lastName("Smith")
                .indexCode("123456")
                .studiesStartYear(2021)
                .build();
        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        when(studentRepo.save(any(Student.class))).thenReturn(studentForCreate);

        //when
        studentService.create(studentForCreate);

        //then
        verify(studentRepo, times(1)).save(studentCaptor.capture());
        Student passedToSave = studentCaptor.getValue();
        assertEquals(studentForCreate, passedToSave);
    }

    @Test
    void create_studentWithId_throwsException() {
        //given
        Student studentForCreate = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .indexCode("123456")
                .studiesStartYear(2021)
                .build();

        //when //then
        assertThrows(IllegalArgumentException.class, () -> studentService.create(studentForCreate));
        verify(studentRepo, times(0)).save(any(Student.class));
    }

    @Test
    void create_studentNull_throwsException() {
        //given //when //then
        assertThrows(IllegalArgumentException.class, () -> studentService.create(null));
        verify(studentRepo, times(0)).save(any(Student.class));
    }

    @Test
    void getById_existingStudentWithGivenId_returnsStudent() {
        //given
        long studentId = 1L;
        Student foundedStudent = Student.builder()
                .id(studentId)
                .firstName("John")
                .lastName("Smith")
                .indexCode("123456")
                .studiesStartYear(2021)
                .build();
        when(studentRepo.findById(eq(studentId))).thenReturn(Optional.of(foundedStudent));

        //when
        Student fetchedStudent = studentService.getById(studentId);

        //then
        verify(studentRepo, times(1)).findById(studentId);
        assertEquals(foundedStudent, fetchedStudent);

    }

    @Test
    void getById_studentWithIdNotExists_throwsException() {
        //given
        long notExistingStudentId = 1L;
        when(studentRepo.findById(eq(notExistingStudentId))).thenReturn(Optional.empty());
        //when //then
        assertThrows(EntityNotFoundException.class, () -> studentService.getById(notExistingStudentId));
        verify(studentRepo, times(1)).findById(notExistingStudentId);
    }

    @Test
    void getAll_studentsExistsInDb_returnsAllStudentsList() {
        //given
        Student student1 = Student.builder()
                .id(1L)
                .firstName("John")
                .lastName("Smith")
                .indexCode("123456")
                .studiesStartYear(2021)
                .build();
        Student student2 = Student.builder()
                .id(2L)
                .firstName("Jack")
                .lastName("Johnson")
                .indexCode("586987")
                .studiesStartYear(2022)
                .build();
        List<Student> allStudents = List.of(student1, student2);
        when(studentRepo.findAll()).thenReturn(allStudents);

        //when

        List<Student> fetchedStudents = studentService.getAll();

        //then
        verify(studentRepo, times(1)).findAll();
        assertEquals(allStudents.size(), fetchedStudents.size());
        assertTrue(fetchedStudents.containsAll(allStudents));
    }

    @Test
    void getAll_noStudentsInDb_returnsEmptyList() {
        //given
        when(studentRepo.findAll()).thenReturn(Collections.emptyList());

        //when
        List<Student> fetchedStudents = studentService.getAll();

        //then
        verify(studentRepo, times(1)).findAll();
        assertTrue(fetchedStudents.isEmpty());
    }

    @Test
    void update_givenExistingUpdatedStudentWithId_updatesStudent() {
        //given
        Student updatedStudent = Student.builder()
                .id(1L)
                .firstName("Jack")
                .lastName("Johnson")
                .indexCode("586987")
                .studiesStartYear(2022)
                .build();
        when(studentRepo.existsById(updatedStudent.getId())).thenReturn(true);
        when(studentRepo.save(any(Student.class))).thenReturn(updatedStudent);

        //when
        studentService.update(updatedStudent);

        //then
        verify(studentRepo, times(1)).save(updatedStudent);
    }

    @Test
    void update_givenStudentWithNotExistingId_throwsException() {
        //given
        Student updatedStudent = Student.builder()
                .id(16L)
                .firstName("Jack")
                .lastName("Johnson")
                .indexCode("586987")
                .studiesStartYear(2022)
                .build();
        when(studentRepo.existsById(updatedStudent.getId())).thenReturn(false);

        //when //then
        assertThrows(EntityNotFoundException.class, () -> studentService.update(updatedStudent));
        verify(studentRepo, times(1)).existsById(updatedStudent.getId());
        verify(studentRepo, times(0)).save(any(Student.class));
    }

    @Test
    void update_givenUpdatedStudentWithNullId_throwsException() {
        //given
        Student updatedStudent = Student.builder()
                .id(null)
                .firstName("Jack")
                .lastName("Johnson")
                .indexCode("586987")
                .studiesStartYear(2022)
                .build();

        //when //then
        assertThrows(IllegalArgumentException.class, () -> studentService.update(updatedStudent));
        verify(studentRepo, times(0)).existsById(any());
        verify(studentRepo, times(0)).save(any());
    }

    @Test
    void update_studentNull_throwsException() {
        //given //when //then
        assertThrows(IllegalArgumentException.class, () -> studentService.update(null));
        verify(studentRepo, times(0)).existsById(any());
        verify(studentRepo, times(0)).save(any());
    }

    @Test
    void delete_studentWithGivenIdExists_deleteStudent() {
        //given
        long studentId = 1L;
        when(studentRepo.existsById(studentId)).thenReturn(true);

        //when
        studentService.delete(studentId);

        //then
        verify(studentRepo, times(1)).deleteById(studentId);
    }

    @Test
    void delete_studentWithGivenIdNotExists_throwsException() {
        //given
        long notExistingId = 199999L;
        when(studentRepo.existsById(notExistingId)).thenReturn(false);

        //when //then
        assertThrows(EntityNotFoundException.class, () -> studentService.delete(notExistingId));
        verify(studentRepo, times(0)).deleteById(any());
    }
}