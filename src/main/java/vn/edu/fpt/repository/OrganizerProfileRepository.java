package vn.edu.fpt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.fpt.model.OrganizerProfile;

public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile, Long> {

    boolean existsByTaxCode(String taxCode);

    OrganizerProfile findByUserId(Long userId);

//    @Query("SELECT o FROM OrganizerProfile o JOIN o.user u WHERE " +
//            "(:status IS NULL OR o.status = :status) AND " +
//            "(:keyword IS NULL OR :keyword = '' OR " +
//            "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
//            "LOWER(o.companyName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
//    Page<OrganizerProfile> searchAndFilterOrganizers(
//            @Param("keyword") String keyword,
//            @Param("status") String status,
//            Pageable pageable);
}
