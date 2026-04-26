package com.ticketmanager.service;

import com.ticketmanager.exception.TicketNotFoundException;
import com.ticketmanager.model.Status;
import com.ticketmanager.model.Ticket;
import com.ticketmanager.repository.TicketRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    public List<Ticket> getAllTickets() {
        return repository.findAll();
    }

    public Ticket getTicketById(String id) {
        return repository.findById(id).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
    }

    public Ticket createTicket(Ticket ticket) {
        return repository.save(ticket);
    }

    public void deleteTicket(String id) {
        if (!repository.existsById(id)) {
            throw new TicketNotFoundException("Ticket not found");
        }
        repository.deleteById(id);
    }

    public Ticket updateTicket(String id, Ticket updatedTicket) {
        return repository.findById(id)
                .map(ticket -> {
                    ticket.setTitle(updatedTicket.getTitle());
                    ticket.setDescription(updatedTicket.getDescription());
                    return repository.save(ticket);
                })
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
    }

    public Ticket updateStatus(String id, Status status) {
        return repository.findById(id).map(ticket -> {
            ticket.setStatus(status);
            return repository.save(ticket);
        }).orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
    }
}