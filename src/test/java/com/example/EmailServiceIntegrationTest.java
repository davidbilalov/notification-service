package com.example;

import com.example.service.EmailService;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class EmailServiceIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private EmailService emailService;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mail.host", () -> "localhost");
        registry.add("spring.mail.port", () -> greenMail.getSmtp().getPort());
        registry.add("spring.mail.username", () -> "test");
        registry.add("spring.mail.password", () -> "password");
        registry.add("spring.mail.properties.mail.smtp.auth", () -> "false");
    }

    @BeforeEach
    void setUp() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    void sendUserCreatedEmail_ShouldSendEmail() throws Exception {
        String toEmail = "user@example.com";

        emailService.sendUserCreatedEmail(toEmail);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        assertEquals("Добро пожаловать!", message.getSubject());
        assertEquals(toEmail, message.getAllRecipients()[0].toString());

        String content = (String) message.getContent();
        assertTrue(content.contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."),
                "Email should contain creation message");
    }

    @Test
    void sendUserDeletedEmail_ShouldSendEmail() throws Exception {
        String toEmail = "user@example.com";

        emailService.sendUserDeletedEmail(toEmail);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        assertEquals("Удаление аккаунта", message.getSubject());
        assertEquals(toEmail, message.getAllRecipients()[0].toString());

        String content = (String) message.getContent();
        assertTrue(content.contains("Здравствуйте! Ваш аккаунт был удалён."),
                "Email should contain deletion message");
    }

    @Test
    void sendEmail_ShouldSendCustomEmail() throws Exception {
        String toEmail = "user@example.com";
        String subject = "Test Subject";
        String messageText = "Test message content";

        emailService.sendEmail(toEmail, subject, messageText);

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        assertEquals(subject, message.getSubject());
        assertEquals(toEmail, message.getAllRecipients()[0].toString());

        String content = (String) message.getContent();
        assertEquals(messageText, content.trim());
    }
}
