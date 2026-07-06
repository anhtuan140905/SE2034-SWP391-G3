package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Ticket;
import vn.edu.fpt.model.constant.TicketStatus;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    @Query("SELECT COUNT(*) FROM Ticket")
    long ticketIssued();
      @Query(value = "select COUNT(*) from tickets t \n" +
              "join order_details od on t.order_detail_id = od.order_detail_id\n" +
              "join orders o on o.order_id = od.order_id\n" +
              "where t.is_checked_in = 1 and o.event_id = :eventId",nativeQuery = true)
    Long countCheckInTicketsByEventId(@Param("eventId") Long eventId);
    @Query(value = "SELECT COUNT(*) AS checked_in_count\n" +
            "FROM tickets t\n" +
            "JOIN seats s ON t.seat_id = s.seat_id\n" +
            "WHERE s.ticket_type_id = :ticketTypeId\n" +
            "  AND t.is_checked_in = 1",nativeQuery = true)
    Long countCheckInTicketsByTicketTypeId(Long ticketTypeId);
//    long countByUserId(Long userId);
//
//    @Query("""
//        SELECT COUNT(DISTINCT t.orderDetail.order.event.id)
//        FROM Ticket t
//        WHERE t.user.id = :userId
//        AND t.isCheckedIn = true
//        """)
//    long countDistinctEventCheckedInByUserId(@Param("userId") Long userId);


//boolean existsByQrCode(String qrCode);
//    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketType.ticketTypeId = :ticketTypeId AND t.status <> 0")
//    Integer getNumTicketSelled(Long ticketTypeId);

    @Query("""
            SELECT SUM(soldQuantity)
            FROM TicketType
            """)
    long countAllSoldTickets();

    @Query("""
            SELECT COUNT(DISTINCT t.ticketId)
            FROM OrderDetail ord
            JOIN Ticket t on ord.orderDetailId = t.orderDetail.orderDetailId
            where ord.order.user.id = :userId 
            """)
    long countAllTicketOfUser(@Param("userId") Long userId);


    @Query("""
SELECT COUNT(DISTINCT t.ticketId)
FROM Ticket t
JOIN OrderDetail ord on t.orderDetail.orderDetailId = ord.orderDetailId
JOIN Order o on ord.order.orderId = o.orderId
WHERE o.user.id = :userId
AND t.isCheckedIn = false
AND o.event.endTime > CURRENT_TIMESTAMP
AND o.status = 'PAID'
""")
    long countUpcomingTicket(@Param("userId") Long userId);


    @Query("""
            SELECT COUNT(DISTINCT t.ticketId) 
            
            FROM Order o
            JOIN OrderDetail ord ON o.orderId = ord.order.orderId
            JOIN Ticket t ON ord.orderDetailId = t.orderDetail.orderDetailId
            WHERE o.user.id = :userId
            AND t.isCheckedIn = true
            AND o.status = 'PAID'
            """)
    long countUsedTicket(@Param("userId") Long userId);

    @Query("""
            SELECT COUNT(DISTINCT t.ticketId)
            FROM Order o
            JOIN OrderDetail ord ON o.orderId = ord.order.orderId
            JOIN Ticket t ON ord.orderDetailId = t.orderDetail.orderDetailId
            WHERE o.user.id = :userId
            AND o.status = 'PAID'
            AND t.isCheckedIn = false
            AND o.event.endTime < CURRENT_TIMESTAMP
            """)
    long countExpiredTicket(@Param("userId") Long userId);


    @Query(value = """
            select 
            t.ticket_id as ticketId,
            ec.category_name as categoryName,
            e.thumbnail_url as thumbnailUrl,
            e.title as eventName,
            e.start_time as startTime,
            e.end_time as endTime,
            tt.zone_name as zoneName,
            a.specific_address as specificAddress,
            w.name wardName,
            c.name cityName,
            tt.price as price,
            t.is_checked_in as checkedIn
            
            from tickets t 
            join order_details ord on t.order_detail_id = ord.order_detail_id
            join orders o on ord.order_id = o.order_id
            join seats s on t.seat_id = s.seat_id
            join ticket_types tt on s.ticket_type_id = tt.ticket_type_id
            join events e on tt.event_id = e.event_id
            left join event_categories ec on e.category_id = ec.category_id
            left join addresses a on e.address_id = a.id
            left join wards w on a.ward_id = w.id
            left join city c on w.city_id = c.id
            where o.user_id = :userId
            
            
            """, nativeQuery = true)
    List<TicketProjection> viewTicket(@Param("userId") Long userId);


    @Query(value = """
            select 
            t.ticket_id as ticketId,
            t.qr_code as qrCode,
            t.ticket_code as ticketCode,
            t.is_checked_in as status,
            ord.order_id as orderId,
            ec.category_name as categoryName,
            e.title as eventName,
            e.thumbnail_url as thumbnailUrl,
            e.start_time as startTime,
            e.end_time as endTime,
            tt.zone_name as zoneName,
            s.row_label as row,
            s.seat_number as seat
            from
            tickets t 
            join order_details ord on t.order_detail_id = ord.order_detail_id
            join seats s on ord.seat_id = s.seat_id
            join ticket_types tt on s.ticket_type_id = tt.ticket_type_id
            join events e on tt.event_id = e.event_id
            left join event_categories ec on e.category_id = ec.category_id
            left join addresses a on e.address_id = a.id
            left join wards w on a.ward_id = w.id
            left join city c on w.city_id = c.id
            where t.ticket_id = :ticketId
            """, nativeQuery = true)
    TicketProjection viewDetailTicket(@Param("ticketId") Long ticketID);

    @Query("SELECT COUNT(t) FROM Ticket t " +
            "WHERE t.orderDetail.order.user.id = :userId " +
            "AND t.seat.ticketType.event.eventId = :eventId " +
            "AND t.orderDetail.order.status = 'PAID'")
    long countCompletedTicketsByUserAndEvent(
            @Param("userId") Long userId,
            @Param("eventId") Long eventId
    );

    @Query(value = "SELECT tp.event_id, COUNT(t.ticket_id) " +
            "FROM tickets t " +
            "JOIN order_details od ON t.order_detail_id = od.order_detail_id " +
            "JOIN orders o ON od.order_id = o.order_id " +
            "JOIN seats s ON t.seat_id = s.seat_id " +
            "JOIN ticket_types tp ON s.ticket_type_id = tp.ticket_type_id " +
            "WHERE tp.event_id IN :eventIds " +
            "AND o.status = 'PAID' " +
            "GROUP BY tp.event_id",
            nativeQuery = true)
    List<Object[]> countSoldTicketsByEventIds(@Param("eventIds") List<Long> eventIds);
}
