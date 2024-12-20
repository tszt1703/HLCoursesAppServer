package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.mapper.UserMapper;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecialistService specialistService;

    @MockBean
    private ListenerService listenerService;

    @MockBean
    private UserMapper userMapper;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllUsers() throws Exception {
        Mockito.when(specialistService.getAllUsers()).thenReturn(Collections.emptyList());
        Mockito.when(listenerService.getAllUsers()).thenReturn(Collections.emptyList());
        Mockito.when(userMapper.toUserDTOList(Collections.emptyList(), Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    @WithMockUser(username = "testuser5@example.com", roles = {"SPECIALIST"})
    void testCreateSpecialist() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setRole("Specialist");
        userDTO.setEmail("test@gmail.com");
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setBirthDate(LocalDate.of(1990, 1, 1));

        Specialist specialist = new Specialist();

        Mockito.when(userMapper.toSpecialist(Mockito.any(UserDTO.class))).thenReturn(specialist);
        Mockito.when(specialistService.createUser(Mockito.any(Specialist.class))).thenReturn(specialist);
        Mockito.when(userMapper.toUserDTO(Mockito.any(Specialist.class), Mockito.eq("Specialist"))).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/specialist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()) // Добавляем CSRF токен
                        .content("""
                            {
                                "role": "Specialist",
                                "email": "test@gmail.com",
                                "firstName": "John",
                                "lastName": "Doe",
                                "birthDate": "1990-01-01"
                            }
                            """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.role").value("Specialist"))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetUserByEmail() throws Exception {
        Specialist specialist = new Specialist();

        UserDTO userDTO = new UserDTO();
        userDTO.setRole("Specialist");
        userDTO.setEmail("test@gmail.com");
        userDTO.setFirstName("Test");
        userDTO.setLastName("User");

        Mockito.when(specialistService.getUserByEmail("test@gmail.com")).thenReturn(Optional.of(specialist));
        Mockito.when(userMapper.toUserDTO(specialist, "Specialist")).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/test@gmail.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.role").value("Specialist"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateSpecialist() throws Exception {
        Mockito.when(specialistService.isUserAuthorizedToUpdate(1L, "test@gmail.com")).thenReturn(true);

        mockMvc.perform(put("/api/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()) // Добавляем CSRF токен
                        .header("email", "test@gmail.com")
                        .content("""
                                {
                                    "role": "Specialist",
                                    "email": "newemail@gmail.com",
                                    "name": "Updated User"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testDeleteSpecialist() throws Exception {
        Mockito.when(specialistService.isUserAuthorizedToDelete(1L, "test@gmail.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/1")
                        .with(csrf()) // Добавляем CSRF токен
                        .header("email", "test@gmail.com")
                        .param("role", "Specialist"))
                .andExpect(status().isOk());
    }
}