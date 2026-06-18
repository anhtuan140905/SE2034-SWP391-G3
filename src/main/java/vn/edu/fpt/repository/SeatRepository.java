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
    @Query("SELECT s FROM Seat s \n" +
            "LEFT JOIN s.ticket t \n" +
            "LEFT JOIN SeatLock sl \n" +
            "ON sl.seat = s \n" +
            "AND sl.event.eventId = :eventId \n" +
            "AND sl.expiresAt > :now \n" +
            "WHERE s.ticketType.event.eventId = :eventId\n" +
            "ORDER BY s.ticketType.ticketTypeId, s.rowLabel, s.seatNumber"
    )
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

    // Thêm vào SeatRepository
    @Query("""
    SELECT sl.seat.seatId FROM SeatLock sl
    WHERE sl.event.eventId = :eventId
    AND sl.expiresAt > :now
""")
    Set<Long> findLockedSeatIds(
            @Param("eventId") Long eventId,
            @Param("now") Instant now
    );
}