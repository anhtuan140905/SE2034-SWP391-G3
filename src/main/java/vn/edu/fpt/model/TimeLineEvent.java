package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
@Entity
@Table(name = "time_line")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TimeLineEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long timeId;
    @Column(name = "time")
    private LocalTime time;
    @Column(name = "decription",columnDefinition = "NVARCHAR(500)")
    private String  description;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;
}
