package vn.edu.fpt.service;

import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Ticket;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.modelview.response.organizer.TicketTypeCheckinDto;

import java.util.List;

public interface TicketService {
    List<TicketTypeCheckinDto> getDetailCheckinByTicketType(Long  eventId);
    Long countTicketCheckInByEvent(Long eventID);
    Integer countAllticketSelledOfEvent(Long eventId);
    long issuedTickets();
    @Transactional
    void generateTicketsForOrder(Order order);
    Ticket handleSaveTicket(Ticket ticket);
    void handleSaveTicketByOrder(Order order);

    long countAllSoldTicket();
    long countAllTicketOfUser(@Param("userId") Long userId);
    long countUpcomingTicket(@Param("userId") Long userId);
    long countUsedTicket(@Param("userId") Long userId);
    long countExpiredTicket(@Param("userId") Long userId);

    Ticket findById(Long orderId);
    List<TicketDTO> viewTicket (Long userId, String tab);
    TicketDTO viewDetailTicket(Long ticketId, Long userId);
    long countCompletedTicketsByUserAndEvent(Long userId, Long eventId);
    List<Object[]> countSoldTicketsByEventIds(List<Long> eventIds);
}
