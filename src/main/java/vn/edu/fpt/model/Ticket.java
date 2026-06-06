package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;

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
    @JoinColumn(name = "order_detail_id", nullable = false, unique = true)
    private OrderDetail orderDetail; // FK Ticket → OrderDetail (không phải ngược lại)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người sở hữu vé

    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode; // QR code độc lập mỗi vé

    @Column(name = "is_checked_in", nullable = false)
    private Boolean isCheckedIn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_in_by")
    private User checkedInBy; // Staff thực hiện check-in — nullable trước khi check-in

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version; // OptimisticLock — xử lý 2 Staff scan QR cùng lúc

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt; // Hữu ích để biết thời điểm check-in, không có updated_by

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