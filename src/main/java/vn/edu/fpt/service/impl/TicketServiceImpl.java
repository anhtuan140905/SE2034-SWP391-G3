package vn.edu.fpt.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.repository.TicketRepository;
import vn.edu.fpt.service.TicketService;

@Service("TicketService")
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {
    private final TicketRepository ticketRepository;
    @Override
    public long issuedTickets() {
        return this.ticketRepository.ticketIssued();
    }
}
