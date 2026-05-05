package org.smartdoc.aismartdoc.ai.dto;

import java.time.Instant;

public record IngestDocumentResponseDto(
        String status,
        int chunksCreated,
        Instant createdAt
) {
}

