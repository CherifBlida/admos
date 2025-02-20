package com.admos.admo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public class SubjectDTO {
    // For creating/updating subjects
    public record SubjectRequest(
            @NotBlank
            @Size(max = 100) String name
    ) {}

    // For API responses
    public record SubjectResponse(
            Long id,
            String name,
            LocalDateTime createdAt,
            List<ChapterSimpleResponse> chapters
    ) {}

    // Simplified chapter response
    public record ChapterSimpleResponse(Long id, String name) {}
}
