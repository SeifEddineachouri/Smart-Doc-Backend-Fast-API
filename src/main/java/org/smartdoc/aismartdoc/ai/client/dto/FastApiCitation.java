package org.smartdoc.aismartdoc.ai.client.dto;

public record FastApiCitation(
        String documentId,
        Integer chunkIndex,
        Integer page,
        String snippet
) {
}

