package org.smartdoc.aismartdoc.ai.client.dto;

public record FastApiIngestRequest(
        String userId,
        String documentId,
        String content
) {
}

