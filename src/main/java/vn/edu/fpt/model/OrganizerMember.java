package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.model.constant.RoleName;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;


// OrganizerMember.java — bỏ @EmbeddedId, đổi FK user → userRole
@Entity
@Table(name = "organizer_members",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_role_id", "event_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrganizerMember extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK vào user_roles — biết cả user lẫn role của người đó
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_role_id", nullable = false)
    private UserRole userRole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "joined_at")
    private Instant joinedAt;

    @PrePersist
    public void prePersist() {
        this.joinedAt = Instant.now();
    }

    // Permissions của member này trong event này
    @OneToMany(mappedBy = "organizerMember",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private Set<OrganizerMemberPermission> permissions = new HashSet<>();

    // Helper: lấy nhanh role mà không cần đi qua userRole
    public RoleName getRoleName() {
        return this.userRole.getRole().getRoleName();
    }
}

