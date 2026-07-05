package vn.edu.fpt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.fpt.model.VerificationToken;
import vn.edu.fpt.model.constant.TokenType;

@Repository
public interface VerifyTokenRepository extends JpaRepository<VerificationToken, Long> {
    void deleteByEmail(String email);

    VerificationToken findByEmailAndTypeAndUsedFalse(String email, TokenType tokenType);

    void deleteByEmailAndType(String email, TokenType type);
}
