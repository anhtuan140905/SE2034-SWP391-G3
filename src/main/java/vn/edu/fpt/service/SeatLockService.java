package vn.edu.fpt.service;

import java.util.List;

public interface SeatLockService {
    void deleteAllBySeatIdIn(List<Integer> seatIds);
    void handleDeleteSeatLockByOrder(Long orderId);
}
