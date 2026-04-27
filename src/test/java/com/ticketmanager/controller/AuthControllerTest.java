package com.ticketmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmanager.dto.AuthResponse;
import com.ticketmanager.dto.UserRequest;
import com.ticketmanager.exception.GlobalExceptionHandler;
import com.ticketmanager.model.User;
import com.ticketmanager.repository.UserRepository;
import com.ticketmanager.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    private UserRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new UserRequest();
        validRequest.setUsername("thomas");
        validRequest.setEmail("thomas@test.com");
        validRequest.setPassword("motdepasse123");
    }

    @Test
    void signup_shouldReturn201WithToken() throws Exception {
        when(userRepository.existsByEmail("thomas@test.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any())).thenReturn(new User());
        when(jwtService.generateToken(anyString())).thenReturn("fake-token");

        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }

    @Test
    void signup_shouldReturn409WhenEmailAlreadyExists() throws Exception {
        when(userRepository.existsByEmail("thomas@test.com")).thenReturn(true);

        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void signup_shouldReturn400WhenUsernameMissing() throws Exception {
        UserRequest request = new UserRequest();
        request.setEmail("thomas@test.com");
        request.setPassword("motdepasse123");

        mockMvc.perform(post("/auth/signup")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void signin_shouldReturn200WithToken() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("thomas@test.com", "motdepasse123"));
        when(jwtService.generateToken("thomas@test.com")).thenReturn("fake-token");

        mockMvc.perform(post("/auth/signin")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"));
    }
}