package com.ticketmanager.service;

import com.ticketmanager.exception.TicketNotFoundException;
import com.ticketmanager.model.Status;
import com.ticketmanager.model.Ticket;
import com.ticketmanager.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketRepository repository;

    @InjectMocks
    private TicketService service;

    private Ticket ticket;

    @BeforeEach
    void setUp() {
        ticket = new Ticket("Bug login", "Le login ne fonctionne pas");
        ticket.setStatus(Status.OPEN);
    }

    @Test
    void getAllTickets_shouldReturnList() {
        when(repository.findAll()).thenReturn(List.of(ticket));

        List<Ticket> result = service.getAllTickets();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Bug login");
    }

    @Test
    void getTicketById_shouldReturnTicket() {
        when(repository.findById("1")).thenReturn(Optional.of(ticket));

        Ticket result = service.getTicketById("1");

        assertThat(result.getTitle()).isEqualTo("Bug login");
    }

    @Test
    void getTicketById_shouldThrowWhenNotFound() {
        when(repository.findById("999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getTicketById("999"))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessage("Ticket not found");
    }

    @Test
    void createTicket_shouldSaveAndReturn() {
        when(repository.save(ticket)).thenReturn(ticket);

        Ticket result = service.createTicket(ticket);

        assertThat(result.getTitle()).isEqualTo("Bug login");
        verify(repository, times(1)).save(ticket);
    }

    @Test
    void deleteTicket_shouldDeleteWhenExists() {
        when(repository.existsById("1")).thenReturn(true);

        service.deleteTicket("1");

        verify(repository, times(1)).deleteById("1");
    }

    @Test
    void deleteTicket_shouldThrowWhenNotFound() {
        when(repository.existsById("999")).thenReturn(false);

        assertThatThrownBy(() -> service.deleteTicket("999"))
                .isInstanceOf(TicketNotFoundException.class);
    }

    @Test
    void updateTicket_shouldUpdateFields() {
        Ticket updated = new Ticket("Nouveau titre", "Nouvelle description");
        when(repository.findById("1")).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenReturn(ticket);

        Ticket result = service.updateTicket("1", updated);

        verify(repository, times(1)).save(any());
    }

    @Test
    void updateStatus_shouldChangeStatus() {
        when(repository.findById("1")).thenReturn(Optional.of(ticket));
        when(repository.save(any())).thenReturn(ticket);

        service.updateStatus("1", Status.IN_PROGRESS);

        verify(repository, times(1)).save(argThat(t -> t.getStatus() == Status.IN_PROGRESS));
    }
}