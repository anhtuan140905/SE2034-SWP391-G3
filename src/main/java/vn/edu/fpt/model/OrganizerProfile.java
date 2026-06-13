package vn.edu.fpt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Column(name = "company_name", columnDefinition = "NVARCHAR(500)")
    private String companyName;

//  Thông tin thanh toán

    @Column(name = "bank_account_name", columnDefinition = "NVARCHAR(100)")
    private String bankAccountName;

    @Column(name = "bank_account_number", columnDefinition = "NVARCHAR(30)")
    private String bankAccountNumber;

    @Column(name = "bank_name", columnDefinition = "NVARCHAR(100)")
    private String bankName;

    @Column(name = "bank_branch", columnDefinition = "NVARCHAR(100)")
    private String bankBranch;

//  Company
    @Column(name = "business_type", columnDefinition = "NVARCHAR(50)")
    private String businessType;

    @Column(name = "tax_code", length = 20, unique = true)
    @NotBlank(message = "Mã số thuế không được để trống")
    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Mã số thuế phải bao gồm đúng 10 chữ số"
    )
    private String taxCode;

    @Column(name = "legal_name", length = 200)
    private String legalName;             // Họ tên / Tên công ty trên hoá đơn

    @Column(name = "legal_address", length = 300)
    private String legalAddress;          // Địa chỉ xuất hoá đơn
}
