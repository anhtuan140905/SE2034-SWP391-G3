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
import java.util.Optional;

@Repository
public interface OrganizerMemberRepository extends  JpaRepository<OrganizerMember, Long>{
    long countByUserRole_Id(Long userRoleId);
    @Query(value = "delete organizer_members  where id = :staffId",nativeQuery = true)
    Void DeleteByIdStaff(@Param("staffId") Long staffId);

//    OrganizerMember findByUserRoleAndEvent(UserRole userRole, Event event);
    @Query("""
    SELECT o FROM OrganizerMember o WHERE o.event.id = :eventId
    AND (:roleId IS NULL OR o.userRole.role.id = :roleId)
    AND (:keyword = '' OR LOWER(o.userRole.user.email) LIKE LOWER(CONCAT('%',:keyword,'%'))
    OR LOWER(CONCAT(o.userRole.user.lastName, o.userRole.user.firstName)) 
    LIKE LOWER(CONCAT('%',:keyword,'%')))""")
    Page<OrganizerMember> getOrganizerMemberByEventID(@Param("eventId") Long eventId,
                                                      @Param("keyword") String keyword,
                                                      @Param("roleId") Long roleId, Pageable pageable);
    @Query("SELECT om FROM OrganizerMember om JOIN om.userRole ur where  ur.user.id = :userId and om.event.eventId = :eventId")
    OrganizerMember CheckPermission(@Param("userId") Long userId, @Param("eventId")  Long eventId);
    @Query("SELECT om FROM OrganizerMember om JOIN om.userRole ur where  ur.user.id = :userId and om.event.eventId = :eventId")
    Optional<OrganizerMember> findbyUserIdAndEventId(@Param("userId") Long userId, @Param("eventId")  Long eventId);
    @Query("SELECT om FROM OrganizerMember om  where  om.event.eventId = :eventId")
    List<OrganizerMember> findByEventId(Long eventId);

}
