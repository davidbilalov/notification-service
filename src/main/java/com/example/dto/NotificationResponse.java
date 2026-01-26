package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

@Data
@Schema(description = "Ответ об успешной отправке уведомления")
public class NotificationResponse extends RepresentationModel<NotificationResponse> {

    @Schema(description = "Статус отправки", example = "SUCCESS")
    private String status;

    @Schema(description = "Email адрес получателя", example = "davidbilalov1994@gmail.com")
    private String email;

    @Schema(description = "Тема письма", example = "Добро пожаловать!")
    private String subject;

    @Schema(description = "Время отправки")
    private LocalDateTime sentAt;

    @Schema(description = "Сообщение", example = "Email успешно отправлен")
    private String message;

    public NotificationResponse() {
        this.sentAt = LocalDateTime.now();
    }

    public NotificationResponse(String status, String email, String subject, String message) {
        this.status = status;
        this.email = email;
        this.subject = subject;
        this.message = message;
        this.sentAt = LocalDateTime.now();
    }

}
