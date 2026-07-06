package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>, JpaSpecificationExecutor<Event> {

    boolean existsByEvent_EventId(Long eventId);

    @Query("""
select count(settlementId)
from Settlement 
""")
    long countAllSettlement();

    @Query("""
select count(settlementId)
from Settlement
where status = 'PENDING'
""")
    long countPendingSettlement();

    @Query("""
select count(settlementId)
from Settlement
where status = 'COMPLETED'
""")
    long countCompletedSettlement();

//    @Query("""
//select sum(payoutAmount)
//from Settlement
//where status = 'COMPLETED'
//""")

}
