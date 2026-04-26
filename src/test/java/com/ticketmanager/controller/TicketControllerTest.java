package com.ticketmanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketmanager.dto.TicketRequest;
import com.ticketmanager.exception.GlobalExceptionHandler;
import com.ticketmanager.exception.TicketNotFoundException;
import com.ticketmanager.model.Status;
import com.ticketmanager.model.Ticket;
import com.ticketmanager.security.JwtService;
import com.ticketmanager.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TicketController.class)
@Import(GlobalExceptionHandler.class)
@WithMockUser
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService service;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private org.springframework.security.core.userdetails.UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket("Bug login", "Le login ne fonctionne pas");
        ticket.setStatus(Status.OPEN);
    }

    @Test
    void getAll_shouldReturn200WithList() throws Exception {
        when(service.getAllTickets()).thenReturn(List.of(ticket));

        mockMvc.perform(get("/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Bug login"));
    }

    @Test
    void getById_shouldReturn200() throws Exception {
        when(service.getTicketById("1")).thenReturn(ticket);

        mockMvc.perform(get("/tickets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Bug login"));
    }

    @Test
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(service.getTicketById("999")).thenThrow(new TicketNotFoundException("Ticket not found"));

        mockMvc.perform(get("/tickets/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Ticket not found"));
    }

    @Test
    void create_shouldReturn201() throws Exception {
        TicketRequest request = new TicketRequest();
        request.setTitle("Bug login");
        request.setDescription("Le login ne fonctionne pas");
        when(service.createTicket(any())).thenReturn(ticket);

        mockMvc.perform(post("/tickets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Bug login"));
    }

    @Test
    void create_shouldReturn400WhenTitleMissing() throws Exception {
        TicketRequest request = new TicketRequest();
        request.setDescription("Une description");

        mockMvc.perform(post("/tickets")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        doNothing().when(service).deleteTicket("1");

        mockMvc.perform(delete("/tickets/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_shouldReturn404WhenNotFound() throws Exception {
        doThrow(new TicketNotFoundException("Ticket not found")).when(service).deleteTicket("999");

        mockMvc.perform(delete("/tickets/999").with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateStatus_shouldReturn200() throws Exception {
        when(service.updateStatus("1", Status.IN_PROGRESS)).thenReturn(ticket);

        mockMvc.perform(patch("/tickets/1/status")
                .with(csrf())
                .param("status", "IN_PROGRESS"))
                .andExpect(status().isOk());
    }
}