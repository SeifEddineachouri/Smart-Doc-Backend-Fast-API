package org.smartdoc.aismartdoc.ai.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record AskQuestionRequestDto(
        @NotBlank String userId,
        @NotBlank String question,
        List<String> documentIds
) {
}

