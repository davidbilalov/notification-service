package com.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO для события пользователя из Kafka")
public class UserEventDto {
    @Schema(description = "Тип операции", example = "CREATE", allowableValues = {"CREATE", "DELETE"})
    private String operation;

    @Schema(description = "Email адрес пользователя", example = "davidbilalov1994@gmail.com")
    private String email;

    @JsonCreator
    public UserEventDto(@JsonProperty("operation") String operation,
                        @JsonProperty("email") String email) {
        this.operation = operation;
        this.email = email;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
