package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Seat;
import vn.edu.fpt.model.TicketType;
import vn.edu.fpt.modelview.response.booking.SeatStatusDTO;
import vn.edu.fpt.modelview.response.booking.TicketTypeSeatsDTO;
import vn.edu.fpt.repository.SeatRepository;
import vn.edu.fpt.service.SeatMapService;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service("SeatMapService")
@RequiredArgsConstructor
public class SeatMapServiceImpl implements SeatMapService {
    private final SeatRepository seatRepository;

    @Transactional
    @Override
    public List<TicketTypeSeatsDTO> getSeatMap(Long eventId) {
        List<Seat> seats = seatRepository.findAllByEventIdWithStatus(eventId, Instant.now());
        Set<Long> lockedIds = seatRepository.findLockedSeatIds(eventId, Instant.now());

        Map<Long, List<Seat>> seatsByTicketType = seats.stream().collect(Collectors.groupingBy(s -> s.getTicketType().getTicketTypeId()));

        List<TicketTypeSeatsDTO> result = new ArrayList<>();

        for (Map.Entry<Long, List<Seat>> entry : seatsByTicketType.entrySet()) {
            TicketType ticketType = entry.getValue().get(0).getTicketType();
            List<Seat> zoneSeats = entry.getValue();

            List<SeatStatusDTO> seatDTOs = new ArrayList<>();
            for (Seat s : zoneSeats) {
                SeatStatusDTO seatDTO = SeatStatusDTO.builder()
                        .seatId(s.getSeatId())
                        .rowLabel(s.getRowLabel())
                        .seatNumber(s.getSeatNumber())
                        .status(resolveStatus(s, lockedIds))
                        .build();
                seatDTOs.add(seatDTO);
            }
            TicketTypeSeatsDTO ticketTypeSeatsDTO = TicketTypeSeatsDTO.builder()
                    .ticketTypeId(ticketType.getTicketTypeId())
                    .zoneName(ticketType.getZoneName())
                    .price(ticketType.getPrice())
                    .totalQuantity(ticketType.getTotalQuantity())
                    .soldQuantity(ticketType.getSoldQuantity())
                    .displayOrder(ticketType.getDisplayOrder())
                    .seats(seatDTOs)
                    .build();
            result.add(ticketTypeSeatsDTO);
        }
        result.sort(Comparator.comparingInt(TicketTypeSeatsDTO :: getDisplayOrder));

        return result;
    }

    private String resolveStatus(Seat seat, Set<Long> lockedIds) {
        if (seat.getTicket() != null) return "SOLD";
        if (lockedIds.contains(seat.getSeatId())) return "LOCKED";
        return "AVAILABLE";
    }
}