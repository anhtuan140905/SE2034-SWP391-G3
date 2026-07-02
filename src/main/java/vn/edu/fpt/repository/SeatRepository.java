package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.Seat;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface SeatRepository extends JpaRepository<Seat,Long>
{
    @Query("SELECT DISTINCT s FROM Seat s " +
            "JOIN FETCH s.ticketType t " +
            "LEFT JOIN FETCH s.ticket tk " +
            "LEFT JOIN FETCH s.seatLocks sl " +
            "WHERE t.event.eventId = :eventId " +
            "ORDER BY t.ticketTypeId, s.rowLabel, s.seatNumber")
    List<Seat> findAllByEventIdWithStatus(
            @Param("eventId") Long eventId,
            @Param("now") Instant now
    );
    // Dùng cho SeatLockService sau này — check seat còn available không
    @Query("""
        SELECT s FROM Seat s
        LEFT JOIN s.ticket t
        LEFT JOIN SeatLock sl
            ON sl.seat = s
            AND sl.event.eventId = :eventId
            AND sl.expiresAt > :now
        WHERE s.seatId IN :seatIds
        AND t IS NULL
        AND sl IS NULL
    """)
    List<Seat> findAvailableSeats(
            @Param("seatIds") List<Long> seatIds,
            @Param("eventId") Long eventId,
            @Param("now") Instant now
    );

}