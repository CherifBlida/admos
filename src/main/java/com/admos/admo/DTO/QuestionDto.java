package com.admos.admo.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public class QuestionDto {
    @Valid
    @Size(min = 2, message = "At least 2 answers required")
    private List<AnswerDto> answers;
}
