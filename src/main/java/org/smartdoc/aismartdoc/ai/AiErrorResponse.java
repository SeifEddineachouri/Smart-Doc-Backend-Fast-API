package org.smartdoc.aismartdoc.ai;

import java.time.Instant;

public record AiErrorResponse(
        String code,
        String message,
        Instant timestamp
) {
}

