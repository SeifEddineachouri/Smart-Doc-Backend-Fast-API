package org.smartdoc.aismartdoc.ai.dto;

import java.time.Instant;
import java.util.List;

public record AskQuestionResponseDto(
        String answer,
        List<CitationDto> citations,
        Instant createdAt
) {
}

