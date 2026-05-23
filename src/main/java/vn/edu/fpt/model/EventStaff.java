package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "event_staffs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventStaff {
    // Junction table — chỉ có created_by + created_at, không extends BaseAuditEntity

    @EmbeddedId
    private EventStaffId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("staffId")
    @JoinColumn(name = "staff_id")
    private User staff;

    @Column(name = "assigned_at")
    private Instant assignedAt;

    @Column(name = "created_by")
    private String createdBy;


    @PrePersist
    protected void onCreate() {
        assignedAt = Instant.now();
    }
}