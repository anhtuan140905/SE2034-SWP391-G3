package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.OrderDetail;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.repository.OrderDetailRepository;
import vn.edu.fpt.repository.OrderRepository;
import vn.edu.fpt.repository.SeatLockRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpiredOrderCleanupJob {
    private final OrderRepository orderRepository;
    private final SeatLockRepository seatLockRepository;
    private final OrderDetailRepository orderDetailRepository;

    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void deleteExpiredOrders() {
        List<Order> orders = this.orderRepository.findByStatusAndExpiresAtBefore(OrderStatus.PENDING_PAYMENT, Instant.now());
        if(orders.isEmpty()) {
            return;
        }
        List<Long> seatIds = new ArrayList<>();
        for(Order order : orders) {
            order.setStatus(OrderStatus.EXPIRED);
            for(OrderDetail orderDetail : order.getOrderDetails()) {
                Long seatId = orderDetail.getSeat().getSeatId();
                seatIds.add(seatId);
            }
        }
        this.orderRepository.saveAll(orders);
        this.seatLockRepository.deleteAllBySeatIdIn(seatIds);
        this.seatLockRepository.deleteAll(this.seatLockRepository.findByExpiresAtBefore(Instant.now()));
     }
}
