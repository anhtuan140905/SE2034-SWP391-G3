package vn.edu.fpt.service.impl;

import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentMethod;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.service.TicketService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final int MAX_TICKETS_PER_EVENT = 3;

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SeatLockRepository seatLockRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketService ticketService;
    private final VoucherUsageRepository voucherUsageRepository;


    @Transactional
    public boolean confirmPayment(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xác nhận order này");
        }

        return confirmPaymentInternal(order);
    }


    @Transactional
    public boolean confirmPaymentByGateway(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order không tồn tại"));

        return confirmPaymentInternal(order);
    }

    @Transactional
    public void failPaymentByGateway(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalStateException("Order không tồn tại"));

        // Callback thất bại đến muộn không được phép làm hỏng đơn đã thanh toán.
        if (order.getStatus() == OrderStatus.PAID) {
            return;
        }

        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Order chưa có Payment"));

        payment.setStatus(PaymentStatus.FAILED);
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        List<Long> seatIds = order.getOrderDetails().stream()
                .map(detail -> detail.getSeat().getSeatId())
                .collect(Collectors.toList());
        if (!seatIds.isEmpty()) {
            seatLockRepository.deleteBySeatSeatIdIn(seatIds);
        }
    }

    private boolean confirmPaymentInternal(Order order) {
        if (order.getStatus() == OrderStatus.PAID) {
            return true; // chặn double-click / double callback
        }
        if (order.getExpiresAt() != null && !order.getExpiresAt().isAfter(Instant.now())) {
            expireOrderInternal(order);
            return false;
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order không ở trạng thái chờ thanh toán");
        }

        int orderTicketCount = order.getOrderDetails().size();
        long boughtTickets = ticketService.countCompletedTicketsByUserAndEvent(
                order.getUser().getId(),
                order.getEvent().getEventId()
        );
        if (boughtTickets + orderTicketCount > MAX_TICKETS_PER_EVENT) {
            throw new IllegalStateException("Bạn chỉ được mua tối đa 3 vé cho sự kiện này.");
        }

        Payment payment = paymentRepository.findByOrder_OrderId(order.getOrderId())
                .orElseThrow(() -> new IllegalStateException("Order chưa có Payment"));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        ticketService.generateTicketsForOrder(order);
        if (order.getVoucher() != null) {
            VoucherUsage voucherUsage = new VoucherUsage();
            voucherUsage.setVoucher(order.getVoucher());
            voucherUsage.setOrder(order);
            voucherUsage.setUserId(order.getUser().getId());
            voucherUsageRepository.save(voucherUsage);
        }
        // Update sold_quantity — group theo TicketType, soldQuantity là Integer
        Map<TicketType, Integer> countByType = new HashMap<>();
        for (OrderDetail detail : order.getOrderDetails()) {
            TicketType type = detail.getSeat().getTicketType();
            countByType.merge(type, 1, Integer::sum);
        }
        for (Map.Entry<TicketType, Integer> entry : countByType.entrySet()) {
            TicketType type = entry.getKey();
            type.setSoldQuantity(type.getSoldQuantity() + entry.getValue());
        }
        ticketTypeRepository.saveAll(countByType.keySet());

        List<Long> seatIds = order.getOrderDetails().stream()
                .map(detail -> detail.getSeat().getSeatId())
                .collect(Collectors.toList());
        seatLockRepository.deleteBySeatSeatIdIn(seatIds);
        return true;
    }
    private void expireOrderInternal(Order order) {
        paymentRepository.findByOrder_OrderId(order.getOrderId()).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
        order.setStatus(OrderStatus.EXPIRED);
        orderRepository.save(order);

        List<Long> seatIds = order.getOrderDetails().stream()
                .map(detail -> detail.getSeat().getSeatId())
                .collect(Collectors.toList());
        if (!seatIds.isEmpty()) {
            seatLockRepository.deleteBySeatSeatIdIn(seatIds);
        }
    }

    public Payment findById(Long orderId) {
        return this.paymentRepository.findById(orderId).orElse(null);
    }

    public Payment handleSavePayment(Payment payment) {
        return this.paymentRepository.save(payment);
    }

    public Payment findByOrderId(Long orderId) {
        return this.paymentRepository.findByOrder_OrderId(orderId).orElse(null);
    }

    public Optional<Payment> findByVnpTxnRef(String vnpTxnRef) {
        return this.paymentRepository.findByVnpTxnRef(vnpTxnRef);
    }

    public void save(Payment payment) {
        this.paymentRepository.save(payment);
    }

    @Transactional
    public Payment prepareVnpayPayment(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền thanh toán order này");
        }

        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order không ở trạng thái chờ thanh toán");
        }
        if (order.getExpiresAt() != null && !order.getExpiresAt().isAfter(Instant.now())) {
            throw new IllegalStateException("Đơn hàng đã hết thời gian thanh toán");
        }

        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Order chưa có Payment"));

        payment.setPaymentMethod(PaymentMethod.VNPAY);

        String txnRef = orderId + "_" + System.currentTimeMillis();
        payment.setVnpTxnRef(txnRef);

        paymentRepository.save(payment);

        return payment;
    }
}