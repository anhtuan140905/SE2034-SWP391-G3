package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.constant.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Role findByRoleName(RoleName roleName);
}
