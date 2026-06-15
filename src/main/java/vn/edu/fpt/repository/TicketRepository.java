package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Ticket;
import vn.edu.fpt.model.constant.TicketStatus;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
//    @Query("SELECT COUNT(*) FROM Ticket")
//    long ticketIssued();
////
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





}
