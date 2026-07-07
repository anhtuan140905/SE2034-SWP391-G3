package vn.edu.fpt.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.repository.VoucherUsageRepository;
import vn.edu.fpt.service.VoucherUsageService;

@Service
@RequiredArgsConstructor
public class VoucherUsageServiceImpl implements VoucherUsageService {
    private final VoucherUsageRepository voucherUsageRepository;
    @Override
    public long countVoucherUsageByVoucherId(Long voucherId) {
        return voucherUsageRepository.countByVoucher_VoucherId(voucherId);
    }

    @Override
    public boolean existsByVoucher_VoucherIdAndUserId(Long voucherId, Long userId) {
        return this.voucherUsageRepository.existsByVoucher_VoucherIdAndUserId(voucherId, userId);
    }
}
