package se.edinjakupovic.server.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
public class ReportController {


    private final Reports reports;

    public ReportController(Reports reports) {
        this.reports = reports;
    }

    @GetMapping(value = "/reports", produces = APPLICATION_JSON_VALUE)
    public Collection<UUID> getReports() {
        log.trace("/reports");
        return reports.reports();
    }

    @GetMapping(value = "/reports/{id}", produces = APPLICATION_JSON_VALUE)
    public Report getReport(@PathVariable UUID id) {
        log.trace("/reports/{}", id);
        return reports.getReport(id);
    }


}
