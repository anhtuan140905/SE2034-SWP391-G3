package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;

import java.util.List;

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

   @Query("""
select sum(payoutAmount)
from Settlement
where status = 'COMPLETED'
""")
Long sumPayoutAmount();

   @Query(value = """
select
se.settlement_id as settlementId,
e.title as eventName,
u.last_name as lastNameOrganizer,
u.middle_name as middleNameOrganizer,
u.first_name as firstNameOrganizer,
se.payout_amount as payoutAmount,
CAST(se.created_at AS datetime2) as createAt,
se.status as status
from settlements se\s
left join events e on se.event_id = e.event_id
left join organizer_profiles o on e.organizer_id = o.user_id
left join users u on o.user_id = u.id
""", nativeQuery = true)
   List<SettlementSummaryProjection> listSettlement();


    @Query(value = """
select
se.settlement_id as settlementId,
e.title as eventName,
u.last_name as lastNameOrganizer,
u.middle_name as middleNameOrganizer,
u.first_name as firstNameOrganizer,
se.payout_amount as payoutAmount,
CAST(se.created_at AS datetime2) as createAt,
se.status as status
from settlements se\s
left join events e on se.event_id = e.event_id
left join organizer_profiles o on e.organizer_id = o.user_id
left join users u on o.user_id = u.id
where 
(lower(e.title) like lower(concat('%', :keyword, '%')) 
or lower(u.last_name) like lower(concat('%', :keyword, '%'))
or lower(u.middle_name) like lower(concat('%', :keyword, '%'))
or lower (u.first_name) like lower(concat('%', :keyword, '%')))
""", nativeQuery = true)
    List<SettlementSummaryProjection> searchSettlement(@Param("keyword") String keyword);


@Query(value = """
select
se.settlement_id as settlementId,
CAST(se.created_at AS datetime2) as createAt,
u.last_name as lastNameFinance,
u.middle_name as middleNameFinance,
u.first_name as firstNameFinance,
se.status as status,
se.gross_revenue as revenue,
se.platform_fee as platformFee,
se.payout_amount as payoutAmount,
CAST(se.updated_at AS datetime2) as updateAt,
CAST(se.paid_at AS datetime2) as paidAt
from settlements se
left join users u on u.email = se.created_by
where se.settlement_id = :settlementId
""", nativeQuery = true)
    SettlementSummaryProjection getSettlementDetail(@Param("settlementId") Long settlementId);
}
