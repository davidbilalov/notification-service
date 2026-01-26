package com.example.controller;

import com.example.dto.ApiRootModel;
import com.example.dto.NotificationResponse;
import com.example.dto.SendEmailDto;
import com.example.service.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
@Tag(name = "Notification API", description = "API для управления уведомлениями")
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping
    @Operation(summary = "Корневой endpoint API", description = "Возвращает информацию об API и ссылки для навигации")
    @ApiResponse(responseCode = "200", description = "Успешный ответ",
            content = @Content(schema = @Schema(implementation = ApiRootModel.class)))
    public ResponseEntity<ApiRootModel> root() {
        ApiRootModel rootModel = new ApiRootModel(
                "Notification Service",
                "1.0.0",
                "API для отправки уведомлений по электронной почте"
        );

        // Добавляем ссылки для навигации
        rootModel.add(linkTo(methodOn(NotificationController.class).root()).withSelfRel());
        rootModel.add(linkTo(methodOn(NotificationController.class).sendEmail(null))
                .withRel("send-email")
                .withTitle("Отправить email-уведомление"));

        return ResponseEntity.ok(rootModel);
    }

    @PostMapping("/notifications/email")
    @Operation(summary = "Отправить email-уведомление", 
               description = "Отправляет email-уведомление указанному получателю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email успешно отправлен",
                    content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
            @ApiResponse(responseCode = "500", description = "Ошибка при отправке email")
    })
    public ResponseEntity<NotificationResponse> sendEmail(@Valid @RequestBody SendEmailDto sendEmailDto) {
        emailService.sendEmail(
                sendEmailDto.getEmail(),
                sendEmailDto.getSubject(),
                sendEmailDto.getMessage()
        );

        NotificationResponse response = new NotificationResponse(
                "SUCCESS",
                sendEmailDto.getEmail(),
                sendEmailDto.getSubject(),
                "Email успешно отправлен"
        );

        // Добавляем HATEOAS ссылки
        response.add(linkTo(methodOn(NotificationController.class).sendEmail(null))
                .withSelfRel()
                .withTitle("Отправить email"));
        response.add(linkTo(methodOn(NotificationController.class).root())
                .withRel(IanaLinkRelations.COLLECTION)
                .withTitle("Главная страница API"));

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
