package org.smartdoc.aismartdoc.ai.service;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryAiHistoryStore {

    private final Map<String, List<HistoryItem>> userHistory = new ConcurrentHashMap<>();

    public void append(String userId, HistoryItem item) {
        userHistory.computeIfAbsent(userId, ignored -> Collections.synchronizedList(new ArrayList<>())).add(item);
    }

    public List<HistoryItem> list(String userId) {
        List<HistoryItem> snapshot = new ArrayList<>(userHistory.getOrDefault(userId, List.of()));
        snapshot.sort(Comparator.comparing(HistoryItem::createdAt).reversed());
        return snapshot;
    }
}

