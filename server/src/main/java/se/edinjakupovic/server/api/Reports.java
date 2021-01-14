package se.edinjakupovic.server.api;

import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    @Value("${producer.rate}")
    int reportPerSecond;

    @Value("${producer.total}")
    int maxReports;

    @Value("${producer.data-size}")
    int dataSize;

    @PostConstruct
    public void generateStuff() {
        Random random = new Random(12312425234665L);
        String a = "a".repeat(dataSize);
        String b = "b".repeat(dataSize);
        String c = "c".repeat(dataSize);

        AtomicInteger reps = new AtomicInteger();

        RateLimiter rateLimiter = RateLimiter.create(reportPerSecond);
        ExecutorService generator = Executors.newSingleThreadExecutor();
        generator.execute(() -> {
                    for (; ; ) {
                        rateLimiter.acquire();
                        if (reps.getAndIncrement() > maxReports) {
                            log.info("done");
                            generator.shutdownNow();
                            return;
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

                    }
                }
        );

    }
}
