package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.SettlementStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {

    Long countByStatus(SettlementStatus status);




    @Query("""
        SELECT COALESCE(SUM(s.grossRevenue), 0)
        FROM Settlement s
    """)
    BigDecimal getTotalRevenue();

    @Query("""
        SELECT COALESCE(SUM(s.payoutAmount), 0)
        FROM Settlement s
        WHERE s.status = vn.edu.fpt.model.constant.SettlementStatus.COMPLETED
    """)
    BigDecimal getPaidAmount();



    List<Settlement> findTop5ByOrderByCreatedAtDesc();
    @Query("""
        SELECT s FROM Settlement s
        JOIN FETCH s.event e
        JOIN FETCH e.organizer
        WHERE s.settlementId = :id
    """)
    Optional<Settlement> findByIdWithEventAndOrganizer(@Param("id") Long id);
    boolean existsByEvent(Event event);
}
