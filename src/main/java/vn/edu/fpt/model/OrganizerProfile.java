package vn.edu.fpt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.edu.fpt.model.constant.OrganizerStatus;

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

    @Column(name = "tax_code", length = 20, unique = true)
    @NotBlank(message = "Mã số thuế không được để trống")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mã số thuế phải bao gồm đúng 10 chữ số"
    )
    private String taxCode;

    @Column(name = "bank_account")
    private String bankAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OrganizerStatus status;
}
