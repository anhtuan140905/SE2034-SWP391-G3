package vn.edu.fpt.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import vn.edu.fpt.model.Order;
import vn.edu.fpt.model.Ticket;
import vn.edu.fpt.modelview.response.booking.OrderEmailDTO;
import vn.edu.fpt.modelview.response.booking.TicketEmailDTO;

import java.util.List;

@Slf4j
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

    @Async
    public void sendEventApprovalEmail(String toEmail, String organizerName, String eventTitle, String reviewMessage) throws MessagingException {
        Context context = new Context();
        context.setVariable("organizerName", organizerName);
        context.setVariable("eventTitle", eventTitle);
        context.setVariable("reviewMessage", reviewMessage != null ? reviewMessage.trim() : "");

        String htmlContent = templateEngine.process("mail/event/approvalMail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("EventHub — Sự kiện của bạn đã ĐƯỢC PHÊ DUYỆT 🎉");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendDeactivationEmail(String toEmail, String organizerName, String eventTitle, String reason) throws MessagingException {
        Context context = new Context();
        context.setVariable("organizerName", organizerName);
        context.setVariable("eventTitle", eventTitle);
        context.setVariable("reason", reason != null ? reason.trim() : "");

        String htmlContent = templateEngine.process("mail/event/DeactivationMail", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("EventHub — Thông báo đóng sự kiện ⚠️");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendOrganizerDeactivateEmail (String toEmail, String organizerName, String reason) throws MessagingException {
        Context context = new Context();
        context.setVariable("organizerName", organizerName);
        context.setVariable("reason", reason != null ? reason.trim() : "");

        String htmlContent = templateEngine.process("mail/organizer/OrganizerDeactivate", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("[EventHub] Tài khoản đối tác của bạn đã bị khóa");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendOrganizerActivationEmail(String toEmail, String organizerName) throws MessagingException {
        Context context = new Context();
        context.setVariable("organizerName", organizerName);

        String htmlContent = templateEngine.process("mail/organizer/OrganizerActivate", context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(toEmail);
        helper.setSubject("[EventHub] Tài khoản đối tác của bạn đã được mở khóa");
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }

    @Async
    public void sendTicketConfirmationEmail(OrderEmailDTO order, List<TicketEmailDTO> tickets) {
        try {
            Context context = new Context();
            context.setVariable("order", order);
            context.setVariable("tickets", tickets);
            context.setVariable("myTicketsUrl", "http://localhost:8081/my-tickets");
            String htmlContent = templateEngine.process("mail/event/TicketInformation", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(order.getUserEmail());
            helper.setSubject("[EventHub] xác nhận vé - Đơn hàng: " + order.getOrderId());
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("Gửi email thất bại cho order {}", order.getOrderId(), e);
        }
    }

    @Async
    public void sendOrganizerCredentialsEmail(String toEmail, String fullName, String username, String password) {
        try {
            Context context = new Context();
            context.setVariable("fullName", fullName);
            context.setVariable("username", username);
            context.setVariable("password", password);

            String htmlContent = templateEngine.process("mail/auth/organizerCredentialsMail", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("EventHub — Thông tin tài khoản nhà tổ chức của bạn");
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Gửi email thông tin tài khoản thất bại cho email {}", toEmail, e);
        }
    }

}
