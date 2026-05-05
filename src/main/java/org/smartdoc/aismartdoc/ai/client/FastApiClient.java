package org.smartdoc.aismartdoc.ai.client;

import org.smartdoc.aismartdoc.ai.AiServiceUnavailableException;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiHealthResponse;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiIngestRequest;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiIngestResponse;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiQueryRequest;
import org.smartdoc.aismartdoc.ai.client.dto.FastApiQueryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class FastApiClient {

    private final RestClient aiRestClient;

    public FastApiClient(RestClient aiRestClient) {
        this.aiRestClient = aiRestClient;
    }

    public FastApiQueryResponse query(FastApiQueryRequest request) {
        try {
            return aiRestClient.post()
                    .uri("/query")
                    .body(request)
                    .retrieve()
                    .body(FastApiQueryResponse.class);
        } catch (RestClientException ex) {
            throw new AiServiceUnavailableException("Failed to call FastAPI /query endpoint", ex);
        }
    }

    public FastApiHealthResponse health() {
        try {
            return aiRestClient.get()
                    .uri("/health")
                    .retrieve()
                    .body(FastApiHealthResponse.class);
        } catch (RestClientException ex) {
            throw new AiServiceUnavailableException("Failed to call FastAPI /health endpoint", ex);
        }
    }

    public FastApiIngestResponse ingest(FastApiIngestRequest request) {
        try {
            return aiRestClient.post()
                    .uri("/ingest")
                    .body(request)
                    .retrieve()
                    .body(FastApiIngestResponse.class);
        } catch (RestClientException ex) {
            throw new AiServiceUnavailableException("Failed to call FastAPI /ingest endpoint", ex);
        }
    }
}



