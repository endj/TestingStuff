package se.edinjakupovic.client.stuff.simulations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import se.edinjakupovic.client.stuff.FileWriter;
import se.edinjakupovic.client.stuff.Report;
import se.edinjakupovic.client.stuff.ReportClient;
import se.edinjakupovic.client.stuff.ReportRepository;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

@Slf4j
public class DumpToDiskConcurrent implements ReportFetchTest {

    private final ReportClient reportClient;
    private final FileWriter fileWriter;
    private final ReportRepository reportRepository;


    @Value("${dumptodisk.workers}")
    int workers;

    @Value("${dumptodisk.duration-between-poll-ms}")
    long delay;

    @Value("${dumptodisk.queue-size}")
    int queueSize;

    public DumpToDiskConcurrent(ReportClient reportClient, FileWriter fileWriter, ReportRepository reportRepository) {
        this.reportClient = reportClient;
        this.fileWriter = fileWriter;
        this.reportRepository = reportRepository;
    }

    @Override
    public void run() {
        ExecutorService theBourgeoisie = Executors.newSingleThreadExecutor();
        ExecutorService workingClassPeople = Executors.newFixedThreadPool(workers);
        BlockingDeque<UUID> filesToProcess = new LinkedBlockingDeque<>(queueSize);
        Set<UUID> seen = Collections.newSetFromMap(new ConcurrentHashMap<>());
        theBourgeoisie.execute(new Producer(fileWriter, seen, filesToProcess, reportClient, delay));
        for (int i = 0; i < workers; i++) {
            workingClassPeople.execute(new Consoomer(filesToProcess, fileWriter, reportRepository, seen));
        }
    }


    @Slf4j
    static class Producer implements Runnable {

        private final FileWriter fileWriter;
        private final Set<UUID> seen;
        private final BlockingDeque<UUID> filesToProcess;
        private final ReportClient reportClient;
        private final long delay;

        public Producer(FileWriter fileWriter, Set<UUID> seen, BlockingDeque<UUID> filesToProcess, ReportClient reportClient,
                        long delay) {
            this.fileWriter = fileWriter;
            this.seen = seen;
            this.filesToProcess = filesToProcess;
            this.reportClient = reportClient;
            this.delay = delay;
        }

        @Override
        public void run() {
            fileWriter.listFiles()
                    .forEach(fileName -> {
                        UUID uuid = UUID.fromString(fileName);
                        seen.add(uuid);
                        filesToProcess.offerFirst(uuid);
                    });
            log.info("Checked existing files {}", fileWriter.listFiles());

            for (; ; ) {
                List<UUID> reportReferences = reportClient.getReportReferences();
                log.info("Found references {}", reportReferences);
                for (UUID reportReference : reportReferences) {

                    if (seen.contains(reportReference)) {
                        continue;
                    }

                    Report report = reportClient.getReport(reportReference);
                    seen.add(report.getId());
                    fileWriter.writeReport(report.getId().toString(), report);
                    filesToProcess.offerFirst(report.getId());
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    return;
                }
                log.info("QueueSize {}", filesToProcess.size());
            }
        }
    }

    @Slf4j
    static class Consoomer implements Runnable {
        private final BlockingDeque<UUID> filesToProcess;
        private final FileWriter fileWriter;
        private final ReportRepository reportRepository;
        private final Set<UUID> seen;

        public Consoomer(BlockingDeque<UUID> filesToProcess, FileWriter fileWriter, ReportRepository reportRepository, Set<UUID> seen) {
            this.filesToProcess = filesToProcess;
            this.fileWriter = fileWriter;
            this.reportRepository = reportRepository;
            this.seen = seen;
        }

        @Override
        public void run() {
            for (; ; ) {
                try {
                    UUID uuid = filesToProcess.takeLast();
                    log.info("processing {}", uuid);
                    Report report = fileWriter.readReport(uuid.toString());
                    reportRepository.saveReport(report);
                    fileWriter.deleteFile(uuid.toString());
                    seen.remove(uuid);
                } catch (InterruptedException e) {
                    return;
                }
            }
        }
    }
}