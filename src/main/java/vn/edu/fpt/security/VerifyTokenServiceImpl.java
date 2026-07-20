package vn.edu.fpt.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.VerificationToken;
import vn.edu.fpt.model.constant.TokenType;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.repository.VerifyTokenRepository;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.VerifyTokenService;

import java.time.LocalDateTime;
import java.util.UUID;

@Service("VerifyTokenService")

public class VerifyTokenServiceImpl implements VerifyTokenService {
    private final VerifyTokenRepository verifyTokenRepository;
    private final UserRepository userRepository;
    public VerifyTokenServiceImpl(VerifyTokenRepository verifyTokenRepository,
                                  UserRepository userRepository) {
        this.verifyTokenRepository = verifyTokenRepository;
        this.userRepository = userRepository;
    }


    @Override
    public VerificationToken findByEmailAndTypeAndUsedFalse(String email, TokenType tokenType) {
        return this.verifyTokenRepository.findByEmailAndTypeAndUsedFalse(email, tokenType);
    }

    @Override
    public VerificationToken handleSaveVerificationToken(VerificationToken verificationToken) {
        return this.verifyTokenRepository.save(verificationToken);
    }
    @Override
    public String createActivationToken(String email) {
        String token = UUID.randomUUID().toString();
        VerificationToken vt = new VerificationToken();
        vt.setToken(token);
        vt.setEmail(email);
        vt.setType(TokenType.ORGANIZER_ACCOUNT_ACTIVATION);
        vt.setExpiryDate(LocalDateTime.now().plusMinutes(30));
        vt.setUsed(false);
        this.verifyTokenRepository.save(vt);
        return token;
    }

    @Override
    public boolean checkActiveAccount(String token, String email) {
        VerificationToken verificationToken = this.verifyTokenRepository.findByEmailAndTypeAndUsedFalse(email, TokenType.ORGANIZER_ACCOUNT_ACTIVATION);
        if (verificationToken == null) {
            return false;
        }
        else if(!verificationToken.getToken().equals(token)) {
            return false;
        } else if(LocalDateTime.now().isAfter(verificationToken.getExpiryDate())) {
            return false;
        }
        User user = this.userRepository.findByEmail(email);
        user.setIsActive(true);
        this.userRepository.save(user);
        verificationToken.setUsed(true);
        this.verifyTokenRepository.save(verificationToken);
        return true;
    }
}
