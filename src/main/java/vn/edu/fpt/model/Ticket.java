package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;


import java.time.Instant;

@Entity
@Table(name = "tickets")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_detail_id",
            nullable = false
            , unique = true)
    private OrderDetail orderDetail; // FK Ticket → OrderDetail (không phải ngược lại)

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, unique = true)
    private Seat seat;

    @Column(name = "ticket_code", nullable = false, unique = true, length = 50)
    private String ticketCode; // gen UUID khi tạo

    @Column(name = "is_checked_in")
    private boolean isCheckedIn;

    @Column(name = "qr_code", nullable = false, unique = true)
    private String qrCode; // QR code độc lập mỗi vé

    @Column(name = "checked_in_at")
    private Instant checkedInAt;

    @Version
    @Column(name = "version")
    private Long version; // optimistic lock khi check-in
}