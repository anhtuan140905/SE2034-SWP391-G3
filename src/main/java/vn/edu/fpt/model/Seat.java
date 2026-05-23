package vn.edu.fpt.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
@Entity
@Table(name = "seats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Seat {
    // Không extends BaseAuditEntity vì Seat chỉ có created_by, created_at
    // Không có updated_* — Seat không bị UPDATE sau khi generate

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private VenueZone zone;
    // Không FK thẳng vào Venue — chỉ cần zone_id là đủ

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel; // A, B, C...

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3...

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

}
