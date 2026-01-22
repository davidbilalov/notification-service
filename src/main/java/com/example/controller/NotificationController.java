package com.example.controller;

import com.example.dto.SendEmailDto;
import com.example.service.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<Void> sendEmail(@Valid @RequestBody SendEmailDto sendEmailDto) {
        emailService.sendEmail(
                sendEmailDto.getEmail(),
                sendEmailDto.getSubject(),
                sendEmailDto.getMessage()
        );
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
