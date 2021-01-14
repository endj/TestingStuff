package se.edinjakupovic.client.stuff.simulations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import se.edinjakupovic.client.stuff.FileWriter;
import se.edinjakupovic.client.stuff.Report;
import se.edinjakupovic.client.stuff.ReportClient;
import se.edinjakupovic.client.stuff.ReportRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class OneAtAtime implements ReportFetchTest {

    private final ReportClient reportClient;
    private final FileWriter fileWriter;
    private final ReportRepository reportRepository;

    @Value("${oneattime.list-reports-delay}")
    long delay;

    public OneAtAtime(ReportClient reportClient, FileWriter fileWriter, ReportRepository reportRepository) {
        this.reportClient = reportClient;
        this.fileWriter = fileWriter;
        this.reportRepository = reportRepository;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {

            for (; ; ) {
                List<UUID> reportReferences = reportClient.getReportReferences();
                for (UUID reportReference : reportReferences) {
                    Report report = reportClient.getReport(reportReference);
                    fileWriter.writeReport(report.getId().toString(), report);
                    reportRepository.saveReport(report);
                    fileWriter.deleteFile(report.getId().toString());
                    log.info("saved report {}", report.getId());
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
    }
}
