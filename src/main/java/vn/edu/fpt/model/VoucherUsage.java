package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;

import java.time.Instant;


@Entity
@Table(name = "voucher_usages",
        indexes = @Index(name = "idx_voucher_usages_voucher_id", columnList = "voucher_id"),
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "order_id"),
                @UniqueConstraint(name = "uk_voucher_user", columnNames = {"voucher_id", "user_id"})
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VoucherUsage {

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

    // MỚI: denormalize để DB tự chặn race condition, không query qua Order nữa
    @Column(name = "user_id", nullable = false)
    private Long userId;

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