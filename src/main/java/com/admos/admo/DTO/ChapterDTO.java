package com.admos.admo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class ChapterDTO {

    public record ChapterRequest(
            @NotNull(message = "Subject ID cannot be null")
            Long subjectId,

            @NotBlank(message = "Chapter name cannot be blank")
            @Size(max = 100, message = "Chapter name cannot exceed 100 characters")
            String name
    ) {}

    public record ChapterResponse(
            Long id,
            String name,
            LocalDateTime createdAt,
            SubjectSimpleResponse subject,
            List<QuestionSimpleResponse> questions
    ) {}

    public record SubjectSimpleResponse(
            Long id,
            String name
    ) {}

    public record QuestionSimpleResponse(
            Long id,
            String text
    ) {}
}