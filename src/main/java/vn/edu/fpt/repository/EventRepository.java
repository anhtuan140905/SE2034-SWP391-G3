package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.constant.EventStatus;
import vn.edu.fpt.modelview.response.homepage.EventSummaryDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {

    @Query("SELECT COUNT(e) FROM Event e WHERE e.status IN :statuses")
    long countHostedEvents(@Param("statuses") List<EventStatus> statuses);

    @Query(value = "SELECT TOP 6 e.event_id AS id, e.title, e.thumbnail_url, e.start_time,\n" +
            "                        MIN(tt.price) as min_price,  \n" +
            "                        ec.category_name as category_name, \n" +
            "                        e.venue_name as venue,\n" +
            "                        ci.name as city_name, \n" +
            "                        COUNT(DISTINCT od.order_detail_id) as sold_count \n" +
            "                        FROM events e\n" +
            "                        JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "                        JOIN event_categories ec ON ec.category_id = e.category_id \n" +
            "                        JOIN addresses a ON a.id = e.address_id\n" +
            "                        JOIN wards w ON w.id = a.ward_id \n" +
            "                        JOIN city ci ON ci.id = w.city_id \n" +
            "                        LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "                        LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "                        WHERE e.status = 'ACTIVE' AND e.start_time > GETDATE()\n" +
            "                        GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, e.venue_name, ci.name\n" +
            "                        ORDER BY sold_count DESC",
            nativeQuery = true)
    List<EventSummaryProjection> findTopFeaturedEvents();
//
    @Query(value="SELECT TOP 1 e.event_id, e.title, e.thumbnail_url, e.start_time as startTime, e.venue_name as venueName, a.specific_address, c.name AS [cityName],\n" +
            "                             MIN(tt.price) as minPrice,\n" +
            "                             ec.category_name,\n" +
            "                             CONCAT(u.first_name, ' ', u.last_name) as organizer_name,\n" +
            "                             COUNT(DISTINCT od.order_detail_id) as soldCount\n" +
            "                        FROM events e\n" +
            "                        JOIN event_categories ec ON e.category_id = ec.category_id\n" +
            "                        JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "                        JOIN users u ON e.organizer_id = u.id\n" +
            "                       JOIN addresses a ON a.id = e.address_id\n" +
            "                       JOIN wards w ON a.ward_id = w.id\n" +
            "                       JOIN city c ON w.city_id = c.id\n" +
            "                        LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "                        LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "                        WHERE e.status = 'ACTIVE' AND e.start_time > GETDATE()\n" +
            "                        GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, u.first_name, u.last_name, e.venue_name,\n" +
            "                       a.specific_address, c.name, ec.category_name\n" +
            "                        ORDER BY soldCount DESC", nativeQuery = true)
    FeaturedEventDTO findFeaturedEvent();
//
//
//    //---------------------------------------------------------------------------------------
//    @Query("SELECT e FROM Event e WHERE " +
//            "(:status IS NULL OR e.status = :status) " +
//            "AND " +
//            "(:categoryId IS NULL OR e.category.categoryId = :categoryId) " +
//            "AND " +
//            "(:keyword IS NULL OR :keyword = '' OR e.title LIKE CONCAT('%', :keyword, '%'))")
//    Page<Event> searchAndFilterEvents(
//            @Param("keyword") String keyword,
//            @Param("status") EventStatus status,
//            @Param("categoryId") Long categoryId,
//            Pageable pageable);
//
//    // Đếm số lượng sự kiện theo trạng thái
//    long countByStatus(EventStatus status);
//
//    // Lấy 3 sự kiện Pending mới nhất
//    List<Event> findByStatusOrderByCreatedAtDesc(EventStatus status, Pageable pageable);
//
//    // Lấy sự kiện diễn ra trong ngày (status = APPROVED)
//    List<Event> findByStatusAndStartTimeBetween(EventStatus status, LocalDateTime start, LocalDateTime end);
//
//    long countByOrganizerId(Long organizerId);
//
////    long countByOrganizerIdAndStatus(Long organizerId, EventStatus status);
//
    @Query(value = "SELECT e.event_id AS id, e.title, e.thumbnail_url, e.start_time, op.company_name, e.description,\n" +
            "                       MIN(tt.price) as min_price,\n" +
            "                       ec.category_name as category_name,\n" +
            "                       e.venue_name as venue,\n" +
            "                       ci.name as city_name,\n" +
            "                       COUNT(DISTINCT od.order_detail_id) as sold_count\n" +
            "                       FROM events e \n" +
            "                       JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "                       JOIN event_categories ec ON ec.category_id = e.category_id\n" +
            "                       JOIN addresses a ON a.id = e.address_id\n" +
            "                       JOIN wards w ON w.id = a.ward_id\n" +
            "                       JOIN city ci ON ci.id = w.city_id\n" +
            "               JOIN users u  ON e.organizer_id = u.id\n" +
            "               JOIN organizer_profiles op ON u.id = op.user_id\n" +
            "                       LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "                       LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "                       WHERE e.status = 'ACTIVE' AND e.event_id = :id\n" +
            "                       GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, e.venue_name, ci.name, op.company_name, e.description",
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
//
//    @Query(value = """
//        SELECT e.title,
//               e.start_time,
//               e.end_time,
//               COUNT(od.order_detail_id)        AS participantCount,
//               COALESCE(SUM(o.total_amount), 0) AS revenue
//        FROM events e
//        LEFT JOIN orders o
//            ON e.event_id = o.event_id
//            AND o.status = 'PAID'
//        LEFT JOIN order_details od
//            ON o.order_id = od.order_id
//        WHERE e.venue_id = :venueId
//                AND e.end_time < GETDATE()
//        GROUP BY e.title, e.start_time, e.end_time, e.event_id
//        """, nativeQuery = true)
//    List<EventSummaryProjection> getEventStatisticsByVenue(@Param("venueId") Long id);
//
//    List<Event> findByVenue_VenueId(Long id);
//
//    @Query(value = """
//        SELECT
//            COUNT(DISTINCT e.event_id)                                              AS eventCount,
//            COUNT(od.order_detail_id)                                               AS participantCount,
//            COALESCE(SUM(o.total_amount), 0)                                        AS revenue,
//            CASE
//                WHEN MAX(v.capacity) = 0 OR MAX(v.capacity) IS NULL THEN 0
//                ELSE (COUNT(od.order_detail_id) * 100.0 / MAX(v.capacity))
//            END                                                                      AS usageRate
//        FROM events e
//        LEFT JOIN orders o
//            ON e.event_id = o.event_id
//            AND o.status = 'PAID'
//        LEFT JOIN order_details od
//            ON o.order_id = od.order_id
//        JOIN venues v
//            ON e.venue_id = v.venue_id
//        WHERE e.venue_id = :venueId
//        """, nativeQuery = true)
//
//    VenueSummaryProjection getVenueStatisticSummary(@Param("venueId") Long venueId);
//
//
//
//    @Query(value = """
//        SELECT
//            MONTH(o.created_at)          AS month,
//            COALESCE(SUM(o.total_amount), 0) AS revenue
//        FROM events e
//        LEFT JOIN orders o
//            ON e.event_id = o.event_id
//            AND o.status = 'PAID'
//        WHERE e.venue_id = :venueId
//        AND YEAR(o.created_at) = YEAR(GETDATE())
//        GROUP BY MONTH(o.created_at)
//        ORDER BY month
//        """, nativeQuery = true)
//    List<VenueSummaryProjection> getMonthlyRevenueByVenue(@Param("venueId") Long venueId);
//
@Query("""
    SELECT e FROM Event e
    WHERE e.organizer.id = :organizerId
      AND (
            :#{#statusList.size()} = 0
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
        e.event_id        AS id,
        e.title           AS title,
        e.thumbnail_url   AS thumbnailUrl,
        e.start_time      AS startTime,
        e.end_time        AS endTime,
        e.venue_name      AS venueName,
        NULL              AS cityName,
        NULL              AS categoryName,
        NULL              AS minPrice,
        COUNT(ord.order_detail_id) AS soldCount,
        COUNT(ord.order_detail_id) AS participantCount,
        NULL              AS revenue
    FROM events e
    LEFT JOIN orders o ON e.event_id = o.event_id
    LEFT JOIN order_details ord ON o.order_id = ord.order_id
    GROUP BY
        e.event_id, e.title, e.thumbnail_url,
        e.start_time, e.end_time, e.venue_name
    ORDER BY e.end_time DESC
    """, nativeQuery = true)
    List<EventSummaryProjection> findTop10Events();


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

}
