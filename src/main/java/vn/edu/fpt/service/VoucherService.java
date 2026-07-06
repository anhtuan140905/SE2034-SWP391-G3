package vn.edu.fpt.service;

import vn.edu.fpt.model.Voucher;
import vn.edu.fpt.modelview.request.organizer.CreateVoucherRequest;

import java.util.List;

public interface VoucherService {
    void createVoucher(Long evenId, Long userId, CreateVoucherRequest request);

    List<Voucher> getVoucherByEventId(Long evenId);

    Voucher getVoucherDetail(Long eventId, Long voucherId);
}
