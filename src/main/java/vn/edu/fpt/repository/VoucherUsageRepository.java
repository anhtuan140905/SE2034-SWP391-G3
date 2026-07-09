package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.VoucherUsage;

@Repository
public interface VoucherUsageRepository extends JpaRepository<VoucherUsage, Long> {
    boolean existsByVoucher_VoucherIdAndUserId(Long voucherId, Long userId);

     long countByVoucher_VoucherId(Long voucherId);


}