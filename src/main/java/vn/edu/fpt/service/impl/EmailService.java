package vn.edu.fpt.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
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

    @Async
    public void sendOtpEmail(String toEmail,
                             String otp) throws MessagingException {
        // Truyền biến vào template
        Context context = new Context();
        context.setVariable("otp", otp);
        // Render template → String HTML
        String htmlContent = templateEngine.process("mail/auth/otpMail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("EventHub — Mã xác nhận đặt lại mật khẩu");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendActiveOrganizerAccount(String toEmail, String organizerName, String companyName, String activationLink) throws MessagingException {
        Context context = new Context();
        context.setVariable("organizerName", organizerName);
        context.setVariable("companyName", companyName);
        context.setVariable("activationLink", activationLink);

        String htmlContent = templateEngine.process("mail/auth/activeOrganizerEmail", context);
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("EventHub - Kích hoạt tài khoản đăng kí tổ chức");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
