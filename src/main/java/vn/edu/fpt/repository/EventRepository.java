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

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event>{

    @Query("SELECT COUNT(e) FROM Event e WHERE e.status IN :statuses")
    long countHostedEvents(@Param("statuses") List<EventStatus> statuses);

    @Query(value = "SELECT TOP 6 e.event_id, e.title, e.thumbnail_url, e.start_time, \n" +
            "            MIN(tt.price) as min_price,  \n" +
            "            ec.category_name as category_name, \n" +
            "            v.venue_name as venue_name,  \n" +
            "            ci.name as city_name,  \n" +
            "            COUNT(DISTINCT od.order_detail_id) as sold_count \n" +
            "            FROM events e \n" +
            "            JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "            JOIN event_categories ec ON ec.category_id = e.category_id \n" +
            "            JOIN venues v ON v.venue_id = e.venue_id \n" +
            "            JOIN addresses a ON a.id = v.address_id  \n" +
            "            JOIN wards w ON w.id = a.ward_id \n" +
            "            JOIN city ci ON ci.id = w.city_id \n" +
            "            LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "            LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "            WHERE e.status = 'APPROVED' AND e.start_time > GETDATE()\n" +
            "            GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, ec.category_name, v.venue_name, ci.name\n" +
            "            ORDER BY sold_count DESC",
            nativeQuery = true)
    List<EventSummaryProjection> findTopFeaturedEvents();



    @Query(value="SELECT TOP 1 e.event_id, e.title, e.thumbnail_url, e.start_time,\n" +
            "       MIN(tt.price) as min_price,\n" +
            "       CONCAT(u.first_name, ' ', u.last_name) as organizer_name,\n" +
            "       COUNT(DISTINCT od.order_detail_id) as sold_count\n" +
            "FROM events e\n" +
            "JOIN ticket_types tt ON tt.event_id = e.event_id\n" +
            "JOIN users u ON u.id = e.organizer_id\n" +
            "LEFT JOIN orders o ON o.event_id = e.event_id AND o.status = 'PAID'\n" +
            "LEFT JOIN order_details od ON od.order_id = o.order_id\n" +
            "WHERE e.status = 'APPROVED' AND e.start_time > GETDATE()\n" +
            "GROUP BY e.event_id, e.title, e.thumbnail_url, e.start_time, u.first_name, u.last_name\n" +
            "ORDER BY sold_count DESC\n", nativeQuery = true)
    FeaturedEventDTO findFeaturedEvent();



    @Query("SELECT e FROM Event e WHERE " +
            "(:status IS NULL OR e.status = :status) " +
            "AND " +
            "(:categoryId IS NULL OR e.category.categoryId = :categoryId) " +
            "AND " +
            "(:keyword IS NULL OR :keyword = '' OR e.title LIKE CONCAT('%', :keyword, '%'))")
    Page<Event> searchAndFilterEvents(
            @Param("keyword") String keyword,
            @Param("status") EventStatus status,
            @Param("categoryId") Long categoryId,
            Pageable pageable);



    long countByStatus(EventStatus status);



    List<Event> findByVenue_VenueId(Long id);



    @Query(value = """
        SELECT e.title,
               e.start_time,
               e.end_time,
               COUNT(od.order_detail_id)        AS participantCount,
               COALESCE(SUM(o.total_amount), 0) AS revenue
        FROM events e
        LEFT JOIN orders o
            ON e.event_id = o.event_id
            AND o.status = 'PAID'
        LEFT JOIN order_details od
            ON o.order_id = od.order_id
        WHERE e.venue_id = :venueId
                AND e.end_time < GETDATE() 
        GROUP BY e.title, e.start_time, e.end_time, e.event_id
        """, nativeQuery = true)
    List<EventSummaryProjection> getEventStatisticsByVenue(@Param("venueId") Long id);



    @Query(value = """
        SELECT
            COUNT(DISTINCT e.event_id)                                              AS eventCount,
            COUNT(od.order_detail_id)                                               AS participantCount,
            COALESCE(SUM(o.total_amount), 0)                                        AS revenue,
            CASE
                WHEN MAX(v.capacity) = 0 OR MAX(v.capacity) IS NULL THEN 0
                ELSE (COUNT(od.order_detail_id) * 100.0 / MAX(v.capacity))
            END                                                                      AS usageRate
        FROM events e
        LEFT JOIN orders o
            ON e.event_id = o.event_id
            AND o.status = 'PAID'
        LEFT JOIN order_details od
            ON o.order_id = od.order_id
        JOIN venues v
            ON e.venue_id = v.venue_id
        WHERE e.venue_id = :venueId
        """, nativeQuery = true)

    VenueSummaryProjection getVenueStatisticSummary(@Param("venueId") Long venueId);



    @Query(value = """
        SELECT
            MONTH(o.created_at)          AS month,
            COALESCE(SUM(o.total_amount), 0) AS revenue
        FROM events e
        LEFT JOIN orders o
            ON e.event_id = o.event_id
            AND o.status = 'PAID'
        WHERE e.venue_id = :venueId
        AND YEAR(o.created_at) = YEAR(GETDATE())
        GROUP BY MONTH(o.created_at)
        ORDER BY month
        """, nativeQuery = true)
    List<VenueSummaryProjection> getMonthlyRevenueByVenue(@Param("venueId") Long venueId);



    List<Event> findByStatusOrderByCreatedAtDesc(EventStatus status, Pageable pageable);

    List<Event> findByStatusAndStartTimeBetween(EventStatus status, LocalDateTime start, LocalDateTime end);


    long countByOrganizerId(Long organizerId);

    long countByOrganizerIdAndStatus(Long organizerId, EventStatus status);

    @Query("""
        SELECT e FROM Event e
        WHERE e.organizer.id = :organizerId 
          AND (:#{#statusList.size()} = 0
               OR e.status IN :statusList)
          AND (:keyword IS NULL OR :keyword = ''
               OR LOWER(e.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)

    Page<Event> findByMultiStatusAndKeyword(
            @Param("organizerId") Long organizerId, // Thêm param này
            @Param("statusList") List<String> statusList,
            @Param("keyword")      String keyword,
            Pageable pageable
    );
}
