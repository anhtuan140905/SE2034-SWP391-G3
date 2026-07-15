package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
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
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("Select u from User u where u.id= :userId")
    User findByUserId(@Param("userId") Long userId);
    User findByEmail(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.id = :id")
    Optional<User> findByIdWithUserRoles(@Param("id") Long id);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.userRoles ur LEFT JOIN FETCH ur.role WHERE u.email = :email")
    Optional<User> findByEmailWithUserRoles(@Param("email") String email);


@Query(value = "select*\n" +
        "from users u\n" +
        "where \n" +
        "(lower(u.email) like lower(concat('%', :keyword, '%')) \n" +
        "or lower(u.last_name) like lower(concat('%', :keyword, '%'))\n" +
        "or lower(u.middle_name) like lower(concat('%', :keyword, '%'))\n" +
        "or lower (u.first_name) like lower(concat('%', :keyword, '%')))"
        ,nativeQuery = true)
    List<User> seachUser(@Param("keyword") String keyword);

    @Query(value = "SELECT u.* FROM users u JOIN organizer_profiles op ON u.id = op.user_id", nativeQuery = true)
    List<User> findActiveOrganizers();

    @Query(value = "SELECT CONCAT(u.first_name, ' ', u.last_name) AS fullName,\n" +
            "       op.company_name AS companyName,\n" +
            "       COUNT(e.event_id) as eventCount\n" +
            "FROM organizer_profiles op\n" +
            "JOIN users u ON u.id = op.user_id\n" +
            "LEFT JOIN events e ON e.organizer_id = op.user_id \n" +
            "    AND e.status = 'ACTIVE'\n" +
            "GROUP BY op.user_id, u.first_name, u.last_name, op.company_name\n" +
            "ORDER BY COUNT(e.event_id) DESC", nativeQuery = true)
    List<FeaturedOrganizerDto> getTopFeaturedOrganizer(Pageable pageable);


    List<User> findTop10ByOrderByUpdatedAtDesc();

    @Query("""
            SELECT DISTINCT u FROM User u
            JOIN u.userRoles ur
            JOIN ur.role r
            WHERE r.roleName = :roleName
            ORDER BY u.updatedAt DESC
            LIMIT 10
            """)
    List<User> findTop10ByRoleNameOrderByUpdatedAtDesc(@Param("roleName") RoleName roleName);

    // Dem so account Organizer con hoat dong tren nen tang
    @Query("""
                SELECT COUNT(DISTINCT u)
                FROM User u
                JOIN u.userRoles ur
                JOIN ur.role r
                WHERE r.roleName = :roleName
                  AND u.isActive = :isActive
            """)
    long countOrganizersByStatus(
            @Param("roleName") RoleName roleName,
            @Param("isActive") Boolean isActive
    );

    //Lay thong tin cua organizer
    @EntityGraph(attributePaths = {
            "address",
            "address.ward",
            "address.ward.city",
            "organizerProfile"
    })
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findOrganizerInformationById(@Param("id") Long id);

    // Kiem tra trung email khi tao tai khoan cho Organizer
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE LOWER(u.email) = LOWER(:email)")
    boolean existsByEmail(@Param("email") String email);

    @Query("SELECT w.city.name FROM User u " +
            "JOIN u.address a " +
            "JOIN a.ward w " +
            "WHERE u.id = :userId")
    Optional<String> findCityNameByUserId(@Param("userId") Long userId);

    //Hien thi danh sach Organizer: search + filter + phan trang
    @Query("""
                SELECT DISTINCT u 
                FROM User u
                JOIN u.userRoles ur
                JOIN ur.role r
                WHERE r.roleName = :roleName
                    AND (:keyword IS NULL OR u.email LIKE %:keyword% OR u.phone LIKE %:keyword%)
                    AND (:isActive IS NULL OR u.isActive = :isActive)
                ORDER BY u.createdAt DESC 
            """)
    Page<User> findOrganizersByFilterAndSearch(
            @Param("roleName") RoleName roleName,
            @Param("keyword") String keyword,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    // Dem tong so Organizer
    @Query("""
                SELECT COUNT(DISTINCT u)
                FROM User u
                JOIN u.userRoles ur
                JOIN ur.role r
                WHERE r.roleName = :roleName
            """)
    long countAllOrganizers(@Param("roleName") RoleName roleName);

    // Top 5 organizer lau nam
    @Query("""
                SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName
                ORDER BY u.createdAt ASC
            """)
    List<User> findTop5OldestOrganizers(@Param("roleName")  RoleName roleName, Pageable pageable);

    // Top 5 organizer duoc tao trong ngay hom nay
    @Query("""
                SELECT u FROM User u JOIN u.userRoles ur JOIN ur.role r WHERE r.roleName = :roleName
                AND CAST(u.createdAt AS DATE) = CURRENT_DATE ORDER BY u.createdAt DESC
            """)
    List<User> findTop5NewOrganizersToday(@Param("roleName") RoleName roleName, Pageable pageable);

}










