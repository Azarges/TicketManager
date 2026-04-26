package com.ticketmanager.controller;

import com.ticketmanager.dto.TicketRequest;
import com.ticketmanager.model.Status;
import com.ticketmanager.model.Ticket;
import com.ticketmanager.service.TicketService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    // Get all tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        return ResponseEntity.ok(service.getAllTickets());
    }

    // Get a ticket by ID
    @GetMapping("/{id}")
    public ResponseEntity<Ticket> getById(@PathVariable String id) {
        return ResponseEntity.ok(service.getTicketById(id));
    }

    // Create a new ticket
    @PostMapping
    public ResponseEntity<Ticket> create(@Valid @RequestBody TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());
        ticket.setCreatedBy(request.getCreatedBy());
        Ticket savedTicket = service.createTicket(ticket);
        return ResponseEntity.status(201).body(savedTicket);
    }

    // Delete a ticket by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.deleteTicket(id);
        return ResponseEntity.noContent().build();

    }

    // Update a ticket by ID
    @PutMapping("/{id}")
    public ResponseEntity<Ticket> update(@PathVariable String id, @RequestBody TicketRequest request) {
        Ticket ticket = new Ticket();
        ticket.setTitle(request.getTitle());
        ticket.setDescription(request.getDescription());

        return ResponseEntity.ok(service.updateTicket(id, ticket));
    }

    // Patch a ticket status by ID
    @PatchMapping("/{id}/status")
    public ResponseEntity<Ticket> updateStatus(
            @PathVariable String id,
            @RequestParam Status status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

}