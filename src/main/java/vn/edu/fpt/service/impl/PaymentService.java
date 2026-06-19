package vn.edu.fpt.service.impl;

import vn.edu.fpt.model.*;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.model.constant.PaymentStatus;
import vn.edu.fpt.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.service.TicketService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final SeatLockRepository seatLockRepository;
    private final TicketTypeRepository ticketTypeRepository;
    private final TicketService ticketService;

    @Transactional
    public void confirmPayment(Long orderId, User currentUser) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order không tồn tại"));

        if (!order.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Bạn không có quyền xác nhận order này");
        }
        if (order.getStatus() == OrderStatus.PAID) {
            return; // chặn double-click
        }
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("Order không ở trạng thái chờ thanh toán");
        }

        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new IllegalStateException("Order chưa có Payment"));


        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setConfirmedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        order.setStatus(OrderStatus.PAID);
        orderRepository.save(order);

        ticketService.generateTicketsForOrder(order);

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
}