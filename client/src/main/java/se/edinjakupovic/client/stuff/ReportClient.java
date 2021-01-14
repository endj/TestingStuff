package se.edinjakupovic.client.stuff;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Slf4j
public class ReportClient {

    private final WebClient webClient;

    public ReportClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public List<UUID> getReportReferences() {
        return webClient.get()
                .uri("/reports")
                .retrieve()
                .toEntity(new ParameterizedTypeReference<List<UUID>>() {
                })
                .map(HttpEntity::getBody)
                .block();
    }

    public Report getReport(UUID uuid) {
        return webClient.get()
                .uri("/reports/{id}", uuid)
                .retrieve()
                .toEntity(Report.class)
                .map(HttpEntity::getBody)
                .block();
    }
}
