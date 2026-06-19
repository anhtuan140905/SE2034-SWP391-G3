package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.OrderDetail;
import vn.edu.fpt.repository.SeatLockRepository;
import vn.edu.fpt.service.OrderService;
import vn.edu.fpt.service.SeatLockService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatLockServiceImpl implements SeatLockService {
    private final OrderService orderService;
    private final SeatLockRepository seatLockRepository;
    @Override
    public void deleteAllBySeatIdIn(List<Integer> seatIds) {

    }

    @Override
    @Transactional
    public void handleDeleteSeatLockByOrder(Long orderId) {
        Order order = this.orderService.findById(orderId);
        List<Long> seatIds = new ArrayList<>();
        for (OrderDetail orderDetail : order.getOrderDetails()) {
            seatIds.add(orderDetail.getSeat().getSeatId());
        }
        this.seatLockRepository.deleteAllBySeatIdIn(seatIds);
    }
}
