package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "favourite_events")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class FavouriteEvent {
    // Toggle insert/delete — chỉ có created_at, không có updated_*, không extends BaseAuditEntity

    @EmbeddedId
    private FavouriteEventId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}