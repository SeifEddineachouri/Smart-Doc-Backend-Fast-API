package org.smartdoc.aismartdoc.ai.client.dto;

public record FastApiIngestResponse(
        String status,
        int chunksCreated
) {
}

