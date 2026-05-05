package org.smartdoc.aismartdoc.ai.dto;

import java.time.Instant;

public record AiHistoryEntryDto(
        String question,
        String answer,
        Instant createdAt
) {
}

