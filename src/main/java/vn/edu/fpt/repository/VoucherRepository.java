package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Voucher;

import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByCodeIgnoreCase(String code);

    List<Voucher> findByEvent_EventId(Long eventId);

    Optional<Voucher> findByEvent_EventIdAndVoucherId(Long eventId, Long voucherId);

}
