package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;

// OrganizerMemberPermission.java — ENTITY MỚI
@Entity
@Table(name = "organizer_member_permissions",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"organizer_member_id", "permission_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizerMemberPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_member_id", nullable = false)
    private OrganizerMember organizerMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
}