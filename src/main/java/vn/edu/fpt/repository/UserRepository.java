package vn.edu.fpt.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.constant.RoleName;
import vn.edu.fpt.modelview.response.homepage.FeaturedOrganizerDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

    List<User> findByFirstNameContainingIgnoreCaseOrMiddleNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName,
            String middleName,
            String lastName
    );

    @Query(value = "SELECT u.* FROM users u JOIN organizer_profiles op ON u.id = op.user_id WHERE op.status = :status", nativeQuery = true)
    List<User> findActiveOrganizers(@Param("status") String status);

    @Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) AS fullName,\n" +
            "       op.company_name AS companyName,\n" +
            "       COUNT(e.event_id) as eventCount\n" +
            "FROM organizer_profiles op\n" +
            "JOIN users u ON u.id = op.user_id\n" +
            "LEFT JOIN events e ON e.organizer_id = op.user_id \n" +
            "    AND e.status = 'APPROVED'\n" +
            "GROUP BY op.user_id, u.first_name, u.last_name, op.company_name\n" +
            "ORDER BY COUNT(e.event_id) DESC", nativeQuery = true)
    List<FeaturedOrganizerDto> getTopFeaturedOrganizer(Pageable pageable);


    List<User>findTop10ByOrderByUpdatedAtDesc();

    List<User> findTop10ByRoles_RoleNameOrderByUpdatedAtDesc(RoleName roleName);;
    // Dem so account Organizer con hoat dong tren nen tang
    @Query(value = "SELECT COUNT(u.id) FROM users u " +
            "JOIN user_roles ur ON u.id = ur.user_id " +
            "JOIN roles r ON ur.role_id = r.id " +
            "WHERE r.role_name = 'ROLE_ORGANIZER' AND u.is_active = 1", nativeQuery = true)
    Long countActiveOrganizers();
}
