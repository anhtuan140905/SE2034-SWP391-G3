
package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;
import vn.edu.fpt.modelview.response.organizer.OrderDto;


import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = """
    SELECT o.order_id AS orderId,CONCAT(u.last_name,' ',u.middle_name,' ',u.first_name) AS fullName,
               u.phone AS phone,FORMAT(o.created_at, 'dd/MM/yyyy HH:mm') AS createAt,
                   CONCAT(COUNT(od.order_detail_id),N' vé · ',MAX(tt.zone_name)) AS quantityTicket,
        o.total_amount AS totalAmount,o.status AS status
    FROM orders o
    JOIN order_details od ON o.order_id = od.order_id
    JOIN seats s ON s.seat_id = od.seat_id
    JOIN ticket_types tt ON s.ticket_type_id = tt.ticket_type_id
    JOIN users u ON o.user_id = u.id
    WHERE o.event_id = :eventId AND ( :keyword IS NULL OR :keyword = ''
    OR CONCAT(u.last_name,' ',u.middle_name,' ',u.first_name) LIKE :keyword
    OR o.order_id LIKE :keyword ) AND (:status IS NULL OR :status = '' OR o.status = :status)
    GROUP BY o.order_id,u.last_name,u.middle_name,u.first_name,u.phone,
        o.created_at,o.total_amount,o.status""", nativeQuery = true)
    Page<OrderProjection> findOrderByEventId(
            @Param("eventId") Long eventId, @Param("keyword") String keyword, @Param("status") String status, Pageable pageable
            );
    // Dùng bởi CheckoutPageController — eager load details + tickets
    @Query("""
    SELECT o FROM Order o
    LEFT JOIN FETCH o.orderDetails od
    LEFT JOIN FETCH od.seat
    LEFT JOIN FETCH od.ticket
    WHERE o.orderId = :orderId
    """)
    Optional<Order> findByIdWithDetails(@Param("orderId") Long orderId);

    List<Order> findByStatusAndExpiresAtBefore(OrderStatus status, Instant now);
    List<Order> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    List<Order> findTop10ByEvent_OrganizerIdOrderByCreatedAtDesc(Long userId);

    void deleteOrderByOrderIdAndUser(Long orderId, User currentUser);



    @Query("""
    SELECT DISTINCT e FROM Order o
    JOIN o.event e
    JOIN FETCH e.category
    JOIN FETCH e.address a
    JOIN FETCH a.ward w
    JOIN FETCH w.city
    WHERE o.user.id = :userId
    AND o.status = :status
""")
    List<Event> findPurchasedEvents(
            @Param("userId") Long userId,
            @Param("status") OrderStatus status
    );

    @Query("""
    SELECT sum(o.totalAmount)
    from Event e left join Order o on e.eventId = o.event.eventId
    where o.event.eventId = :eventId and o.status = 'PAID'
    """)
    BigDecimal calculateGrossRevenueByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(t) FROM Ticket t JOIN t.orderDetail od JOIN od.order o WHERE o.event.eventId = :eventId AND o.status = 'PAID'")
    Long countPaidTicketsByEventId(@Param("eventId") Long eventId);

    @Query("SELECT CAST(o.createdAt AS date) as orderDate, SUM(o.totalAmount) " +
           "FROM Order o " +
           "WHERE o.event.eventId = :eventId AND o.status = :status AND o.createdAt >= :startDate " +
           "GROUP BY CAST(o.createdAt AS date) " +
           "ORDER BY orderDate ASC")
    List<Object[]> getDailyRevenueByEventId(@Param("eventId") Long eventId, @Param("status") OrderStatus status, @Param("startDate") Instant startDate);
}
