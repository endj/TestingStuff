package se.edinjakupovic.client.stuff.simulations;

import lombok.extern.slf4j.Slf4j;
import se.edinjakupovic.client.stuff.FileWriter;
import se.edinjakupovic.client.stuff.Report;
import se.edinjakupovic.client.stuff.ReportClient;
import se.edinjakupovic.client.stuff.ReportRepository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class OneAtAtime implements ReportFetchTest {

    private final ReportClient reportClient;
    private final FileWriter fileWriter;
    private final ReportRepository reportRepository;

    public OneAtAtime(ReportClient reportClient, FileWriter fileWriter, ReportRepository reportRepository) {
        this.reportClient = reportClient;
        this.fileWriter = fileWriter;
        this.reportRepository = reportRepository;
    }

    @Override
    public void run() {
        AtomicInteger atomicInteger = new AtomicInteger();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {

            while (atomicInteger.getAndIncrement() < 100) {
                List<UUID> reportReferences = reportClient.getReportReferences();
                for (UUID reportReference : reportReferences) {
                    Report report = reportClient.getReport(reportReference);
                    log.info("fetched report {}", report);
                    fileWriter.writeReport(report.getId().toString(), report);
                    log.info("saved report to {}", report.getId().toString());
                    reportRepository.saveReport(report);
                    log.info("saved report to postgres");
                    fileWriter.deleteFile(report.getId().toString());
                    log.info("Deleted report");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            log.info("## DONE ##");
        });
    }
}
