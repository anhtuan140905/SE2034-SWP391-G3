
package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.OrderStatus;
import vn.edu.fpt.modelview.response.homepage.TicketDTO;


import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

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

    @Query( value = """

            SELECT
                 o.order_id as orderId,
                 e.title as eventName,
                 ec.category_name as categoryName,
                 e.thumbnail_url as thumbnailUrl,
                 e.start_time as startTime,
                 e.end_time as endTime,
                 a.specific_address as specificAddress,
                 w.name as wardName,
                 c.name as cityName,
                 tp.zone_name as zoneName,
                 o.total_amount as price,
                 t.is_checked_in as status,
                 count(od.order_detail_id) as ticketCount
             FROM orders o
             JOIN order_details od ON od.order_id = o.order_id
             JOIN seats s ON s.seat_id = od.seat_id
             JOIN ticket_types tp ON tp.ticket_type_id = s.ticket_type_id
             JOIN events e ON e.event_id = tp.event_id
             JOIN tickets t ON t.order_detail_id = od.order_detail_id
             LEFT JOIN event_categories ec ON ec.category_id = e.category_id
             LEFT JOIN addresses a ON a.id = e.address_id
             LEFT JOIN wards w ON w.id = a.ward_id
             LEFT JOIN city c ON c.id = w.city_id
             WHERE o.user_id = :userId
            GROUP BY
                o.order_id,
                e.title,
                ec.category_name,
                e.thumbnail_url,
                e.start_time,
                e.end_time,
                a.specific_address,
                w.name,
                c.name,
                tp.zone_name,
                o.total_amount,
                t.is_checked_in 

""", nativeQuery = true)
    List<TicketProjection> viewOrder(Long userId);


    @Query(value = """
    SELECT
        o.order_id as orderId,
        e.title as eventName,
        ec.category_name as categoryName,
        e.thumbnail_url as thumbnailUrl,
        e.start_time as startTime,
        a.specific_address as specificAddress,
        w.name as wardName,
        c.name as cityName,
        tp.zone_name as zoneName,
        t.ticket_id as ticketId,
        t.ticket_code as ticketCode,
        t.qr_code as qrCode,
        p.amount as price,  
        t.is_checked_in as status,
FORMAT(CAST(od.created_at AS datetime2), 'dd/MM/yyyy HH:mm') as createdAt,
        p.payment_code as paymentCode,
        s.row_label as row,
        s.seat_number as seat
FROM orders o
JOIN order_details od ON od.order_id = o.order_id
JOIN seats s ON s.seat_id = od.seat_id
JOIN ticket_types tp ON tp.ticket_type_id = s.ticket_type_id
JOIN events e ON e.event_id = tp.event_id
JOIN tickets t ON t.order_detail_id = od.order_detail_id
LEFT JOIN event_categories ec ON ec.category_id = e.category_id
LEFT JOIN addresses a ON a.id = e.address_id
LEFT JOIN wards w ON w.id = a.ward_id
LEFT JOIN city c ON c.id = w.city_id
LEFT JOIN payments p ON p.order_id = o.order_id
WHERE o.order_id = :orderId
GROUP BY 
o.order_id,
e.title,
ec.category_name,
e.thumbnail_url,
e.start_time,
a.specific_address,
w.name,
c.name,
tp.zone_name,
t.ticket_id,
t.ticket_code,
t.qr_code,
 t.is_checked_in,
od.created_at,
p.payment_code,
p.amount,
s.row_label,
s.seat_number
    """, nativeQuery = true)
    List<TicketProjection> viewOrderDetail(@Param("orderId") Long orderId);


    @Query("SELECT DISTINCT e.category.categoryId " +
            "FROM Order o " +
            "JOIN o.event e " +
            "WHERE o.user.id = :userId " +
            "AND o.status = :status")
    List<Long> findPurchasedCategoryIdsByUserId(
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
}
