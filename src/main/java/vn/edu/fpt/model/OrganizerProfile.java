package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "organizer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganizerProfile {
    @Id
    @Column(name = "user_id")       // Shared PK với Users: ae hiểu cái thằng profile này nó có mqh 1-1 về thg user
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId                          // userId lấy từ Users.userId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "tax_code", length = 20)
    private String taxCode;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "status", nullable = false, length = 20)
    private String status;
}
