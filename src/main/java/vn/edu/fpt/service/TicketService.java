package vn.edu.fpt.service;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Ticket;

public interface TicketService {
    long issuedTickets();
    @Transactional
    void generateTicketsForOrder(Order order);
    Ticket handleSaveTicket(Ticket ticket);
    void handleSaveTicketByOrder(Order order);

    long countAllTicketOfUser(@Param("userId") Long userId);
    long countUpcomingTicket(@Param("userId") Long userId);
    long countUsedTicket(@Param("userId") Long userId);
    long countExpiredTicket(@Param("userId") Long userId);

    Ticket findById(Long orderId);

}
