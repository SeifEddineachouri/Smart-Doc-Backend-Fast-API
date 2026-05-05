package org.smartdoc.aismartdoc.ai.dto;

import jakarta.validation.constraints.NotBlank;

public record IngestDocumentRequestDto(
        @NotBlank String userId,
        @NotBlank String documentId,
        @NotBlank String content
) {
}

