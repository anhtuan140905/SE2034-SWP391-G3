package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.OrganizerProfile;

@Repository
public interface OrganizerProfileRepository extends JpaRepository<OrganizerProfile,Long> {
    boolean existsByTaxCode(String taxCode);
}
