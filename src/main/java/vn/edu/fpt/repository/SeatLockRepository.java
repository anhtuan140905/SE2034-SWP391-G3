package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.SeatLock;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatLockRepository extends JpaRepository<SeatLock, Long> {
    @Modifying
    @Query("DELETE FROM SeatLock sl WHERE sl.seat.seatId IN :seatIds")
    void deleteAllBySeatIdIn(@Param("seatIds") List<Long> seatIds);

    Optional<SeatLock> findBySeatSeatIdAndUserId(Long seatId, Long userId);

    void deleteBySeatSeatIdIn(List<Long> seatIds);

    List<SeatLock> findByExpiresAtBefore(Instant expiresAt);

    Optional<SeatLock> findBySeatSeatId(Long seatId);

    void deleteBySeatSeatIdAndUserId(Long seatId, Long userId);
}

