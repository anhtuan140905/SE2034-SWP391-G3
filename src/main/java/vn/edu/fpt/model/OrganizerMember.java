package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.constant.OrganizerMemberRole;

import java.io.Serializable;
import java.time.Instant;


@Entity
@Table(name = "organizer_members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerMember {
    @EmbeddedId
    private OrganizerMemberId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_Id")
    private User userId;

    @ManyToOne
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private Event eventId;

    @Enumerated(EnumType.STRING)
    private OrganizerMemberRole memberRole;

    private Instant joinedAt;

}

