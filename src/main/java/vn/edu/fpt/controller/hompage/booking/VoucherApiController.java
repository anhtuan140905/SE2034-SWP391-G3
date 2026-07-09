package vn.edu.fpt.controller.hompage.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import vn.edu.fpt.modelview.response.homepage.VoucherDTO;
import vn.edu.fpt.service.AuthenticatedUser;
import vn.edu.fpt.service.VoucherService;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class VoucherApiController {

    private final VoucherService voucherService;

    @GetMapping("/api/events/{eventId}/vouchers")
    public List<VoucherDTO> getAvailableVouchers(@PathVariable Long eventId) {
        return this.voucherService.findAvailableVouchersByEvent(eventId)
                .stream().map(VoucherDTO::from).toList();
    }
    @GetMapping("/api/vouchers/{voucherId}/validate")
    public ResponseEntity<?> validateVoucher(@PathVariable Long voucherId,
                                             @RequestParam Long eventId,
                                             @RequestParam BigDecimal subtotal,
                                             @AuthenticationPrincipal AuthenticatedUser currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var result = voucherService.validate(voucherId, eventId, currentUser.getUser().getId(), subtotal);
        if (!result.isValid()) {
            return ResponseEntity.unprocessableEntity().body(Map.of("errorCode", result.getErrorCode()));
        }
        return ResponseEntity.ok(Map.of("discount", result.getDiscount()));
    }
}