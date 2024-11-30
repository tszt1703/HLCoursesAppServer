package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.dto.UserDTO;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.service.SpecialistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecialistService specialistService;  // Мокируем SpecialistService

    @MockBean
    private ListenerService listenerService;      // Мокируем ListenerService

    @Mock
    private Authentication authentication;        // Мокируем объект Authentication

    @BeforeEach
    public void setUp() {
        // В этом случае Mockito создаст и инжектит мокированные зависимости через @MockBean
        authentication = Mockito.mock(Authentication.class);  // Мокируем Authentication
    }

    @Test
    public void shouldUpdateSpecialistSuccessfully() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setEmail("testuse2@example.com");

        // Мокируем авторизацию
        when(authentication.getName()).thenReturn("testuser2@example.com");

        // Мокируем проверку авторизации и обновление специалиста
        when(specialistService.isUserAuthorizedToUpdate(anyLong(), anyString())).thenReturn(true);
        doNothing().when(specialistService).updateSpecialist(anyLong(), Mockito.any());

        mockMvc.perform(put("/users/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"testuse2@example.com\" }")
                        .param("userType", "specialist")
                        .principal(authentication))  // Проверьте, что тут правильно передается authentication
                .andExpect(status().isOk()) // Ожидаем 200 OK
                .andExpect(jsonPath("$").value("Specialist successfully updated."));
    }


    @Test
    public void shouldReturn403ForUnauthorizedSpecialistUpdate() throws Exception {
        when(authentication.getName()).thenReturn("johndoe@example.com");
        when(specialistService.isUserAuthorizedToUpdate(anyLong(), anyString())).thenReturn(false);

        mockMvc.perform(put("/users/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"firstName\": \"John\", \"lastName\": \"Doe\", \"email\": \"johndoe@example.com\" }")
                        .param("userType", "specialist")
                        .principal(authentication))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$").value("You are not authorized to update this user."));
    }
}
