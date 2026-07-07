package vn.edu.fpt.service;

import vn.edu.fpt.model.User;

import java.util.List;

public interface CheckoutService {
    public Long proceedToPayment(List<Long> seatIds, Long voucherId, User currentUser);
    public void lockSeatTemporarily(Long seatId, User User);
    public void unlockSeatTemporarily(Long seatId, User User);
    Long resolveEventIdFromSeats(List<Long> seatIds);
}
