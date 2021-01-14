package se.edinjakupovic.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import se.edinjakupovic.client.stuff.simulations.ReportFetchTest;

@Slf4j
@SpringBootApplication
public class ClientApplication implements CommandLineRunner {

    private final ReportFetchTest test;


    public ClientApplication(ReportFetchTest reportFetchTest) {
        this.test = reportFetchTest;
    }

    public static void main(String[] args) {
        SpringApplication.run(ClientApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        test.run();
    }
}
