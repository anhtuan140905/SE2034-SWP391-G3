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

    @Transactional(readOnly = true) // Thêm readOnly để Hibernate không tốn tài nguyên quản lý thực thể
    @Override
    public List<TicketTypeSeatsDTO> getSeatMap(Long eventId) {
        Instant now = Instant.now();
        // 1 câu Query duy nhất lấy sạch mọi thứ đã kết nối
        List<Seat> seats = seatRepository.findAllByEventIdWithStatus(eventId, now);

        if (seats.isEmpty()) return new ArrayList<>();

        // Gom nhóm theo ID loại vé (lúc này TicketType đã được FETCH nên chạy cực mượt, không sinh thêm SQL)
        Map<Long, List<Seat>> seatsByTicketType = seats.stream()
                .collect(Collectors.groupingBy(s -> s.getTicketType().getTicketTypeId()));

        List<TicketTypeSeatsDTO> result = new ArrayList<>();

        for (Map.Entry<Long, List<Seat>> entry : seatsByTicketType.entrySet()) {
            List<Seat> zoneSeats = entry.getValue();
            TicketType ticketType = zoneSeats.get(0).getTicketType();

            List<SeatStatusDTO> seatDTOs = new ArrayList<>();
            for (Seat s : zoneSeats) {

                // TỰ QUYẾT ĐỊNH TRẠNG THÁI DỰA TRÊN DỮ LIỆU ĐÃ FETCH
                String status = "AVAILABLE";
                if (s.getTicket() != null) {
                    status = "SOLD";
                } else if (s.getSeatLocks() != null && !s.getSeatLocks().isEmpty()) {
                    // Kiểm tra xem có lock nào chưa hết hạn trực tiếp trong Object
                    boolean isLocked = s.getSeatLocks().stream()
                            .anyMatch(lock -> lock.getEvent().getEventId().equals(eventId) && lock.getExpiresAt().isAfter(now));
                    if (isLocked) status = "LOCKED";
                }

                SeatStatusDTO seatDTO = SeatStatusDTO.builder()
                        .seatId(s.getSeatId())
                        .rowLabel(s.getRowLabel())
                        .seatNumber(s.getSeatNumber())
                        .status(status)
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

        result.sort(Comparator.comparingInt(TicketTypeSeatsDTO::getDisplayOrder));
        return result;
    }

    private String resolveStatus(Seat seat, Set<Long> lockedIds) {
        if (seat.getTicket() != null) return "SOLD";
        if (lockedIds.contains(seat.getSeatId())) return "LOCKED";
        return "AVAILABLE";
    }
}