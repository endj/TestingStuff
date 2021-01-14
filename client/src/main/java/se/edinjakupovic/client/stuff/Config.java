package se.edinjakupovic.client.stuff;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import se.edinjakupovic.client.stuff.simulations.OneAtAtime;
import se.edinjakupovic.client.stuff.simulations.ReportFetchTest;

@Configuration
public class Config {

    @Bean
    public ReportClient reportClient(WebClient webClient) {
        return new ReportClient(webClient);
    }

    @Bean
    public WebClient webClient(@Value("${reports.host}") String reportUrl) {
        return WebClient.create(reportUrl);
    }

    @Bean
    public ReportFetchTest oneAtAtime(ReportClient reportClient, FileWriter fileWriter, ReportRepository reportRepository) {
        return new OneAtAtime(reportClient, fileWriter, reportRepository);
    }

    @Bean
    public FileWriter fileWriter(ObjectMapper objectMapper) {
        return new FileWriter(objectMapper);
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public ReportRepository reportRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        return new ReportRepository(namedParameterJdbcTemplate);
    }


}
