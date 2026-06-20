package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Ticket;
import vn.edu.fpt.model.constant.TicketStatus;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    @Query("SELECT COUNT(*) FROM Ticket")
    long ticketIssued();

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
SELECT COUNT(odr.orderDetailId)
FROM OrderDetail odr
JOIN Order o ON odr.order.orderId = o.orderId
where o.user.id = :userId
""")
    long countAllTicketOfUser(@Param("userId") Long userId);


    @Query("""
SELECT COUNT(DISTINCT t.ticketId)
FROM Ticket t
JOIN OrderDetail ord on t.orderDetail.orderDetailId = ord.orderDetailId
JOIN Order o on ord.order.orderId = o.orderId
WHERE o.user.id = :userId
AND t.isCheckedIn = false
AND ord.order.event.endTime >= CURRENT_TIMESTAMP
""")
    long countUpcomingTicket(@Param("userId") Long userId);


    @Query("""
SELECT COUNT(DISTINCT t.ticketId) 

From Order o
JOIN OrderDetail ord ON o.orderId = ord.order.orderId
JOIN Ticket t ON ord.orderDetailId = t.orderDetail.orderDetailId
 where o.user.id = :userId
AND t.isCheckedIn = true
""")
    long countUsedTicket(@Param("userId") Long userId);

    @Query("""
select count(distinct t.ticketId)
from Event e
join Order o on e.eventId = o.event.eventId
JOIN OrderDetail ord ON o.orderId = ord.order.orderId
JOIN Ticket t ON ord.orderDetailId = t.orderDetail.orderDetailId
 where o.user.id = :userId
AND t.isCheckedIn = false
AND ord.order.event.endTime < CURRENT_TIMESTAMP
""")
    long countExpiredTicket(@Param("userId") Long userId);



}
