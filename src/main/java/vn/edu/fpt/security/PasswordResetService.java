package vn.edu.fpt.security;

import jakarta.mail.MessagingException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.fpt.exception.ServiceValidationException;
import vn.edu.fpt.model.User;
import vn.edu.fpt.model.VerificationToken;
import vn.edu.fpt.model.constant.TokenType;
import vn.edu.fpt.repository.UserRepository;
import vn.edu.fpt.repository.VerifyTokenRepository;
import vn.edu.fpt.service.impl.EmailService;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PasswordResetService {

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerifyTokenRepository verifyTokenRepository;

    private static final int OPT_LENGTH = 6;
    private static final int MAX_EXPIRY_SECONDS = 60*5;

    private static final String SESSION_OTP         = "reset_otp";
    private static final String SESSION_OTP_EMAIL   = "reset_email";
    private static final String SESSION_OTP_EXPIRY  = "reset_expiry";


    public PasswordResetService( EmailService emailService, PasswordEncoder passwordEncoder, UserRepository userRepository, VerifyTokenRepository verifyTokenRepository) {
        this.verifyTokenRepository = verifyTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    public VerificationToken sendOtp(String email) throws MessagingException {
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Email không tồn tại!");
        }
        this.verifyTokenRepository.deleteByEmailAndType(email, TokenType.FORGOT_PASSWORD_OTP);

        String otp = generateOtp();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(otp);
        verificationToken.setEmail(email);
        verificationToken.setExpiryDate(LocalDateTime.now().plusSeconds(MAX_EXPIRY_SECONDS));
        verificationToken.setType(TokenType.FORGOT_PASSWORD_OTP);
        this.verifyTokenRepository.save(verificationToken);
        this.emailService.sendOtpEmail(email, otp);
        return verificationToken;
    }

    @Transactional
    public boolean verifyOtp(String otp, VerificationToken verificationToken) {
        String storedOtp = verificationToken.getToken();
        LocalDateTime expiry = verificationToken.getExpiryDate();

        if (storedOtp == null || expiry == null) return false;
        if (LocalDateTime.now().isAfter(expiry)) return false;
        if (!storedOtp.equals(otp)) return false;

        verificationToken.setUsed(true);
        this.verifyTokenRepository.save(verificationToken);
        return true;
    }

    @Transactional
    public boolean resetPassword(String newPassword, String confirmPassword, String email) {
        validatePasswordStrength(newPassword, confirmPassword);
        if(email == null) {
            return false;
        }
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            return false;
        }

        user.setPasswordHash(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        this.verifyTokenRepository.deleteByEmail(email);
        return true;
    }

    private static final int MIN_PASSWORD_LENGTH = 8;

    private void validatePasswordStrength(String newPassword, String confirmPassword) {
        ServiceValidationException ex = new ServiceValidationException();

        if (newPassword == null || newPassword.isBlank()) {
            ex.add("newPassword", "Mật khẩu không được để trống!");
        } else if (newPassword.length() < MIN_PASSWORD_LENGTH) {
            ex.add("newPassword", "Mật khẩu phải có ít nhất 8 ký tự.");
        }

        if (newPassword != null && !newPassword.equals(confirmPassword)) {
            ex.add("confirmPassword", "Mật khẩu xác nhận không khớp.");
        }

        if (ex.hasErrors()) {
            throw ex;
        }
    }
}
