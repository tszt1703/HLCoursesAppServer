package org.example.hlcoursesappserver.controller;

import org.example.hlcoursesappserver.service.SpecialistService;
import org.example.hlcoursesappserver.service.ListenerService;
import org.example.hlcoursesappserver.repository.SpecialistRepository;
import org.example.hlcoursesappserver.repository.ListenerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerDeleteTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private SpecialistService specialistService;

    @Mock
    private ListenerService listenerService;

    @Mock
    private SpecialistRepository specialistRepository;

    @Mock
    private ListenerRepository listenerRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testDeleteSpecialist_success() throws Exception {
        // Данные пользователя, который удаляется
        String emailFromToken = "testuser5@example.com";
        Long specialistId = 8L;

        // Заглушка для авторизации пользователя с токеном
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn(emailFromToken);

        // Мокаем проверку в сервисе, что пользователь может удалять этого специалиста
        when(specialistService.isUserAuthorizedToDelete(specialistId, emailFromToken)).thenReturn(true);

        // Выполнение запроса на удаление
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/users/{id}?userType=specialist", specialistId)
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().string("Specialist successfully deleted."));

        // Проверка, что сервис удалил специалиста из базы
        verify(specialistService, times(1)).deleteSpecialist(specialistId);
        assertFalse(specialistRepository.findById(specialistId).isPresent()); // Проверка, что специалист действительно удален
    }


    @Test
    public void testDeleteSpecialist_notAuthorized() throws Exception {
        // Данные пользователя, который пытается удалить
        String emailFromToken = "testuser5@example.com";
        Long specialistId = 1L;

        // Заглушка для авторизации
        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn(emailFromToken);

        // Мокаем проверку, что пользователь не может удалять этого специалиста
        when(specialistService.isUserAuthorizedToDelete(specialistId, emailFromToken)).thenReturn(false);

        // Выполнение запроса на удаление
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/users/{id}?userType=specialist", specialistId)
                        .principal(mockAuth))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You are not authorized to delete this user."));

        // Убедимся, что метод удаления не был вызван
        verify(specialistService, never()).deleteSpecialist(specialistId);
    }

    @Test
    public void testDeleteListener_success() throws Exception {
        String emailFromToken = "testuser@example.com";
        Long listenerId = 2L;

        Authentication mockAuth = mock(Authentication.class);
        when(mockAuth.getName()).thenReturn(emailFromToken);

        when(listenerService.isUserAuthorizedToDelete(listenerId, emailFromToken)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/users/{id}?userType=listener", listenerId)
                        .principal(mockAuth))
                .andExpect(status().isOk())
                .andExpect(content().string("Listener successfully deleted."));

        verify(listenerService, times(1)).deleteListener(listenerId);
    }

    @Test
    public void testDeleteListener_notFound() throws Exception {
        Long listenerId = 2L;

        Authentication mockAuth = mock(Authentication.class);
        String emailFromToken = "testu@example.com";
        when(mockAuth.getName()).thenReturn(emailFromToken);

        when(listenerService.isUserAuthorizedToDelete(listenerId, emailFromToken)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/users/{id}?userType=listener", listenerId)
                        .principal(mockAuth))
                .andExpect(status().isForbidden())
                .andExpect(content().string("You are not authorized to delete this user."));

        verify(listenerService, never()).deleteListener(listenerId);
    }
}
