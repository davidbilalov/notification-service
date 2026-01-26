package com.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@Schema(description = "Корневой ресурс API с ссылками для навигации")
public class ApiRootModel extends RepresentationModel<ApiRootModel> {

    @Schema(description = "Название сервиса", example = "Notification Service")
    private String serviceName;

    @Schema(description = "Версия API", example = "1.0.0")
    private String version;

    @Schema(description = "Описание", example = "API для отправки уведомлений")
    private String description;

    public ApiRootModel() {
    }

    public ApiRootModel(String serviceName, String version, String description) {
        this.serviceName = serviceName;
        this.version = version;
        this.description = description;
    }

}
