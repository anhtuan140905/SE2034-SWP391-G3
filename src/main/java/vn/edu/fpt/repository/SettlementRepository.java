package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Settlement;

import java.util.List;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long>, JpaSpecificationExecutor<Event> {
    @Query("select s from Settlement s where s.event.eventId = :eventId")
    Settlement getPayoutAmountByEventId(@Param("eventId") Long eventId);

    boolean existsByEvent_EventId(Long eventId);

    @Query(value = """
select count(se.settlement_id)
from settlements se
""",nativeQuery = true)
    long countAllSettlement();

    @Query(value = """
select count(se.settlement_id)
from settlements se
where se.status = 'PENDING'
""",nativeQuery = true)
    long countPendingSettlement();

    @Query(value = """
select count(se.settlement_id)
from settlements se
where se.status = 'COMPLETED'
""",nativeQuery = true)
    long countCompletedSettlement();

    @Query(value = """
select sum(se.payout_amount)
from settlements se
where se.status = 'COMPLETED'
""",nativeQuery = true)
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
CAST(se.paid_at AS datetime2) as paidAt,
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
CAST(se.paid_at AS datetime2) as paidAt,
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
se.created_by as createdBy,
u.last_name as lastNameFinance,
u.middle_name as middleNameFinance,
u.first_name as firstNameFinance,
se.status as status,
se.gross_revenue as revenue,
se.platform_fee as platformFee,
se.payout_amount as payoutAmount,
CAST(se.updated_at as datetime2) as updateAt,
CAST(se.paid_at as datetime2) as paidAt,
o.bank_account_name as bankAccountName,
o.bank_account_number as bankAccountNumber,
o.bank_branch as bankBranch,
b.name as bankName
from settlements se
left join users u on u.email = se.updated_by
left join organizer_profiles o on u.id = o.user_id
left join banks b on o.bank_id = b.id
where se.settlement_id = :settlementId
""", nativeQuery = true)
    SettlementSummaryProjection getSettlementDetail(@Param("settlementId") Long settlementId);

    @Query("""
        select
            e.eventId as eventId,
            e.title as eventName,
            e.organizer.lastName as lastNameOrganizer,
            e.organizer.middleName as middleNameOrganizer,
            e.organizer.firstName as firstNameOrganizer,
            e.endTime as endTime,
            se.settlementId as settlementId,

            (select SUM(tt.soldQuantity)
             from TicketType tt
             where tt.event.eventId = e.eventId
            ) as soldTicket,

            (select SUM(o.totalAmount)
             from Order o
             where o.event.eventId = e.eventId and o.status = 'PAID'
            ) as revenue,

            se.status as status

        from Event e
        left join Settlement se on e.eventId = se.event.eventId
        where e.eventId = :eventId
        """)
    SettlementSummaryProjection findEventDetailById(@Param("eventId") Long eventId);

    @Query(value = """
select sum(se.payout_amount)
from settlements se
where se.status = 'PENDING'
""", nativeQuery = true)
    Long sumPendingPayoutAmount();

    @Query(value = """
select count(se.settlement_id)
from settlements se
where se.status = 'PENDING'\s
and DATEDIFF(hour,se.created_at,SYSDATETIME()) between 48 and 71
""",nativeQuery = true)
    long countNearDuePendingSettlements();

    @Query(value = """
        select count(e.event_id)
        from orders o
        left join events e on o.event_id = e.event_id
        left join settlements se on e.event_id = se.event_id
        where e.end_time <= CURRENT_TIMESTAMP\s
          and se.settlement_id IS NULL
		  and o.status = 'PAID'
        """, nativeQuery = true)
    long countUnsettledEvents();

@Query(value = """
select
MONTH(e.start_time) as month,
sum(se.platform_fee) as platformFee
from settlements se left join events e on se.event_id = e.event_id
where se.status = 'COMPLETED' and YEAR(e.start_time) = YEAR(SYSDATETIME())
group by MONTH(e.start_time)
order by MONTH(e.start_time)
""",nativeQuery = true)
    List<SettlementSummaryProjection> platformFeeByMonth();

@Query(value = """

        select
sum(case
       when DATEDIFF(HOUR, se.created_at,SYSDATETIME()) < 24
	   then 1
	   else 0
	end
) as bucket0,

sum(case
       when DATEDIFF(HOUR, se.created_at, SYSDATETIME()) >= 24 and DATEDIFF(HOUR, se.created_at, SYSDATETIME()) < 48
	   then 1
	   else 0
	end
) as bucket1,

sum(case\s
       when DATEDIFF(HOUR, se.created_at, SYSDATETIME()) >= 48 and DATEDIFF(HOUR, se.created_at, SYSDATETIME()) < 72
	   then 1
	   else 0
	end
) as bucket2,

sum(case
       when DATEDIFF(HOUR, se.created_at, SYSDATETIME()) >= 72
	   then 1
	   else 0
	end
) as bucket3
from settlements se left join events e on se.event_id = e.event_id
where se.status = 'PENDING'\s
""", nativeQuery = true)
SettlementAgingProjection getSettlementAging();
}
