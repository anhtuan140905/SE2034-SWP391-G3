package vn.edu.fpt.service;

public interface VoucherUsageService {
    long countVoucherUsageByVoucherId(Long voucherId);

    boolean existsByVoucher_VoucherIdAndUserId(Long voucherId, Long userId);
}
