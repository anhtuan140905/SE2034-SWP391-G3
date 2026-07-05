package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.constant.DiscountType;


import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vouchers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Voucher extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "voucher_id")
    private Long voucherId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String title;

    @Column(name = "description", columnDefinition = "NVARCHAR(MAX)")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 10)
    private DiscountType discountType;

    @Column(name = "discount_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_usage", nullable = false)
    private Integer maxUsage;

    // LocalDateTime vì đây là thời gian hiển thị với user — "voucher có hiệu lực từ ... đến ..."
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "voucher", cascade = CascadeType.PERSIST)
    private Set<VoucherUsage> usages; // Set vì không cần thứ tự, tra cứu theo voucher
}