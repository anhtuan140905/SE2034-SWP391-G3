package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;

import java.time.Instant;


@Entity
@Table(name = "voucher_usages")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VoucherUsage {
    // Chỉ có created_by + created_at — insert một lần khi dùng voucher, không extends BaseAuditEntity

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "usage_id")
    private Long usageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id", nullable = false)
    private Voucher voucher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // user_id không lưu trực tiếp — query qua Orders.user_id khi cần
    @Column(name = "used_at")
    private Instant usedAt;

    @Column(name = "created_by")
    private String createdBy;

    @PrePersist
    protected void onCreate() {
        this.createdBy = SecurityUtil.getCurrentUsername();
        this.usedAt = Instant.now();
    }
}