package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.model.Specialist;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.mockito.ArgumentMatchers.anyLong;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private SpecialistService specialistService;

    @InjectMocks
    private UserController userController;

    private Specialist mockSpecialist;

    @BeforeEach
    public void setUp() {
        mockSpecialist = new Specialist();
        mockSpecialist.setFirstName("John");
        mockSpecialist.setLastName("Doe");
        mockSpecialist.setEmail("johndoe@example.com");
    }

    @Test
    public void shouldReturnSpecialistById() throws Exception {
        // Мокируем поведение сервиса
        when(specialistService.getSpecialistById(anyLong())).thenReturn(mockSpecialist);

        mockMvc.perform(get("/api/users/specialist/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.email").value("johndoe@example.com"));
    }
}

