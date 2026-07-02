package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Voucher;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {
    boolean existsByCode(String code);
}
