package org.smartdoc.aismartdoc.ai.service;

import org.smartdoc.aismartdoc.ai.client.FastApiClient;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiCitation;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiHealthResponse;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiIngestRequest;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiIngestResponse;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiQueryRequest;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiQueryResponse;
import org.smartdoc.aismartdoc.ai.dto.AiHistoryEntryDto;
import org.smartdoc.aismartdoc.ai.dto.AiHistoryResponseDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionRequestDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionResponseDto;
import org.smartdoc.aismartdoc.ai.dto.CitationDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentRequestDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentResponseDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.List;

@Service
public class AiQaService {

    private final FastApiClient fastApiClient;
    private final InMemoryAiHistoryStore historyStore;

    public AiQaService(FastApiClient fastApiClient, InMemoryAiHistoryStore historyStore) {
        this.fastApiClient = fastApiClient;
        this.historyStore = historyStore;
    }

    public AskQuestionResponseDto askQuestion(AskQuestionRequestDto requestDto) {
        FastApiQueryResponse fastApiResponse = fastApiClient.query(new FastApiQueryRequest(
                requestDto.userId(),
                requestDto.question(),
                requestDto.documentIds()
        ));

        String answer = fastApiResponse != null && fastApiResponse.answer() != null
                ? fastApiResponse.answer()
                : "No answer was returned by AI service.";

        Instant now = Instant.now();
        historyStore.append(requestDto.userId(), new HistoryItem(requestDto.question(), answer, now));

        List<CitationDto> citations = fastApiResponse == null || fastApiResponse.citations() == null
                ? List.of()
                : fastApiResponse.citations().stream().map(this::toCitationDto).toList();

        return new AskQuestionResponseDto(answer, citations, now);
    }

    public AiHistoryResponseDto getHistory(String userId, int page, int size) {
        if (page < 0) {
            throw new IllegalArgumentException("page must be >= 0");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("size must be > 0");
        }

        List<HistoryItem> entries = historyStore.list(userId);
        int fromIndex = page * size;
        if (fromIndex >= entries.size()) {
            return new AiHistoryResponseDto(List.of(), page, size, entries.size());
        }

        int toIndex = Math.min(fromIndex + size, entries.size());
        List<AiHistoryEntryDto> items = entries.subList(fromIndex, toIndex).stream()
                .map(entry -> new AiHistoryEntryDto(entry.question(), entry.answer(), entry.createdAt()))
                .toList();

        return new AiHistoryResponseDto(items, page, size, entries.size());
    }

    public Map<String, String> health() {
        FastApiHealthResponse response = fastApiClient.health();
        String providerStatus = response != null && response.status() != null ? response.status() : "unknown";
        return Map.of(
                "service", "spring-api",
                "provider", "fastapi",
                "providerStatus", providerStatus
        );
    }

    public IngestDocumentResponseDto ingestDocument(IngestDocumentRequestDto requestDto) {
        FastApiIngestResponse fastApiResponse = fastApiClient.ingest(new FastApiIngestRequest(
                requestDto.userId(),
                requestDto.documentId(),
                requestDto.content()
        ));

        String status = fastApiResponse != null && fastApiResponse.status() != null
                ? fastApiResponse.status()
                : "ingested";
        int chunksCreated = fastApiResponse != null ? fastApiResponse.chunksCreated() : 0;

        return new IngestDocumentResponseDto(status, chunksCreated, Instant.now());
    }

    private CitationDto toCitationDto(FastApiCitation citation) {
        return new CitationDto(
                citation.documentId(),
                citation.chunkIndex(),
                citation.page(),
                citation.snippet()
        );
    }
}



