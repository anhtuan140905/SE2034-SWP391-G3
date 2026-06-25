package vn.edu.fpt.repository;

import org.apache.commons.lang3.ClassUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Event;
import vn.edu.fpt.model.OrganizerMember;
import vn.edu.fpt.model.UserRole;

import java.util.List;

@Repository
public interface OrganizerMemberRepository extends  JpaRepository<OrganizerMember, Long>{
    OrganizerMember findByUserRoleAndEvent(UserRole userRole, Event event);
    @Query("""
    SELECT o FROM OrganizerMember o
    WHERE o.event.id = :eventId
    AND (:roleId IS NULL OR o.userRole.role.id = :roleId)
    AND (:keyword = '' OR 
         LOWER(o.userRole.user.email) LIKE LOWER(CONCAT('%',:keyword,'%'))
      OR LOWER(CONCAT(o.userRole.user.lastName, o.userRole.user.firstName)) 
         LIKE LOWER(CONCAT('%',:keyword,'%')))
    """)
    Page<OrganizerMember> getOrganizerMemberByEventID(@Param("eventId") Long eventId,
                                                      @Param("keyword") String keyword,
                                                      @Param("roleId") Long roleId, Pageable pageable);

}
