package com.example;


import com.example.dto.SendEmailDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import jakarta.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NotificationControllerIntegrationTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    void sendEmail_ShouldSendEmailViaAPI() throws Exception {
        SendEmailDto sendEmailDto = new SendEmailDto(
                "user@example.com",
                "Test Subject",
                "Test message content"
        );

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmailDto)))
                .andExpect(status().isOk());

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(1, messages.length, "Should receive exactly one email");

        MimeMessage message = messages[0];
        assertEquals("Test Subject", message.getSubject());
        assertEquals("user@example.com", message.getAllRecipients()[0].toString());

        String content = (String) message.getContent();
        assertEquals("Test message content", content.trim());
    }

    @Test
    void sendEmail_WithInvalidEmail_ShouldReturnBadRequest() throws Exception {
        SendEmailDto sendEmailDto = new SendEmailDto(
                "invalid-email",
                "Test Subject",
                "Test message content"
        );

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmailDto)))
                .andExpect(status().isBadRequest());

        MimeMessage[] messages = greenMail.getReceivedMessages();
        assertEquals(0, messages.length, "Should not receive any email");
    }

    @Test
    void sendEmail_WithMissingFields_ShouldReturnBadRequest() throws Exception {
        SendEmailDto sendEmailDto = new SendEmailDto(
                "user@example.com",
                "",
                ""
        );

        mockMvc.perform(post("/api/notifications/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sendEmailDto)))
                .andExpect(status().isBadRequest());
    }
}
