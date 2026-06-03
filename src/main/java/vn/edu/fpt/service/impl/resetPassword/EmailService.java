package vn.edu.fpt.service.impl.resetPassword;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendOtpEmail(String toEmail,
                             String otp) throws MessagingException {
        // Truyền biến vào template
        Context context = new Context();
        context.setVariable("otp", otp);
        // Render template → String HTML
        String htmlContent = templateEngine.process("mail/otpMail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("EventHub — Mã xác nhận đặt lại mật khẩu");
        helper.setText(htmlContent, true); // true = isHtml
        mailSender.send(message);
    }
}