package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.modelview.response.booking.TicketTypeSeatsDTO;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.CheckoutService;
import vn.edu.fpt.service.SeatMapService;

import java.util.List;

// Controller 2: REST API trả JSON cho JS fetch()
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class SeatMapApiController {

    private final SeatMapService seatMapService;
    private final CheckoutService checkoutService;

    @GetMapping("/{eventId}/seat-map")
    public ResponseEntity<List<TicketTypeSeatsDTO>> getSeatMap(
            @PathVariable Long eventId,
            @AuthenticationPrincipal AuthenticatedUser userDetails) {
        Long currentUserId = (userDetails != null) ? userDetails.getUser().getId() : null;
        return ResponseEntity.ok(seatMapService.getSeatMap(eventId, currentUserId));
    }


}