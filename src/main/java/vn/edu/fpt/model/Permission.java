package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "permission_key", nullable = false, unique = true, length = 100)
    private String permissionKey;  // "CAN_EDIT_EVENT", "CAN_CHECK_IN", ...

    @Column(nullable = false, columnDefinition = "NVARCHAR(500)")
    private String description;
}