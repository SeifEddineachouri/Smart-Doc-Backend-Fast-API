package org.smartdoc.aismartdoc.ai.dto;

public record CitationDto(
        String documentId,
        Integer chunkIndex,
        Integer page,
        String snippet
) {
}

