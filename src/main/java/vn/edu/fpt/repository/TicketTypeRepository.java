package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import vn.edu.fpt.model.TicketType;

public interface TicketTypeRepository extends CrudRepository<TicketType, Integer> {
    // Atomic increment — tránh race condition khi nhiều order confirm đồng thời
    @Modifying
    @Query("""
        UPDATE TicketType tt
        SET tt.soldQuantity = tt.soldQuantity + :delta
        WHERE tt.ticketTypeId = :ticketTypeId
        """)
    void incrementSoldQuantity(Long ticketTypeId, int delta);
}
