package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.constant.RoleName;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(RoleName roleName);
    @Query("SELECT r FROM Role r WHERE r.roleName IN ('ROLE_MANAGER', 'ROLE_STAFF')")
    List<Role> findRoleMemberOfOrganizer();
}
