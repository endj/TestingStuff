package se.edinjakupovic.server.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class Reports {
    private final Map<UUID, Report> reports = new ConcurrentHashMap<>();

    public Set<UUID> reports() {
        return reports.keySet();
    }

    public Report getReport(UUID uuid) {
        return reports.remove(uuid);
    }

    public void addReport(Report report) {
        reports.put(UUID.randomUUID(), report);
    }

    @PostConstruct
    public void generateStuff() {
        Random random = new Random(12312425234665L);
        String a = "a".repeat(512);
        String b = "b".repeat(512);
        String c = "c".repeat(512);

        AtomicInteger reps = new AtomicInteger();

        ScheduledExecutorService generator = Executors.newSingleThreadScheduledExecutor();
        generator.scheduleAtFixedRate(() -> {
                    int andIncrement = reps.getAndIncrement();
                    if (andIncrement > 10000) {
                        log.info("done");
                        generator.shutdownNow();
                    }
                    reports.put(UUID.randomUUID(),
                            Report.builder()
                                    .a(a)
                                    .b(b)
                                    .c(c)
                                    .age(random.nextInt(128))
                                    .good(random.nextBoolean())
                                    .id(UUID.randomUUID())
                                    .name(UUID.randomUUID().toString())
                                    .build());
                },
                0,
                1,
                TimeUnit.SECONDS);

    }
}
