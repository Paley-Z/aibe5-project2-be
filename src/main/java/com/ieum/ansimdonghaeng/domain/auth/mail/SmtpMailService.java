package com.ieum.ansimdonghaeng.domain.auth.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@ConditionalOnProperty(name = "mail.enabled", havingValue = "true")
@RequiredArgsConstructor
public class SmtpMailService implements MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:${spring.mail.username:}}")
    private String from;

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setTo(to);
            if (StringUtils.hasText(from)) {
                helper.setFrom(from);
            }
            helper.setSubject("[안심동행] 비밀번호 재설정");
            helper.setText("아래 링크를 클릭하여 비밀번호를 재설정하세요 (10분 이내):\n\n" + resetLink, false);

            mailSender.send(message);
            log.info("Password reset email sent. to={}", to);
        } catch (MessagingException e) {
            throw new MailSendException("Failed to create password reset email message", e);
        }
    }
}
