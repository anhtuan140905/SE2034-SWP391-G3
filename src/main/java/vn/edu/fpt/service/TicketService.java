package vn.edu.fpt.service;

import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Ticket;

public interface TicketService {
    long issuedTickets();
    @Transactional
    void generateTicketsForOrder(Order order);
    Ticket handleSaveTicket(Ticket ticket);
    void handleSaveTicketByOrder(Order order);
}
