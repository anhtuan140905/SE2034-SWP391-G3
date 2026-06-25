package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.Permission;
@Repository
public interface PermissionRepository extends JpaRepository<Permission,Long> {
}
