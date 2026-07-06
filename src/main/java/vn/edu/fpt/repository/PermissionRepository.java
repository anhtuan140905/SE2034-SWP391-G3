package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;
import vn.edu.fpt.model.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    @Query("select p.id from Permission p")
    List<Long> getPermissionsOfOrganizer();

    @Query("SELECT p FROM Permission p WHERE p.permissionKey LIKE 'STAFF%'")
    List<Permission> getStaffPermission();

    @Query("SELECT p FROM Permission p WHERE p.permissionKey LIKE 'STAFF%' OR  p.permissionKey LIKE 'MANAGER%'")
    List<Permission> getAllPermission();
}