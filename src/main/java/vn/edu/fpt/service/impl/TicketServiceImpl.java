package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.*;
import vn.edu.fpt.repository.SeatLockRepository;
import vn.edu.fpt.repository.TicketRepository;
import vn.edu.fpt.repository.TicketTypeRepository;
import vn.edu.fpt.service.TicketService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("TicketService")
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final SeatLockRepository  seatLockRepository;
    @Override
    public long issuedTickets() {
        return this.ticketRepository.ticketIssued();
    }

    @Override
    public void generateTicketsForOrder(Order order) {
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            Seat seat = orderDetail.getSeat();

            String ticketCode = "TKT-" + UUID.randomUUID().toString().toUpperCase();
            String qrCode = "EVH-QR-" +  UUID.randomUUID().toString().toUpperCase();

            Ticket ticket = new Ticket();
            ticket.setTicketCode(ticketCode);
            ticket.setOrderDetail(orderDetail);
            ticket.setSeat(seat);
            ticket.setQrCode(qrCode);
            ticket.setCheckedIn(false);
            ticket.setCheckedInAt(null);
            this.ticketRepository.save(ticket);

            TicketType ticketType = seat.getTicketType();
            ticketType.setSoldQuantity(ticketType.getSoldQuantity() + 1);
            ticketTypeRepository.save(ticketType);
        }
    }

    @Override
    public Ticket handleSaveTicket(Ticket ticket) {
        return null;
    }

    @Override
    public void handleSaveTicketByOrder(Order order) {
        List<Long> seatIds = new ArrayList<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            Seat seat = detail.getSeat();
            if (seat != null) {
                seatIds.add(seat.getSeatId());

                Ticket ticket = Ticket.builder()
                        .seat(seat)
                        .ticketCode("EVH-TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                        .qrCode("TICKET_QR_" + UUID.randomUUID().toString())
                        .isCheckedIn(false)
                        .build();
                ticket.setOrderDetail(detail);
                this.ticketRepository.save(ticket);
            }
        }
        if (!seatIds.isEmpty()) {
            seatLockRepository.deleteAllBySeatIdIn(seatIds);
        }
    }


}
