package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Voucher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VoucherRepository extends JpaRepository<Voucher, Long> {

    boolean existsByCodeIgnoreCase(String code);

    List<Voucher> findByEvent_EventId(Long eventId);

    Optional<Voucher> findByEvent_EventIdAndVoucherId(Long eventId, Long voucherId);

    Optional<Voucher> findByVoucherIdAndEvent_EventId(Long voucherId, Long eventId);

    @Query("SELECT v FROM Voucher v WHERE v.event.eventId = :eventId " +
            "AND v.isActive = true " +
            "AND v.validFrom <= :now AND v.validTo >= :now")
    List<Voucher> findAvailableVouchersByEvent(@Param("eventId") Long eventId, @Param("now") LocalDateTime now);
}
