package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;
import vn.edu.fpt.common.SecurityUtil;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "order_details")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrderDetail {
    // Chỉ có created_by + created_at — immutable sau khi tạo, không extends BaseAuditEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_detail_id")
    private Long orderDetailId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @Column(name = "unit_price", nullable = false, precision = 15, scale = 2)
    private BigDecimal unitPrice; // Snapshot giá tại thời điểm đặt — không thay đổi dù Organizer edit price sau

    // 1 OrderDetail = 1 Ticket (không có quantity)
    @OneToOne(mappedBy = "orderDetail", cascade = CascadeType.ALL)
    private Ticket ticket;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdBy = SecurityUtil.getCurrentUsername();
        this.createdAt = Instant.now();
    }
}