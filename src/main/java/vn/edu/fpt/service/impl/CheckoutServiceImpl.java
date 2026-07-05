package vn.edu.fpt.service.impl;



import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.repository.*;
import vn.edu.fpt.service.CheckoutService;

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

    private static final long CHECKOUT_TTL_MINUTES = 10;

    @Transactional
    public Long proceedToPayment(List<Long> seatIds, User currentUser) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("Chưa chọn ghế nào");
        }

        Instant now = Instant.now();
        Instant newExpiry = now.plus(CHECKOUT_TTL_MINUTES, ChronoUnit.MINUTES);

        // 1. Validate + nới lỏng cơ chế check SeatLock cho từng ghế
        List<SeatLock> locks = new ArrayList<>();
        for (Long seatId : seatIds) {

            // SỬA TẠI ĐÂY: Chỉ tìm theo seatId để lấy bản ghi lock ra kiểm tra
            SeatLock lock = seatLockRepository.findBySeatSeatId(seatId)
                    .orElseThrow(() -> new IllegalStateException(
                            "Ghế số " + seatId + " chưa được giữ trên hệ thống. Vui lòng chọn lại!"));

            // Kiểm tra xem lock này có phải của chính User đang bấm nút thanh toán không
            if (lock.getUser() != null && !lock.getUser().getId().equals(currentUser.getId())) {
                throw new IllegalStateException("Ghế " + seatId + " đang thuộc quyền giữ của một người dùng khác!");
            }

            // Kiểm tra xem thời hạn giữ ghế tạm thời (5 phút) đã bị quá hạn chưa
            if (lock.getExpiresAt().isBefore(now)) {
                throw new IllegalStateException("Ghế " + seatId + " đã quá thời gian giữ tạm thời, vui lòng chọn lại.");
            }

            // Đạt điều kiện -> Gia hạn thời gian giữ ghế lên 12 phút để sang trang hóa đơn điền thông tin
            lock.setExpiresAt(newExpiry);
            locks.add(lock);
        }
        seatLockRepository.saveAll(locks);

        // 2. Tính tổng tiền (Giữ nguyên code cũ của bạn)
        BigDecimal totalAmount = BigDecimal.ZERO;
        Event event = locks.get(0).getSeat().getTicketType().getEvent();
        for (SeatLock lock : locks) {
            totalAmount = totalAmount.add(lock.getSeat().getTicketType().getPrice());
        }

        // 3. Tạo Order (Giữ nguyên code cũ của bạn)
        Order order = Order.builder()
                .user(currentUser)
                .event(event)
                .totalAmount(totalAmount)
                .status(OrderStatus.PENDING_PAYMENT)
                .expiresAt(newExpiry)
                .build();
        order = orderRepository.save(order);

        // 4. Tạo OrderDetail (Giữ nguyên code cũ của bạn)
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

        // 1. SỬA TẠI ĐÂY: Dùng .orElse(null) thay vì .orElseThrow(null)
        SeatLock existingLock = seatLockRepository.findBySeatSeatId(seatId).orElse(null);

        if (existingLock != null) {
            // 2. SỬA TẠI ĐÂY: Thêm check existingLock.getUser() != null để tránh NullPointerException nếu dữ liệu rác
            if (existingLock.getExpiresAt().isAfter(now)
                    && existingLock.getUser() != null
                    && !existingLock.getUser().getId().equals(user.getId())) {
                throw new IllegalStateException("Ghế này vừa có người khác giữ mất rồi!");
            }

            // Nếu là của mình gia hạn, hoặc lock cũ của người khác ĐÃ HẾT HẠN -> Cập nhật thông tin mới
            existingLock.setUser(user);
            existingLock.setExpiresAt(expiresAt);
            existingLock.setLockedAt(now);

            seatLockRepository.save(existingLock);
        } else {
            // CHƯA AI LOCK -> TẠO MỚI BẢN GHI LOCK
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
        // Khi user bỏ chọn ghế (click lại lần 2), xóa bản ghi lock này đi để người khác chọn
        seatLockRepository.deleteBySeatSeatIdAndUserId(seatId, user.getId());
    }
    }
