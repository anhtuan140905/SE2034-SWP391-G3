package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;
import vn.edu.fpt.model.constant.TicketStatus;
import vn.edu.fpt.model.constant.EventStatus;

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
    @JoinColumn(name = "order_detail_id"
//            , nullable = false
            , unique = true)
    private OrderDetail orderDetail; // FK Ticket → OrderDetail (không phải ngược lại)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id"
//            , nullable = false
    )
    private User user; // Người sở hữu vé

    @Column(name = "is_checked_in")
    private boolean isCheckedIn;

    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode; // QR code độc lập mỗi vé
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "checked_in_by")
//    private User checkedInBy; // Staff thực hiện check-in — nullable trước khi check-in

    //    @Column(name = "checked_in_at")
//    private Instant checkedInAt;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id")
    private TicketType ticketType;
    @Column(name = "status", nullable = false, length = 20)
    private TicketStatus status;

//    @Version
//    @Column(name = "version", nullable = false)
//    private Integer version; // OptimisticLock — xử lý 2 Staff scan QR cùng lúc

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}