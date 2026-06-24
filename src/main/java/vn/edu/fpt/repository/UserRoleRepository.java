package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.UserRole;
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole,Long> {
    @Modifying
    @Query("DELETE FROM UserRole ur WHERE ur.user.id = :userId")
    void deleteAllByUser_Id(Long userId);
    UserRole findByUserIdAndRoleId (Long userid, Long roleid);
}
