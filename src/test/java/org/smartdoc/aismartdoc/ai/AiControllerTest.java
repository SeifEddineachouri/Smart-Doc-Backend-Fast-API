package org.smartdoc.aismartdoc.ai;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.smartdoc.aismartdoc.ai.dto.AiHistoryResponseDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionRequestDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionResponseDto;
import org.smartdoc.aismartdoc.ai.dto.CitationDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentRequestDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentResponseDto;
import org.smartdoc.aismartdoc.ai.service.AiQaService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class AiControllerTest {

    @Mock
    private AiQaService aiQaService;

    @InjectMocks
    private AiController controller;

    @Test
    void shouldAskQuestion() {
        AskQuestionResponseDto response = new AskQuestionResponseDto(
                "AI answer",
                List.of(new CitationDto("doc-1", 0, 1, "snippet")),
                Instant.parse("2026-04-18T10:00:00Z")
        );
        when(aiQaService.askQuestion(any())).thenReturn(response);

        AskQuestionRequestDto request = new AskQuestionRequestDto("user-1", "What is inside?", List.of("doc-1"));

        AskQuestionResponseDto actual = controller.askQuestion(request);

        assertEquals("AI answer", actual.answer());
        assertEquals("doc-1", actual.citations().get(0).documentId());
    }

    @Test
    void shouldGetHistory() {
        when(aiQaService.getHistory("user-1", 0, 20)).thenReturn(new AiHistoryResponseDto(List.of(), 0, 20, 0));

        AiHistoryResponseDto actual = controller.getHistory("user-1", 0, 20);

        assertEquals(0, actual.totalItems());
    }

    @Test
    void shouldReportHealth() {
        when(aiQaService.health()).thenReturn(Map.of("providerStatus", "ok"));

        Map<String, String> actual = controller.health();

        assertEquals("ok", actual.get("providerStatus"));
    }

    @Test
    void shouldIngestDocument() {
        IngestDocumentRequestDto request = new IngestDocumentRequestDto(
                "user-1",
                "doc-1",
                "Document content for chunking"
        );
        IngestDocumentResponseDto response = new IngestDocumentResponseDto(
                "ingested",
                1,
                Instant.parse("2026-04-18T10:00:00Z")
        );
        when(aiQaService.ingestDocument(any())).thenReturn(response);

        IngestDocumentResponseDto actual = controller.ingest(request);

        assertEquals("ingested", actual.status());
        assertEquals(1, actual.chunksCreated());
    }

    @Test
    void shouldMapUnavailableServiceToStatusExceptionOnQuestion() {
        AskQuestionRequestDto request = new AskQuestionRequestDto("user-1", "What is inside?", List.of("doc-1"));
        when(aiQaService.askQuestion(any())).thenThrow(new AiServiceUnavailableException("down", new RuntimeException()));

        assertThrows(org.springframework.web.server.ResponseStatusException.class, () -> controller.askQuestion(request));
    }
}



