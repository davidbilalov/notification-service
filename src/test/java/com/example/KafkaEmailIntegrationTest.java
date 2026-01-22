package com.example;

import com.example.dto.UserEventDto;
import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.mail.internet.MimeMessage;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@Testcontainers
class KafkaEmailIntegrationTest {

    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"));

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("test", "password"))
            .withPerMethodLifecycle(false);

    @Autowired
    private KafkaTemplate<String, UserEventDto> kafkaTemplate;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
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
    void consumeCreateEvent_ShouldSendUserCreatedEmail() throws Exception {
        String email = "create@example.com";
        UserEventDto event = new UserEventDto("CREATE", email);

        kafkaTemplate.send("user-events", email, event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            assertEquals(1, messages.length, "Should receive exactly one email");

            MimeMessage message = messages[0];
            assertEquals("Добро пожаловать!", message.getSubject());
            assertEquals(email, message.getAllRecipients()[0].toString());

            String content = (String) message.getContent();
            assertTrue(content.contains("Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."),
                    "Email should contain creation message");
        });
    }

    @Test
    void consumeDeleteEvent_ShouldSendUserDeletedEmail() throws Exception {
        String email = "delete@example.com";
        UserEventDto event = new UserEventDto("DELETE", email);

        kafkaTemplate.send("user-events", email, event);

        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            MimeMessage[] messages = greenMail.getReceivedMessages();
            assertEquals(1, messages.length, "Should receive exactly one email");

            MimeMessage message = messages[0];
            assertEquals("Удаление аккаунта", message.getSubject());
            assertEquals(email, message.getAllRecipients()[0].toString());

            String content = (String) message.getContent();
            assertTrue(content.contains("Здравствуйте! Ваш аккаунт был удалён."),
                    "Email should contain deletion message");
        });
    }
}
