package org.smartdoc.aismartdoc.ai.client.dto;

import java.util.List;

public record FastApiQueryResponse(
        String answer,
        List<FastApiCitation> citations
) {
}

