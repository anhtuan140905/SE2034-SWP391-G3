package vn.edu.fpt.service.impl.resetPassword;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.edu.fpt.model.User;
import vn.edu.fpt.repository.UserRepository;

import java.time.Instant;
import java.util.Random;

@Service
public class PasswordResetService {

    private static final int OPT_LENGTH = 6;
    private static final int MAX_EXPIRY_SECONDS = 5 * 60;

    private static final String SESSION_OTP         = "reset_otp";
    private static final String SESSION_OTP_EMAIL   = "reset_email";
    private static final String SESSION_OTP_EXPIRY  = "reset_expiry";

    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public PasswordResetService(EmailService emailService, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    public void sendOtp(String email, HttpSession session) throws MessagingException {
        User user = this.userRepository.findByEmail(email);
        if(user == null) {
            throw new UsernameNotFoundException("Email không tồn tại!");
        }
        String otp = generateOtp();
        session.setAttribute(SESSION_OTP, otp);
        session.setAttribute(SESSION_OTP_EMAIL, email);
        session.setAttribute(SESSION_OTP_EXPIRY, Instant.now().plusSeconds(MAX_EXPIRY_SECONDS));
        this.emailService.sendOtpEmail(email, otp);
    }

    public boolean verifyOtp(String otp, HttpSession session) {
        String storedOtp = (String) session.getAttribute(SESSION_OTP);
        Instant expiry = (Instant) session.getAttribute(SESSION_OTP_EXPIRY);

        if(storedOtp == null || expiry == null) {
            return false;
        }
        if(Instant.now().isAfter(expiry)) {
            return false;
        }
        return storedOtp.equals(otp);
    }

    public boolean resetPassword(String newPassword, HttpSession session) {
        String email1 = (String) session.getAttribute(SESSION_OTP_EMAIL);
        if(email1 == null) {
            return false;
        }
        User user = this.userRepository.findByEmail(email1);
        if(user == null) {
            return false;
        }

        user.setPasswordHash(this.passwordEncoder.encode(newPassword));
        this.userRepository.save(user);
        session.removeAttribute(SESSION_OTP);
        session.removeAttribute(SESSION_OTP_EMAIL);
        session.removeAttribute(SESSION_OTP_EXPIRY);
        return true;
    }
}
