package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Venue extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "venue_id")
    private Long venueId;

    @Column(name = "venue_name", nullable = false)
    private String venueName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "capacity", nullable = false)
    private Integer capacity; // Tính động = SUM(rows × seats_per_row) của tất cả Zone

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "imageUrl")
    private String imageUrl;

    @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("zoneName ASC")
    private List<VenueZone> zones; // List vì có thứ tự Zone A, B, C... (AE hiểu kĩ mấy cái này sau mà còn biết dùng cái nào tốt List/Set/Map)

    @OneToMany(mappedBy = "venue")
    private List<Event> events; // List vì 1 Venue host nhiều Event theo thời gian
}