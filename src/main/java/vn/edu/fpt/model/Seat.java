package vn.edu.fpt.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.common.SecurityUtil;

import java.time.Instant;
@Entity
@Table(name = "seats")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Seat {
    // Không extends BaseAuditEntity vì Seat chỉ có created_by, created_at
    // Không có updated_* — Seat không bị UPDATE sau khi generate

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long seatId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_type_id", nullable = false)
    private TicketType ticketType;

    @Column(name = "row_label", nullable = false, length = 5)
    private String rowLabel; // A, B, C...

    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber; // 1, 2, 3...

    @OneToOne(mappedBy = "seat", fetch = FetchType.LAZY)
    private Ticket ticket;
}
