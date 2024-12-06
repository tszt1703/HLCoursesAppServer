package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.HlCoursesAppServerApplication;
import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.model.Listener;
import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.example.hlcoursesappserver.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private SpecialistService specialistService;

    @MockBean
    private ListenerService listenerService;

    @MockBean
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void testCreateSpecialist() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Specialist");
        Specialist specialist = new Specialist("test@domain.com", "encodedPassword");
        when(userMapper.toSpecialist(userDTO)).thenReturn(specialist);
        when(specialistService.createUser(specialist)).thenReturn(specialist);
        when(userMapper.toUserDTO(specialist, "Specialist")).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/specialist")
                        .contentType("application/json")
                        .content("{\"email\":\"test@domain.com\",\"password\":\"password\",\"role\":\"Specialist\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("Specialist"));

        verify(specialistService, times(1)).createUser(specialist);
    }

    @Test
    void testCreateListener() throws Exception {
        UserDTO userDTO = new UserDTO(3L, "Listener");
        Listener listener = new Listener("testuser@example.com", "password123");
        when(userMapper.toListener(userDTO)).thenReturn(listener);
        when(listenerService.createUser(listener)).thenReturn(listener);
        when(userMapper.toUserDTO(listener, "Listener")).thenReturn(userDTO);

        mockMvc.perform(post("/api/users/listener")
                        .contentType("application/json")
                        .content("{\"email\":\"test@domain.com\",\"password\":\"password\",\"role\":\"Listener\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("Listener"));

        verify(listenerService, times(1)).createUser(listener);
    }

    @Test
    void testGetUserByEmailSpecialist() throws Exception {
        Specialist specialist = new Specialist("test@domain.com", "encodedPassword");
        specialist.setSpecialistId(1L);
        when(specialistService.getUserByEmail("test@domain.com")).thenReturn(Optional.of(specialist));
        when(userMapper.toUserDTO(specialist, "Specialist")).thenReturn(new UserDTO(1L, "Specialist"));

        mockMvc.perform(get("/api/users/test@domain.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("Specialist"));
    }

    @Test
    void testGetUserByEmailListener() throws Exception {
        Listener listener = new Listener("test@domain.com", "encodedPassword");
        listener.setListenerId(1L);
        when(listenerService.getUserByEmail("test@domain.com")).thenReturn(Optional.of(listener));
        when(userMapper.toUserDTO(listener, "Listener")).thenReturn(new UserDTO(1L, "Listener"));

        mockMvc.perform(get("/api/users/test@domain.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.role").value("Listener"));
    }

    @Test
    void testGetUserByEmailNotFound() throws Exception {
        when(specialistService.getUserByEmail("test@domain.com")).thenReturn(Optional.empty());
        when(listenerService.getUserByEmail("test@domain.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/test@domain.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateSpecialist() throws Exception {
        Specialist specialist = new Specialist("test@domain.com", "encodedPassword");
        specialist.setSpecialistId(1L);
        when(specialistService.isUserAuthorizedToUpdate(1L, "test@domain.com")).thenReturn(true);
        when(userMapper.toSpecialist(any(UserDTO.class))).thenReturn(specialist);

        mockMvc.perform(put("/api/users/1")
                        .header("email", "test@domain.com")
                        .contentType("application/json")
                        .content("{\"email\":\"newEmail@domain.com\",\"password\":\"newPassword\",\"role\":\"Specialist\"}"))
                .andExpect(status().isOk());

        verify(specialistService, times(1)).updateUser(1L, specialist);
    }

    @Test
    void testUpdateListener() throws Exception {
        Listener listener = new Listener("test@domain.com", "encodedPassword");
        listener.setListenerId(1L);
        when(listenerService.isUserAuthorizedToUpdate(1L, "test@domain.com")).thenReturn(true);
        when(userMapper.toListener(any(UserDTO.class))).thenReturn(listener);

        mockMvc.perform(put("/api/users/1")
                        .header("email", "test@domain.com")
                        .contentType("application/json")
                        .content("{\"email\":\"newEmail@domain.com\",\"password\":\"newPassword\",\"role\":\"Listener\"}"))
                .andExpect(status().isOk());

        verify(listenerService, times(1)).updateUser(1L, listener);
    }

    @Test
    void testDeleteSpecialist() throws Exception {
        when(specialistService.isUserAuthorizedToDelete(3L, "testuser@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/3")
                        .header("email", "test@domain.com")
                        .param("role", "Specialist"))
                .andExpect(status().isOk());

        verify(specialistService, times(1)).deleteUser(1L);
    }

    @Test
    void testDeleteListener() throws Exception {
        when(listenerService.isUserAuthorizedToDelete(3L, "testuser@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/3")
                        .header("email", "testuser@example.com")
                        .param("role", "Listener"))
                .andExpect(status().isOk());

        verify(listenerService, times(1)).deleteUser(1L);
    }
}
