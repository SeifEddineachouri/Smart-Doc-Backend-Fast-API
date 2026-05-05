package org.smartdoc.aismartdoc.ai;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.smartdoc.aismartdoc.ai.dto.AiHistoryResponseDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionRequestDto;
import org.smartdoc.aismartdoc.ai.dto.AskQuestionResponseDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentRequestDto;
import org.smartdoc.aismartdoc.ai.dto.IngestDocumentResponseDto;
import org.smartdoc.aismartdoc.ai.service.AiQaService;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/v1/ai")
public class AiController {

    private final AiQaService aiQaService;

    public AiController(AiQaService aiQaService) {
        this.aiQaService = aiQaService;
    }

    @PostMapping("/questions")
    public AskQuestionResponseDto askQuestion(@Valid @RequestBody AskQuestionRequestDto request) {
        try {
            return aiQaService.askQuestion(request);
        } catch (AiServiceUnavailableException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI service is currently unavailable. Please retry shortly.",
                    ex
            );
        }
    }

    @GetMapping("/history")
    public AiHistoryResponseDto getHistory(
            @RequestParam @NotBlank String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            return aiQaService.getHistory(userId, page, size);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
        }
    }

    @PostMapping("/ingest")
    public IngestDocumentResponseDto ingest(@Valid @RequestBody IngestDocumentRequestDto request) {
        try {
            return aiQaService.ingestDocument(request);
        } catch (AiServiceUnavailableException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI ingestion service is currently unavailable. Please retry shortly.",
                    ex
            );
        }
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        try {
            return aiQaService.health();
        } catch (AiServiceUnavailableException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "AI provider health endpoint is unavailable.",
                    ex
            );
        }
    }
}





