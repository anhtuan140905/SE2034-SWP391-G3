package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.edu.fpt.model.Ticket;

public interface TickRepository extends JpaRepository<Ticket,Long> {
    boolean existsByQrCode(String qrCode);
    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.ticketType.ticketTypeId = :ticketTypeId AND t.status <> 0")
    Integer getNumTicketSelled(Long ticketTypeId);
}
