package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);
}
