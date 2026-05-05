package org.smartdoc.aismartdoc.ai.dto;

import java.util.List;

public record AiHistoryResponseDto(
        List<AiHistoryEntryDto> items,
        int page,
        int size,
        long totalItems
) {
}

