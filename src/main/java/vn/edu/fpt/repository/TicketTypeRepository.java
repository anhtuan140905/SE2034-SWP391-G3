package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.TicketType;

import java.util.List;

public interface TicketTypeRepository extends CrudRepository<TicketType, Integer> {
    // Atomic increment — tránh race condition khi nhiều order confirm đồng thời
    @Modifying
    @Query("UPDATE TicketType tt " +
            "SET tt.soldQuantity = tt.soldQuantity + :delta " +
            "WHERE tt.ticketTypeId = :ticketTypeId")
    void incrementSoldQuantity(Long ticketTypeId, int delta);

    @Query("SELECT tt.event.id, MIN(tt.price) " +
            "FROM TicketType tt " +
            "WHERE tt.event.id IN :eventIds " +
            " GROUP BY tt.event.id")
    List<Object[]> findMinPriceByEventIds(@Param("eventIds") List<Long> eventIds);

    List<TicketType> findByEvent_EventId(Long eventId);
}
