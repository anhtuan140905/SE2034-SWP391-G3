package vn.edu.fpt.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "seat_locks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_seat_locks_event_seat",
                columnNames = {"event_id", "seat_id"} // Chặn double-lock ở mức DB
        )
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SeatLock {
    // Bảng kỹ thuật thuần túy — không có audit fields, không extends BaseAuditEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lock_id")
    private Long lockId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "locked_at", nullable = false)
    private Instant lockedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @PrePersist
    protected void onCreate() {
        lockedAt = Instant.now();
        expiresAt = lockedAt.plus(5, ChronoUnit.MINUTES);
    }
}