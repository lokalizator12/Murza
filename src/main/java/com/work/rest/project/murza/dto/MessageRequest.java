package com.work.rest.project.murza.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MessageRequest {
    @NotNull @Positive
    private Long receiverId;
    @NotBlank @Size(max = 2000)
    private String content;
}
