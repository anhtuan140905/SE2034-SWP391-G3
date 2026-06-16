package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.edu.fpt.modelview.response.booking.TicketTypeSeatsDTO;
import vn.edu.fpt.service.SeatMapService;

import java.util.List;

// Controller 2: REST API trả JSON cho JS fetch()
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SeatMapApiController {

    private final SeatMapService seatMapService;

    @GetMapping("/{eventId}/seat-map")
    public ResponseEntity<List<TicketTypeSeatsDTO>> getSeatMap(
            @PathVariable Long eventId) {
        return ResponseEntity.ok(seatMapService.getSeatMap(eventId));
    }
}