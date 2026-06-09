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


    //---------------------------------------------------------------------------------------
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

    // Đếm số lượng sự kiện theo trạng thái
    long countByStatus(EventStatus status);

    // Lấy 3 sự kiện Pending mới nhất
    List<Event> findByStatusOrderByCreatedAtDesc(EventStatus status, Pageable pageable);

    // Lấy sự kiện diễn ra trong ngày (status = APPROVED)
    List<Event> findByStatusAndStartTimeBetween(EventStatus status, LocalDateTime start, LocalDateTime end);

    long countByOrganizerId(Long organizerId);

    long countByOrganizerIdAndStatus(Long organizerId, EventStatus status);

}
