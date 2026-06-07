package vn.edu.fpt.service.impl.security;

import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.fpt.common.error.CheckDuplicateException;
import vn.edu.fpt.model.User;
import vn.edu.fpt.modelview.request.auth.RegisterOrgDTO;
import vn.edu.fpt.service.UserService;
import vn.edu.fpt.service.VerifyTokenService;
import vn.edu.fpt.service.impl.EmailService;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final UserService userService;
    private final EmailService emailService;
    private final VerifyTokenService verifyTokenService;

    public void registerOrganizer(RegisterOrgDTO dto) throws CheckDuplicateException, MessagingException {
        // 1. Tạo user
        User u = userService.handleCreateOrganizer(dto);

        // 2. Tạo token
        String token = verifyTokenService.createActivationToken(u.getEmail());

        // 3. Gửi email
        String activationLink = "http://localhost:8081/auth/activate?token=" + token + "&email=" + u.getEmail();
        String fullName = dto.getFirstName() + " " + dto.getMiddleName() + " " + dto.getLastName();
        emailService.sendActiveOrganizerAccount(dto.getUsername(), fullName, dto.getCompanyName(), activationLink);
    }

}
