package com.example.universitymanager.domain.lecturer;


import com.example.universitymanager.domain.common.ExceptionResponse;
import com.example.universitymanager.domain.lecturer.controller.request.CreateLecturerRequest;
import com.example.universitymanager.domain.lecturer.controller.request.UpdateLecturerRequest;
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
class LecturerIntegrationTest {

    public static final String BASE_URL = "/api/v1/lecturers";
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LecturerRepository lecturerRepository;

    @BeforeEach
    public void clearDatabase() {
        lecturerRepository.deleteAll();
    }

    @Test
    void createLecturer_validInput_createsLecturerAndReturnsStatus201() throws Exception {
        // given
        CreateLecturerRequest createLecturerRequest = CreateLecturerRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();

        // when
        MvcResult mvcResult = mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLecturerRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        // then
        LecturerDto returnedLecturerDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), LecturerDto.class);
        assertNotNull(returnedLecturerDto);
        assertNotNull(returnedLecturerDto.getId());
        assertThat(returnedLecturerDto.getFirstName()).isEqualTo(createLecturerRequest.getFirstName());
        assertThat(returnedLecturerDto.getLastName()).isEqualTo(createLecturerRequest.getLastName());
        assertThat(returnedLecturerDto.getTitle()).isEqualTo(createLecturerRequest.getTitle());
        assertThat(returnedLecturerDto.getEmail()).isEqualTo(createLecturerRequest.getEmail());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "John;;dr;email@gmail.com",
            ";Doe;professor;mail@mail.pl",
            "John;Doe;;email@mail.com",
            "John;Doe;dr;email@mail.com",
    }, delimiter = ';')
    void createLecturer_invalidInput_returnsStatus400(String firstName,
                                                      String lastName,
                                                      String title,
                                                      String email) throws Exception {
        // given
        CreateLecturerRequest createLecturerRequest = CreateLecturerRequest.builder()
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .email(email)
                .build();

        // when
        String response = mockMvc.perform(post(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createLecturerRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertTrue(lecturerRepository.findAll().isEmpty());
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 400);
        assertFalse(exceptionResponse.getMessage().isBlank());
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
    }

    @Test
    void updateLecturer_validInput_updatesLecturerAndReturnsStatus204() throws Exception {
        // given

        Lecturer lecturerToBeUpdated = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();
        lecturerRepository.save(lecturerToBeUpdated);
        UpdateLecturerRequest updateLecturerRequest = UpdateLecturerRequest.builder()
                .id(lecturerToBeUpdated.getId())
                .firstName("John")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();

        // when
        mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateLecturerRequest)))
                .andExpect(status().isNoContent());

        // then
        Lecturer lecturerInDb = lecturerRepository.findById(updateLecturerRequest.getId()).orElseThrow(EntityNotFoundException::new);
        Lecturer lecturerFromUpdateRequest = Lecturer.builder()
                .id(updateLecturerRequest.getId())
                .firstName(updateLecturerRequest.getFirstName())
                .lastName(updateLecturerRequest.getLastName())
                .title(updateLecturerRequest.getTitle())
                .email(updateLecturerRequest.getEmail())
                .build();
        assertThat(lecturerInDb).isEqualTo(lecturerFromUpdateRequest);
    }

    @ParameterizedTest
    @CsvSource(value = {
            ";John;;dr;e",
            "-6;John;;professor;email",
            "1;;Doe;professor;email@email.com",
            "1;John;Doe;;email@xx.com",
            "1;John;Doe;dr;email@.com"}, delimiter = ';')
    void updateLecturer_invalidInput_returnsStatus400(Long id,
                                                      String firstName,
                                                      String lastName,
                                                      String title,
                                                      String email) throws Exception {
        // given
        UpdateLecturerRequest updateLecturerRequest = UpdateLecturerRequest.builder()
                .id(id)
                .firstName(firstName)
                .lastName(lastName)
                .title(title)
                .email(email)
                .build();
        Lecturer lecturer = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();
        lecturerRepository.save(lecturer);

        // when
        String response = mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateLecturerRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(lecturerRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 400);
        assertFalse(exceptionResponse.getMessage().isBlank());
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
    }

    @Test
    void updateLecturer_lecturerWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        UpdateLecturerRequest updateLecturerRequest = UpdateLecturerRequest.builder()
                .id(500000L)
                .firstName("John")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();

        Lecturer lecturer = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("professor")
                .email("examplex@mail.com")
                .build();
        lecturerRepository.save(lecturer);

        // when
        String response = mockMvc.perform(put(BASE_URL)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateLecturerRequest)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(lecturerRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

    @Test
    void getLecturerById_existingLecturerWithGivenId_returnsLecturerAndStatus200() throws Exception {
        // given
        Lecturer lecturer1 = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example2@mail.com")
                .build();
        Lecturer lecturer2 = Lecturer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .title("Dr")
                .email("example1@mail.com")
                .build();
        Lecturer lecturer3 = Lecturer.builder()
                .firstName("Bob")
                .lastName("Smith")
                .title("professor")
                .email("example3@mail.com")
                .build();

        lecturerRepository.save(lecturer1);
        lecturerRepository.save(lecturer2);
        lecturerRepository.save(lecturer3);

        LecturerDto expectedLecturerDto = lecturer2.toDto();

        // when
        String responseContent = mockMvc.perform(get(BASE_URL + "/" + lecturer2.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        LecturerDto foundedLecturerByIdDto = objectMapper.readValue(responseContent, LecturerDto.class);
        assertThat(expectedLecturerDto).isEqualTo(foundedLecturerByIdDto);
    }

    @Test
    void getLecturerById_lecturerWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        Lecturer lecturer = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();
        lecturerRepository.save(lecturer);
        long notExistingLecturerId = lecturer.getId() + 1;

        // when
        String response = mockMvc.perform(get(BASE_URL + "/" + notExistingLecturerId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(lecturerRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL + "/" + notExistingLecturerId);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

    @Test
    void getAllLecturers_lecturersExistsInDb_returnsLecturersListAndStatus200() throws Exception {
        // given
        Lecturer lecturer1 = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();
        Lecturer lecturer2 = Lecturer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .title("Dr")
                .email("example1@mail.com")
                .build();
        Lecturer lecturer3 = Lecturer.builder()
                .firstName("Bob")
                .title("Dr")
                .email("example2@mail.com")
                .build();
        lecturerRepository.save(lecturer1);
        lecturerRepository.save(lecturer2);
        lecturerRepository.save(lecturer3);
        List<LecturerDto> expectedLecturersDto = List.of(lecturer1.toDto(), lecturer2.toDto(), lecturer3.toDto());

        // when
        String responseContent = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        List<LecturerDto> foundedLecturerByIdDto = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertThat(foundedLecturerByIdDto).containsAll(expectedLecturersDto);
    }

    @Test
    void getAllLecturers_noLecturersExistsInDb_returnsEmptyListAndStatus200() throws Exception {
        // given // when
        String responseContent = mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        List<LecturerDto> foundedLecturerByIdDto = objectMapper.readValue(responseContent, new TypeReference<>() {
        });
        assertThat(foundedLecturerByIdDto).isEmpty();
    }

    @Test
    void deleteLecturerById_existingLecturerWithGivenId_deletesLecturerAndStatus204() throws Exception {
        // given
        Lecturer lecturer1 = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example1@mail.com")
                .build();
        Lecturer lecturer2 = Lecturer.builder()
                .firstName("Jane")
                .lastName("Doe")
                .title("Dr")
                .email("example2@mail.com")
                .build();
        Lecturer lecturer3 = Lecturer.builder()
                .firstName("Bob")
                .lastName("Smith")
                .title("professor")
                .email("example3@mail.com")
                .build();
        lecturerRepository.save(lecturer1);
        lecturerRepository.save(lecturer2);
        lecturerRepository.save(lecturer3);
        long lecturerToDeleteId = lecturer2.getId();

        // when
        mockMvc.perform(delete(BASE_URL + "/" + lecturerToDeleteId))
                .andExpect(status().isNoContent());

        // then
        assertFalse(lecturerRepository.existsById(lecturerToDeleteId));
        assertEquals(lecturerRepository.findAll().size(), 2);
        assertThat(lecturerRepository.findAll()).containsExactlyInAnyOrder(lecturer1, lecturer3);
    }

    @Test
    void deleteLecturerById_lecturerWithGivenIdNotExists_returnsStatus404() throws Exception {
        // given
        Lecturer lecturer = Lecturer.builder()
                .firstName("Johny")
                .lastName("Doe")
                .title("Dr")
                .email("example@mail.com")
                .build();
        lecturerRepository.save(lecturer);
        long notExistingLecturerId = lecturer.getId() + 1;

        // when
        String response = mockMvc.perform(delete(BASE_URL + "/" + notExistingLecturerId))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // then
        ExceptionResponse exceptionResponse = objectMapper.readValue(response, ExceptionResponse.class);
        assertEquals(lecturerRepository.findAll().size(), 1);
        assertNotNull(exceptionResponse);
        assertEquals(exceptionResponse.getHttpStatusCode(), 404);
        assertEquals(exceptionResponse.getRequestUri(), BASE_URL + "/" + notExistingLecturerId);
        assertFalse(exceptionResponse.getMethod().isBlank());
        assertTrue(exceptionResponse.getMessage().contains("not found"));
    }

}