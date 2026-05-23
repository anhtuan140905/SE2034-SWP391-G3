package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.constant.SettlementStatus;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "settlements")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Settlement extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "settlement_id")
    private Long settlementId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false, unique = true) // UNIQUE — 1 Event chỉ settle 1 lần
    private Event event;

    @Column(name = "gross_revenue", nullable = false, precision = 15, scale = 2)
    private BigDecimal grossRevenue;

    @Column(name = "platform_fee", nullable = false, precision = 15, scale = 2)
    private BigDecimal platformFee;

    @Column(name = "payout_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal payoutAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SettlementStatus status;

    @Column(name = "paid_at")
    private Instant paidAt; // Nullable — chỉ có giá trị khi status = COMPLETED
}