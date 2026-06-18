package vn.edu.fpt.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
public class User extends BaseAuditEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "first_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String firstName;
    @Column(name = "middle_name", columnDefinition = "NVARCHAR(255)")
    private String middleName;
    @Column(name = "last_name", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String lastName;
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;
    @Column(name = "password_hash", nullable = true)
    private String passwordHash;
    @Column(name = "phone", length = 10)
    private String phone;
    @Column(name = "gender", length = 10)
    private String gender;
    @Column(name = "dob")
    private LocalDate dob;
    private String avatar;
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;
    @OneToOne(mappedBy = "user", cascade =  {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private OrganizerProfile organizerProfile;
}