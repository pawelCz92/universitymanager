package com.example.universitymanager.domain.student;


import com.example.universitymanager.domain.common.ExceptionResponse;
import com.example.universitymanager.domain.student.controller.request.CreateStudentRequest;
import com.example.universitymanager.domain.student.controller.request.UpdateStudentRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class StudentIntegrationTest {

    public static final String BASE_URL = "/api/v1/students";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @BeforeEach
    public void clearDatabase() {
        studentRepository.deleteAll();
    }

    @Test
    void createStudent_validInput_createsStudentAndReturnsStatus201() throws Exception {
        // given
        CreateStudentRequest createStudentRequest = CreateStudentRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .indexCode("123456")
                .studiesStartYear(2023)
                .build();

        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudentRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        StudentDto returnedStudentDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), StudentDto.class);
        assertNotNull(returnedStudentDto);
        assertNotNull(returnedStudentDto.getId());
        assertThat(returnedStudentDto.getFirstName()).isEqualTo(createStudentRequest.getFirstName());
        assertThat(returnedStudentDto.getLastName()).isEqualTo(createStudentRequest.getLastName());
        assertThat(returnedStudentDto.getIndexCode()).isEqualTo(createStudentRequest.getIndexCode());
        assertThat(returnedStudentDto.getStudiesStartYear()).isEqualTo(createStudentRequest.getStudiesStartYear());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "John;;123456;2023",
            ";Doe;123456;2023",
            "John;Doe;;2023",
            "John;Doe;123456;1000",
    }, delimiter = ';')
    void createStudent_invalidInput_returnsStatus400(String firstName,
                                                     String lastName,
                                                     String indexCode,
                                                     int studiesStartYear) throws Exception {
        // given
        CreateStudentRequest createStudentRequest = CreateStudentRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .indexCode(indexCode)
                .studiesStartYear(studiesStartYear)
                .build();

        // when
        String response = mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createStudentRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertTrue(studentRepository.findAll().isEmpty());
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 400);
        assertFalse(exceptionResponse.getMessage().isBlank());
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
    }

    @Test
    void updateStudent_validInput_updatesStudentAndReturnsStatus204() throws Exception {
        // given

        Student studentToBeUpdated = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        studentRepository.save(studentToBeUpdated);
        UpdateStudentRequest updateStudentRequest = UpdateStudentRequest.builder()
                .id(studentToBeUpdated.getId())
                .firstName("John")
                .lastName("Doe")
                .indexCode("123456")
                .studiesStartYear(2023)
                .build();

        // when
        mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentRequest)))
                .andExpect(status().isNoContent());

        // then
        Student studentInDb = studentRepository.findById(updateStudentRequest.getId()).orElseThrow(EntityNotFoundException::new);
        Student studentFromUpdateRequest = Student.builder()
                .id(updateStudentRequest.getId())
                .firstName(updateStudentRequest.getFirstName())
                .lastName(updateStudentRequest.getLastName())
                .indexCode(updateStudentRequest.getIndexCode())
                .studiesStartYear(updateStudentRequest.getStudiesStartYear())
                .build();
        assertThat(studentInDb).isEqualTo(studentFromUpdateRequest);
    }

    @ParameterizedTest
    @CsvSource(value = {
            ";John;;123456;2023",
            "-6;John;;123456;2023",
            "1;;Doe;123456;2023",
            "1;John;Doe;;2023",
            "1;John;Doe;123456;1000"}, delimiter = ';')
    void updateStudent_invalidInput_returnsStatus400(Long id,
                                                     String firstName,
                                                     String lastName,
                                                     String indexCode,
                                                     int studiesStartYear) throws Exception {
        // given
        UpdateStudentRequest updateStudentRequest = UpdateStudentRequest.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .indexCode(indexCode)
                .studiesStartYear(studiesStartYear)
                .build();
        Student student = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        studentRepository.save(student);

        // when
        String response = mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(studentRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 400);
        assertFalse(exceptionResponse.getMessage().isBlank());
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
    }

    @Test
    void updateStudent_studentWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        UpdateStudentRequest updateStudentRequest = UpdateStudentRequest.builder()
                .id(500000L)
                .firstName("John")
                .lastName("Doe")
                .indexCode("123456")
                .studiesStartYear(2023)
                .build();

        Student student = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        studentRepository.save(student);

        // when
        String response = mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateStudentRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(studentRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

    @Test
    void getStudentById_existingStudentWithGivenId_returnsStudentAndStatus200() throws Exception {
        // given
        Student student1 = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        Student student2 = Student.builder()
                .firstName("Jane")
                .lastName("Doe")
                .indexCode("98765")
                .studiesStartYear(2021)
                .build();
        Student student3 = Student.builder()
                .firstName("Bob")
                .lastName("Smith")
                .indexCode("45678")
                .studiesStartYear(2020)
                .build();

        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);

        StudentDto expectedStudentDto = student2.toDto();

        // when
        String responseContent = mockMvc.perform(get(BASE_URL + "/" + student2.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        StudentDto foundedStudentByIdDto = objectMapper.readValue(responseContent, StudentDto.class);
        assertThat(expectedStudentDto).isEqualTo(foundedStudentByIdDto);
    }

    @Test
    void getStudentById_studentWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        Student student = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        studentRepository.save(student);
        long notExistingStudentId = student.getId() + 1;

        // when
        String response = mockMvc.perform(get(BASE_URL + "/" + notExistingStudentId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(studentRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL + "/" + notExistingStudentId);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

    @Test
    void getAllStudents_studentsExistsInDb_returnsStudentsListAndStatus200() throws Exception {
        // given
        Student student1 = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        Student student2 = Student.builder()
                .firstName("Jane")
                .lastName("Doe")
                .indexCode("98765")
                .studiesStartYear(2021)
                .build();
        Student student3 = Student.builder()
                .firstName("Bob")
                .lastName("Smith")
                .indexCode("45678")
                .studiesStartYear(2020)
                .build();
        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        List<StudentDto> expectedStudentsDto = List.of(student1.toDto(), student2.toDto(), student3.toDto());

        // when
        String responseContent = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        List<StudentDto> foundedStudentByIdDto = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertThat(foundedStudentByIdDto).containsAll(expectedStudentsDto);
    }

    @Test
    void getAllStudents_noStudentsExistsInDb_returnsEmptyListAndStatus200() throws Exception {
        // given // when
        String responseContent = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        List<StudentDto> foundedStudentByIdDto = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertThat(foundedStudentByIdDto).isEmpty();
    }

    @Test
    void deleteStudentById_existingStudentWithGivenId_deletesStudentAndStatus204() throws Exception {
        // given
        Student student1 = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        Student student2 = Student.builder()
                .firstName("Jane")
                .lastName("Doe")
                .indexCode("98765")
                .studiesStartYear(2021)
                .build();
        Student student3 = Student.builder()
                .firstName("Bob")
                .lastName("Smith")
                .indexCode("45678")
                .studiesStartYear(2020)
                .build();
        studentRepository.save(student1);
        studentRepository.save(student2);
        studentRepository.save(student3);
        long studentToDeleteId = student2.getId();

        // when
        mockMvc.perform(delete(BASE_URL + "/" + studentToDeleteId))
                .andExpect(status().isNoContent());

        // then
        assertFalse(studentRepository.existsById(studentToDeleteId));
        assertEquals(studentRepository.findAll().size(), 2);
        assertThat(studentRepository.findAll()).containsExactlyInAnyOrder(student1, student3);
    }

    @Test
    void deleteStudentById_studentWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        Student student = Student.builder()
                .firstName("Johny")
                .lastName("Doe")
                .indexCode("25469")
                .studiesStartYear(2022)
                .build();
        studentRepository.save(student);
        long notExistingStudentId = student.getId() + 1;

        // when
        String response = mockMvc.perform(delete(BASE_URL + "/" + notExistingStudentId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(studentRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL + "/" + notExistingStudentId);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

}