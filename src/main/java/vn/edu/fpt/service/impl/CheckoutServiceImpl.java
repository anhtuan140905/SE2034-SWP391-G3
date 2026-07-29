package vn.edu.fpt.service.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.exception.VoucherValidationException;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.CheckoutService;
import vn.edu.fpt.service.VoucherService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final SeatLockRepository seatLockRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final VoucherService  voucherService;

    private static final long CHECKOUT_TTL_MINUTES = 10;
    private static final int MAX_TICKETS_PER_EVENT = 3;

    @Transactional
    public Long proceedToPayment(List<Long> seatIds, Long voucherId, User currentUser) {
        if (currentUser == null) {
            throw new IllegalArgumentException("Vui lòng đăng nhập trước khi thanh toán");
        }
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Chưa chọn ghế nào");
        }
        if (seatIds.size() > MAX_TICKETS_PER_EVENT) {
            throw new IllegalStateException("Bạn chỉ được mua tối đa 3 vé cho sự kiện này.");
        }
        Instant now = Instant.now();
        Instant newExpiry = now.plus(CHECKOUT_TTL_MINUTES, ChronoUnit.MINUTES);

        List<SeatLock> locks = new ArrayList<>();
        for (Long seatId : seatIds) {

            SeatLock lock = seatLockRepository.findBySeatSeatId(seatId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Ghế số " + seatId + " chưa được giữ trên hệ thống. Vui lòng chọn lại!"));

            if (lock.getUser() != null && !lock.getUser().getId().equals(currentUser.getId())) {
                throw new IllegalStateException("Ghế " + seatId + " đang thuộc quyền giữ của một người dùng khác!");
            }

            if (lock.getExpiresAt().isBefore(now)) {
                throw new IllegalStateException("Ghế " + seatId + " đã quá thời gian giữ tạm thời, vui lòng chọn lại.");
            }

            lock.setExpiresAt(newExpiry);
            locks.add(lock);
        }
        seatLockRepository.saveAll(locks);

        BigDecimal subAmount = BigDecimal.ZERO;
        Event event = locks.get(0).getSeat().getTicketType().getEvent();
        for (SeatLock lock : locks) {
            subAmount = subAmount.add(lock.getSeat().getTicketType().getPrice());
        }

        Voucher appliedVoucher = null;
        BigDecimal discount = BigDecimal.ZERO;

        if (voucherId != null) {
            var result = voucherService.validate(voucherId, event.getEventId(), currentUser.getId(), subAmount);
            if (!result.isValid()) {
                throw new VoucherValidationException(result.getErrorCode());
            }
            appliedVoucher = result.getVoucher();
            discount = result.getDiscount();
        }

        BigDecimal totalAmount = subAmount.subtract(discount);

        Order order = Order.builder()
                .user(currentUser)
                .event(event)
                .totalAmount(totalAmount)
                .voucher(appliedVoucher)
                .discountAmount(discount)
                .status(OrderStatus.PENDING_PAYMENT)
                .expiresAt(newExpiry)
                .build();
        order = orderRepository.save(order);

        Set<OrderDetail> details = new HashSet<>();
        for (SeatLock lock : locks) {
            Seat seat = lock.getSeat();
            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .seat(seat)
                    .unitPrice(seat.getTicketType().getPrice())
                    .build();
            details.add(detail);
        }
        orderDetailRepository.saveAll(details);

        // 5. Tạo Payment (Giữ nguyên code cũ của bạn)
        Payment payment = Payment.builder()
                .order(order)
                .paymentCode("EVH" + order.getOrderId())
                .amount(totalAmount)
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        return order.getOrderId();
    }

    @Transactional
    public void lockSeatTemporarily(Long seatId, User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plus(5, java.time.temporal.ChronoUnit.MINUTES);

        SeatLock existingLock = seatLockRepository.findBySeatSeatId(seatId).orElse(null);

        if (existingLock != null) {
            if (existingLock.getExpiresAt().isAfter(now)
                    && existingLock.getUser() != null
                    && !existingLock.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Ghế này vừa có người khác giữ mất rồi!");
            }

            existingLock.setUser(user);
            existingLock.setExpiresAt(expiresAt);
            existingLock.setLockedAt(now);

            seatLockRepository.save(existingLock);
        } else {
            Seat seat = seatRepository.findById(seatId)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ghế có ID: " + seatId));

            Event event = seat.getTicketType().getEvent();

            SeatLock newLock = SeatLock.builder()
                    .seat(seat)
                    .event(event)
                    .user(user)
                    .lockedAt(now)
                    .expiresAt(expiresAt)
                    .build();

            seatLockRepository.save(newLock);
        }
    }

    @Transactional
    public void unlockSeatTemporarily(Long seatId, User user) {
        seatLockRepository.deleteBySeatSeatIdAndUserId(seatId, user.getId());
    }
    @Override
    public Long resolveEventIdFromSeats(List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("seatIds rỗng");
        }
        SeatLock lock = seatLockRepository.findBySeatSeatId(seatIds.get(0))
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lock cho ghế " + seatIds.get(0)));
        return lock.getSeat().getTicketType().getEvent().getEventId();
    }
}
