package org.smartdoc.aismartdoc.ai.client.dto;

import java.util.List;

public record FastApiQueryRequest(
        String userId,
        String question,
        List<String> documentIds
) {
}

