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
    @Column(name = "display_order", nullable = true)
    private Integer displayOrder;
    @Column(name = "description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @Column(name = "zone_name", nullable = false, length = 100)
    private String zoneName; // Organizer tự đặt: VIP, Standard...
    @Column(name = "price", nullable = false)
    private BigDecimal price;
    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "sold_quantity", nullable = false)
    private Integer soldQuantity; // đếm tự động khi Ticket được tạo

    @OneToMany(mappedBy = "ticketType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Seat> seats;
}