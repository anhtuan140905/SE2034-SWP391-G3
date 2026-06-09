package vn.edu.fpt.service;

import vn.edu.fpt.model.VerificationToken;
import vn.edu.fpt.model.constant.TokenType;

public interface VerifyTokenService {
    VerificationToken findByEmailAndTypeAndUsedFalse(String email, TokenType tokenType);
    VerificationToken handleSaveVerificationToken(VerificationToken verificationToken);
    String createActivationToken(String email);
    boolean checkActiveAccount(String token, String email);
}
