package vn.edu.fpt.service;

import vn.edu.fpt.modelview.response.booking.TicketTypeSeatsDTO;

import java.util.List;

public interface SeatMapService {
    public List<TicketTypeSeatsDTO> getSeatMap(Long eventId, Long currentUserId);
}