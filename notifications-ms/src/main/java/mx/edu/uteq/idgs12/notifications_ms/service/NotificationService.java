package mx.edu.uteq.idgs12.notifications_ms.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import mx.edu.uteq.idgs12.notifications_ms.dto.NotificationDTO;
import mx.edu.uteq.idgs12.notifications_ms.entity.Notification;
import mx.edu.uteq.idgs12.notifications_ms.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Envía un correo (HTML si se proporcionan variables de plantilla, o texto plano si no).
     */
    public Notification sendEmail(NotificationDTO request) {
        Notification notification = new Notification();
        notification.setRecipientEmail(request.getRecipientEmail());
        notification.setSubject(request.getSubject());
        notification.setMessage(request.getMessage());
        notification.setStatus("PENDING");

        try {
            if (request.getTemplateName() != null && !request.getTemplateName().isEmpty()) {
                // 📧 Envío HTML con plantilla Thymeleaf
                sendHtmlEmail(request);
            } else {
                // ✉️ Envío de texto plano (retrocompatible)
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(request.getRecipientEmail());
                message.setSubject(request.getSubject());
                message.setText(request.getMessage());
                mailSender.send(message);
            }

            notification.setStatus("SENT");
            notification.setSentAt(LocalDateTime.now());
        } catch (Exception e) {
            notification.setStatus("FAILED");
        }

        return notificationRepository.save(notification);
    }

    /**
     * Genera y envía un correo HTML con plantilla Thymeleaf.
     */
    private void sendHtmlEmail(NotificationDTO request) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

        Context context = new Context();
        context.setVariables(request.getTemplateVariables());

        String htmlBody = templateEngine.process(request.getTemplateName(), context);

        helper.setTo(request.getRecipientEmail());
        helper.setSubject(request.getSubject());
        helper.setText(htmlBody, true);

        mailSender.send(mimeMessage);
    }
}
