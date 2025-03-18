package org.example.hlcoursesappserver.config;

import org.example.hlcoursesappserver.service.CustomUserDetailsService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    private String validJwt;

    @BeforeEach
    void setUp() {
        validJwt = "mocked-jwt-token";
        when(jwtUtil.generateAccessToken(1L, "Listener")).thenReturn(validJwt);
        when(userDetailsService.loadUserByUserId(1L, "Listener")).thenReturn(mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtUtil.validateToken(validJwt)).thenReturn(true);
        when(jwtUtil.extractUserId(validJwt)).thenReturn(1L);
        when(jwtUtil.extractRole(validJwt)).thenReturn("Listener");
    }

    @Test
    void testPublicEndpointAccessible() throws Exception {
        mockMvc.perform(post("/auth/login")) // Изменено на POST
                .andExpect(status().isOk());
    }
}