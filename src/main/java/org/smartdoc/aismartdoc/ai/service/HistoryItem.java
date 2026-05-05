package org.smartdoc.aismartdoc.ai.service;

import java.time.Instant;

record HistoryItem(String question, String answer, Instant createdAt) {
}

