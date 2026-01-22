package com.example.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserEventDto {
    private String operation;
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
