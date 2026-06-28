package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
    @Query("select p.id from Permission p")
    List<Long> getALLIdPermission();
}
