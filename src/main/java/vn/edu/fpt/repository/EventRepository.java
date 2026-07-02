package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.EventCategory;
import vn.edu.fpt.model.Settlement;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.request.admin.CountEventByMonthDTO;
import vn.edu.fpt.modelview.response.homepage.EventHomeDTO;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query("SELECT e FROM Event e WHERE (e.status = :activeStatus AND e.endTime < :now) Or (e.startTime <= :now AND e.endTime >= :now)")
    List<Event> findEndedEvents(@Param("activeStatus") EventStatus activeStatus, @Param("now") LocalDateTime now);
    @Query("SELECT COUNT(e) FROM Event e WHERE e.status IN :statuses")
    long countHostedEvents(@Param("statuses") List<EventStatus> statuses);

    @Query(value = "SELECT TOP 6 e.event_id AS id, e.title, e.thumbnail_url, e.start_time,\n" +
            "MIN(tt.price) as min_price,  \n" +
            "ec.category_name as category_name, \n" +
            "e.venue_name as venueName,\n" +
            "ci.name as city_name, \n" +
            "COUNT(DISTINCT od.order_detail_id) as sold_count \n" +
            "FROM events e\n" +
            "JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "JOIN event_categories ec ON ec.category_id = e.category_id \n" +
            "JOIN addresses a ON a.id = e.address_id\n" +
            "JOIN wards w ON w.id = a.ward_id \n" +
            "JOIN city ci ON ci.id = w.city_id \n" +
            "LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "WHERE e.status = 'ACTIVE' AND e.start_time > GETDATE()\n" +
            "GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, e.venue_name, ci.name\n" +
            "ORDER BY sold_count DESC",
            nativeQuery = true)
    List<EventSummaryProjection> findTopFeaturedEvents();

    //
    @Query(value = "SELECT TOP 1 e.event_id, e.title, e.thumbnail_url, e.start_time as startTime, e.venue_name as venueName, a.specific_address, c.name AS [name],\n" +
            "MIN(tt.price) as minPrice,\n" +
            "ec.category_name,\n" +
            "CONCAT(u.first_name, ' ', u.last_name) as organizer_name,\n" +
            "COUNT(DISTINCT od.order_detail_id) as soldCount\n" +
            "FROM events e\n" +
            "JOIN event_categories ec ON e.category_id = ec.category_id\n" +
            "JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "JOIN users u ON e.organizer_id = u.id\n" +
            "JOIN addresses a ON a.id = e.address_id\n" +
            "JOIN wards w ON a.ward_id = w.id\n" +
            "JOIN city c ON w.city_id = c.id\n" +
            "LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "WHERE e.status = 'ACTIVE' AND e.start_time > GETDATE()\n" +
            "GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, u.first_name, u.last_name, e.venue_name,\n" +
            "a.specific_address, c.name, ec.category_name\n" +
            "ORDER BY soldCount DESC", nativeQuery = true)
    FeaturedEventDTO findFeaturedEvent();

    @Query(value = "SELECT e.event_id AS id, e.title, e.thumbnail_url, e.start_time, op.company_name, e.description,\n" +
            "MIN(tt.price) as min_price,\n" +
            "ec.category_name as category_name,\n" +
            "e.venue_name as venueName,\n" +
            "ci.name as city_name,\n" +
            "COUNT(DISTINCT od.order_detail_id) as sold_count,\n" +
            "SUM(tt.total_quantity) as total_tickets\n" +
            "FROM events e \n" +
            "JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "JOIN event_categories ec ON ec.category_id = e.category_id\n" +
            "JOIN addresses a ON a.id = e.address_id\n" +
            "JOIN wards w ON w.id = a.ward_id\n" +
            "JOIN city ci ON ci.id = w.city_id\n" +
            "JOIN users u  ON e.organizer_id = u.id\n" +
            "JOIN organizer_profiles op ON u.id = op.user_id\n" +
            "LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "WHERE e.status = 'ACTIVE' AND e.event_id = :id\n" +
            "GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, e.venue_name, ci.name, op.company_name, e.description",
            nativeQuery = true)
    EventSummaryProjection findEventDetailById(Long id);


    //    @Query(value = "SELECT e.event_id, e.title, e.thumbnail_url, e.start_time, op.company_name, e.description,\n" +
//            "           MIN(tt.price) as min_price,\n" +
//            "           ec.category_name as category_name,\n" +
//            "           v.venue_name as venue_name,\n" +
//            "           ci.name as city_name,\n" +
//            "           COUNT(DISTINCT od.order_detail_id) as sold_count\n" +
//            "           FROM events e \n" +
//            "           JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
//            "           JOIN event_categories ec ON ec.category_id = e.category_id\n" +
//            "           JOIN venues v ON v.venue_id = e.venue_id\n" +
//            "           JOIN addresses a ON a.id = v.address_id\n" +
//            "           JOIN wards w ON w.id = a.ward_id\n" +
//            "           JOIN city ci ON ci.id = w.city_id\n" +
//            "\t\t   JOIN users u  ON e.organizer_id = u.id\n" +
//            "\t\t   JOIN organizer_profiles op ON u.id = op.user_id\n" +
//            "           LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
//            "           LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
//            "           WHERE e.status = 'APPROVED' AND e.event_id = :id\n" +
//            "           GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, v.venue_name, ci.name, op.company_name, e.description",
//            nativeQuery = true)
//    EventSummaryProjection findEventDetailById(Long id);

    //
    @Query("""
            SELECT e FROM Event e
            JOIN OrganizerMember o ON o.event = e
            JOIN o.userRole ur
            WHERE ur.user.id = :organizerId
              AND (
                    :#{#statusList == null} = true
                    OR :#{#statusList.size()} = 0
                    OR e.status IN :statusList
                  )
              AND (
                    :keyword IS NULL
                    OR :keyword = ''
                    OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            """)
    Page<Event> findByMultiStatusAndKeyword(
            @Param("organizerId") Long organizerId,
            @Param("statusList") List<String> statusList,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    List<Event> findTop10ByOrganizerIdOrderByCreatedAtDesc(Long userId);

    @Query(value = """
            SELECT TOP 10
            e.event_id                      AS id,
            e.title                         AS title,
            e.thumbnail_url                 AS thumbnailUrl,
            e.start_time                    AS startTime,
            e.end_time                      AS endTime,
            e.venue_name                    AS venueName,
            e.status                        AS status,
            COALESCE(tt.totalSold, 0)       AS soldCount,
            COALESCE(tt.totalSold, 0)       AS participantCount
            
            FROM events e
            
            LEFT JOIN (
            SELECT event_id, SUM(sold_quantity) AS totalSold
            FROM ticket_types
            GROUP BY event_id
            ) tt ON e.event_id = tt.event_id
            
            WHERE e.status IN ('ACTIVE', 'INACTIVE', 'ENDED')
            ORDER BY e.end_time ASC 
            """, nativeQuery = true)
    List<EventSummaryProjection> findTop10Events();

    @Query(value = """
            SELECT TOP 5
            e.event_id              AS id,
            e.title                 AS title,
            e.start_time            AS startTime,
            e.end_time              AS endTime,
            e.venue_name            AS venueName,
            e.status                AS status,
            COALESCE(tt.totalSold, 0)           AS soldCount,
            COALESCE(tt.totalTickets, 0)        AS totalTickets,
            COALESCE(ord.participantCount, 0)   AS participantCount,
            COALESCE(ord.revenue, 0)            AS revenue,
            CAST(COALESCE(tt.totalSold, 0) AS FLOAT) / NULLIF(COALESCE(tt.totalTickets, 0), 0) * 100 AS salesRate
            FROM events e
            
            LEFT JOIN (
            SELECT event_id,
            SUM(sold_quantity)  AS totalSold,
            SUM(total_quantity) AS totalTickets
            FROM ticket_types
            GROUP BY event_id
            ) tt ON e.event_id = tt.event_id
            
            LEFT JOIN (
            SELECT o.event_id,
            COUNT(od.order_detail_id) AS participantCount,
            SUM(od.unit_price)        AS revenue
            FROM orders o
            JOIN order_details od ON o.order_id = od.order_id
            WHERE o.status = 'PAID'
            GROUP BY o.event_id
            ) ord ON e.event_id = ord.event_id
            
            WHERE e.status IN ('ACTIVE', 'ENDED')
            ORDER BY soldCount DESC
            """, nativeQuery = true)
    List<EventSummaryProjection> findTop5EventsBySoldCount();

    @Query("""
            select count(e.eventId)
                from Event e
            """)
    long countAllEvent();

    @Query("""
            SELECT COUNT(u.id)
            FROM User u
            WHERE u.isActive = true     
            """)
    long countAllUseActive();


    @Query("""
            SELECT MONTH(e.startTime) as month, 
                COUNT(e.eventId) as total
            FROM Event e
            GROUP BY MONTH(e.startTime)
            ORDER BY MONTH(e.startTime)
            """)
    List<CountEventByMonthDTO> countEventByMonth();

    @Query(value = """
            SELECT
            MONTH(e.start_time) AS month,
            SUM(ord.unit_price) AS total
            FROM orders o
            JOIN order_details ord ON o.order_id = ord.order_id
            JOIN events e ON o.event_id = e.event_id
            WHERE o.status = 'PAID' AND YEAR(e.start_time) = YEAR(GETDATE())
            GROUP BY MONTH(e.start_time)
            ORDER BY MONTH(e.start_time)
            """, nativeQuery = true)
    List<SumRevenueByMonthProjection> sumRevenueByMonth();

    //----------------------------------------------------------------------------------------------------
    // MODERATOR
    //----------------------------------------------------------------------------------------------------

    // 1. Dem so su kien theo trang thai
    @Query("SELECT COUNT(e) FROM Event e WHERE e.status = :status")
    long countEventsByStatus(@Param("status") EventStatus status);

    // 2. Dem so su kien moi dang trong ngay
    @Query(value = "SELECT COUNT(*) FROM events WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE)", nativeQuery = true)
    long countNewEventsToday();

    // 3. Lay ra 5 su kien moi dang tai len nen tang
    @Query(value = "SELECT * FROM events WHERE CAST(created_at AS DATE) = CAST(GETDATE() AS DATE) ORDER BY created_at DESC", nativeQuery = true)
    List<Event> findTopFiveNewEventsToday(Pageable pageable);

    // 4. Lay ra 5 su kien dien ra hom nay
    @Query("SELECT e FROM Event e WHERE e.status = :status AND CAST(e.date AS date) = CURRENT_DATE ORDER BY e.startTime ASC")
    List<Event> findTopFiveEventsToday(@Param("status") EventStatus status, Pageable pageable);

    // ---------------------------------------- EVENT MANAGEMENT --------
    // 1. Dem tong tat ca su kien
    @Query("SELECT COUNT(e) FROM Event e")
    long countAllEvents();

    // 2. Dem su kien dien ra hom nay
    @Query("SELECT COUNT(e) FROM Event e WHERE CAST(e.date AS date) = CURRENT_DATE")
    long countEventsToday();

    // 3. Dem su kien Ket Thuc trong thang nay
    @Query("SELECT COUNT(e) FROM Event e WHERE e.status = :status " +
            "AND MONTH(e.date) = MONTH(CURRENT_DATE) " +
            "AND YEAR(e.date) = YEAR(CURRENT_DATE)")
    long countEndedThisMonth(@Param("status") EventStatus status);

    // 4. Lay danh sach su kien: Filter + Search + Pagination
    @Query("SELECT e FROM Event e " +
            "WHERE (:status IS NULL OR e.status = :status) " +
            "AND (:keyword IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:categoryId IS NULL OR e.category.categoryId = :categoryId)")
    Page<Event> findEventsWithFilterAndSearch(
            @Param("status") EventStatus status,
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );

    // 5. Lay thong tin chi tiet của event theo Id
    // EVENT DETAIL
    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.organizer o " +
            "JOIN FETCH e.category c " +
            "JOIN FETCH e.address a " +
            "JOIN FETCH a.ward w " +
            "JOIN FETCH w.city " +
            "WHERE e.eventId = :id")
    Optional<Event> moderatorFindEventDetailById(@Param("id") Long id);

    // ---------------------------------------- ORGANIZER INFORMATION --------
    // 1, Dem so luong su kien duoc to chuc boi organizer
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId")
    long countEventOrganized(@Param("organizerId") Long organizerId);

    // 2, Dem so luong su kien cua organizer bi tat
    @Query("SELECT COUNT(e) FROM Event e WHERE e.organizer.id = :organizerId AND e.status = :status")
    long countEventInactivated(@Param("organizerId") Long organizerId,
                               @Param("status") EventStatus status);

    @Query("""
            SELECT COUNT(DISTINCT o.event.eventId)
            FROM Order o
            WHERE o.user.id = :userId AND o.status = 'PAID'
            AND o.event.startTime > CURRENT_TIMESTAMP
            """)
    long countUpcomingEvent(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(DISTINCT o.event.eventId)
            FROM Order o
            JOIN o.orderDetails od
            JOIN od.ticket t
            WHERE o.user.id = :userId
            AND t.isCheckedIn = true
            AND o.status = 'PAID'
            """)
    long countAttendedEvent(@Param("userId") Long userId);


    @Query("SELECT new vn.edu.fpt.modelview.response.homepage.EventHomeDTO (" +
            "e.eventId, e.title, e.thumbnailUrl, e.description, e.venueName, e.startTime, MIN(t.price) AS minPrice, c.categoryName, op.companyName) " +
            "FROM Event e " +
            "LEFT JOIN e.ticketTypes t " +
            "LEFT JOIN e.category c " +
            "LEFT JOIN e.organizer o " +
            "LEFT JOIN o.organizerProfile op " +
            "WHERE e.eventId = :eventId " +
            "GROUP BY e.eventId, e.title, e.thumbnailUrl, e.description, e.venueName, e.startTime, c.categoryName, op.companyName")
    EventHomeDTO findEventsWithMinPrice(Long eventId);

    @Query("SELECT DISTINCT e FROM Event e " +
            "JOIN FETCH e.category c " +
            "JOIN FETCH e.address a " +
            "JOIN FETCH a.ward w " +
            "JOIN FETCH w.city city " +
            "JOIN FETCH e.ticketTypes tt " +
            "WHERE c.categoryId IN :categoryIds " +
            "AND e.status = :status " +
            "AND e.date >= :today " +
            "AND e.eventId NOT IN (" +
            "   SELECT o.event.eventId FROM Order o " +
            "   WHERE o.user.id = :userId " +
            "   AND o.status = :paidStatus) " +
            "ORDER BY e.date ASC")
    List<Event> findCandidatesEventByCategories(
            @Param("categoryIds") List<Long> categoryIds,
            @Param("status") EventStatus status,
            @Param("paidStatus") OrderStatus paidStatus,
            @Param("today") LocalDate today,
            @Param("userId") Long userId,
            Pageable pageable
            );

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
)as soldTicket,

(select SUM(o.totalAmount) 
from Order o
where o.event.eventId = e.eventId
)as revenue,

se.status as status

from Event e
left join Settlement se on e.eventId = se.event.eventId


where e.endTime <= CURRENT_TIMESTAMP
group by 
e.eventId,
e.title,
e.organizer.lastName,
e.organizer.middleName,
e.organizer.firstName,
e.endTime,
se.status,
se.settlementId 

order by e.endTime ASC
""")

List<SettlementSummaryProjection> findEndedEventsWithSettlementStatus();

@Query("""
select count(e.eventId)
from Event e
where e.endTime <= CURRENT_TIMESTAMP
""")
    long countEndedEvent();

    @Query("""
            select count(e.eventId)
            from Event e
            left join Settlement se on e.eventId = se.event.eventId
            where e.endTime <= CURRENT_TIMESTAMP and se.settlementId is null
            """)
    long countUnsettledEvents();

@Query("""
select sum(o.totalAmount)
from Event e
left join Order o on e.eventId = o.event.eventId
where e.endTime <= CURRENT_TIMESTAMP and o.status = 'PAID'
""")
    Long sumTotalRevenue();


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
)as soldTicket,

(select SUM(o.totalAmount) 
from Order o
where o.event.eventId = e.eventId
)as revenue,

se.status as status

from Event e
left join Settlement se on e.eventId = se.event.eventId

where (e.endTime <= CURRENT_TIMESTAMP) and
(lower(e.title) like lower(concat('%', :keyword, '%')) 
or lower(e.organizer.lastName) like lower(concat('%', :keyword, '%'))
or lower(e.organizer.middleName) like lower(concat('%', :keyword, '%'))
or lower (e.organizer.firstName) like lower(concat('%', :keyword, '%')))
group by 
e.eventId,
e.title,
e.organizer.lastName,
e.organizer.middleName,
e.organizer.firstName,
e.endTime,
se.status,
se.settlementId

order by e.endTime DESC
""")

    List<SettlementSummaryProjection> searchEndedEvents(@Param("keyword") String keyword);


    @Query("SELECT e FROM Event e " +
            "JOIN FETCH e.category " +
            "JOIN FETCH e.address a " +
            "JOIN FETCH a.ward w " +
            "JOIN FETCH w.city " +
            "WHERE e.status = :status " +
            "AND e.date >= :today " +
            "ORDER BY e.date ASC")
    List<Event> findUpcomingEvents(
            @Param("status") EventStatus status,
            @Param("today") LocalDate today,
            Pageable pageable
    );
