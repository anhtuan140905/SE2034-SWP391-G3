package vn.edu.fpt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode // Bắt buộc với Embeddable composite PK
public class EventStaffId implements Serializable {

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "staff_id")
    private Long staffId;
}