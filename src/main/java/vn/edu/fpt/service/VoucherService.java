package vn.edu.fpt.service;

import vn.edu.fpt.model.Voucher;
import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;
import vn.edu.fpt.modelview.response.homepage.VoucherValidationResult;

import java.math.BigDecimal;
import java.util.List;

public interface VoucherService {
    void createVoucher(Long evenId, Long userId, CreateVoucherRequest request);

    List<Voucher> getVoucherByEventId(Long evenId);

    Voucher getVoucherDetail(Long eventId, Long voucherId);

    VoucherValidationResult validate(Long voucherId, Long eventId, Long userId, BigDecimal subtotal);

    List<Voucher> findAvailableVouchersByEvent(Long eventId);
}
