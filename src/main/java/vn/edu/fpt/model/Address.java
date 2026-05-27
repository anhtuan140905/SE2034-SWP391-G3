package vn.edu.fpt.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "street_address", nullable = false, columnDefinition = "NVARCHAR(255)")
    private String streetAddress;
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String ward;
    @Column(nullable = false, columnDefinition = "NVARCHAR(255)")
    private String city;
}
