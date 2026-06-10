package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;
import vn.edu.fpt.model.constant.TicketStatus;

import java.time.Instant;

@Entity
@Table(name = "tickets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Ticket {
    // updated_at có nhưng không có updated_by — checked_in_by thay thế
    // Không extends BaseAuditEntity vì không đủ 4 fields chuẩn

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id", unique = true)
    private OrderDetail orderDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "is_checked_in")
    private boolean isCheckedIn;

    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "status")
    private TicketStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;

    @PrePersist
    protected void onCreate() {
        this.createdBy = SecurityUtil.getCurrentUsername();
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}