package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Role;
import vn.edu.fpt.model.UserRole;
import vn.edu.fpt.model.constant.RoleName;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId")
    void deleteAllByUser_Id(Long userId);
    Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId);

    Optional<UserRole> findByUser_Id(Long userId);


    @Query("SELECT r FROM Role r WHERE r.roleName = :roleName")
    Optional<Role> findByRoleName(@Param("roleName") RoleName roleName);

}
