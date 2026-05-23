package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ticket_types")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class TicketType extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_type_id")
    private Long ticketTypeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zone_id", nullable = false)
    private VenueZone zone; // Bridge để query giá khi User chọn ghế theo zone

    @Column(name = "type_name", nullable = false)
    private String typeName; // Auto-fill từ zone_name khi Organizer setup pricing

    @Column(name = "price", nullable = false, precision = 15, scale = 2)
    private BigDecimal price;

    @Column(name = "stock", nullable = false)
    private Integer stock; // Auto-fill = rows × seats_per_row

    @OneToMany(mappedBy = "ticketType")
    private List<OrderDetail> orderDetails;
}