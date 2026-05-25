package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);
}
