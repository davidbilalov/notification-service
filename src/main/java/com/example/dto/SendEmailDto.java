package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для отправки email-уведомления")
public class SendEmailDto {

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email адрес получателя", example = "davidbilalov1994@gmail.com", required = true)
    private String email;

    @NotBlank(message = "Subject is required")
    @Schema(description = "Тема письма", example = "Добро пожаловать!", required = true)
    private String subject;

    @NotBlank(message = "Message is required")
    @Schema(description = "Текст сообщения", example = "Здравствуйте! Ваш аккаунт был успешно создан.", required = true)
    private String message;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

