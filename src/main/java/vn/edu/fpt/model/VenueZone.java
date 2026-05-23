package vn.edu.fpt.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venue_zones")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class VenueZone extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "zone_id")
    private Long zoneId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @Column(name = "zone_name", nullable = false)
    private String zoneName;

    @Column(name = "rows", nullable = false)
    private Integer rows;

    @Column(name = "seats_per_row", nullable = false)
    private Integer seatsPerRow;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rowLabel ASC, seatNumber ASC")
    private List<Seat> seats; // List vì cần sort A1, A2... khi render sơ đồ ghế

    @OneToMany(mappedBy = "zone")
    private List<TicketType> ticketTypes; // List vì hiển thị theo thứ tự zone
}