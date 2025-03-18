package org.example.hlcoursesappserver.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.example.hlcoursesappserver.service.CustomUserDetailsService;
import org.example.hlcoursesappserver.util.JwtUtil;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        String validJwt = "mocked-jwt-token";
        when(jwtUtil.generateAccessToken(1L, "Listener")).thenReturn(validJwt);
        when(userDetailsService.loadUserByUserId(1L, "Listener")).thenReturn(mock(org.springframework.security.core.userdetails.UserDetails.class));
        when(jwtUtil.validateToken(validJwt)).thenReturn(true);
        when(jwtUtil.extractUserId(validJwt)).thenReturn(1L);
        when(jwtUtil.extractRole(validJwt)).thenReturn("Listener");
    }

    @Test
    void testCorsConfiguration() throws Exception {
        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET")
                        .header("Access-Control-Request-Headers", "Content-Type, Authorization"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"))
                .andExpect(header().string("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE"))
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }

    @Test
    void testCorsInvalidOrigin() throws Exception {
        mockMvc.perform(options("/auth/login")
                        .header("Origin", "http://invalid.com")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isForbidden())
                .andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
    }
}