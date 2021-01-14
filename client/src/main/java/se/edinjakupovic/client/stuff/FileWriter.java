package se.edinjakupovic.client.stuff;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class FileWriter {

    private final ObjectMapper objectMapper;

    public FileWriter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }


    @SneakyThrows
    public void writeReport(String fileName, Report report) {
        byte[] bytes = objectMapper.writer().writeValueAsBytes(report);
        Files.write(Path.of("reports/" + fileName), bytes);
    }

    @SneakyThrows
    public List<String> listFiles() {
        return Files.list(Path.of("reports"))
                .map(Path::toString)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public void deleteFile(String fileName) {
        Files.deleteIfExists(Path.of("reports/" + fileName));
    }

    @SneakyThrows
    public Report readReport(String fileName) {
        return objectMapper.reader().readValue(new File(fileName), Report.class);
    }

  /*  public static void main(String[] args) throws IOException {
        FileWriter fileWriter = new FileWriter(new ObjectMapper());
        Report build = Report.builder()
                .a("aaa")
                .b("aaabbbb")
                .c("ccccc")
                .age(123)
                .good(true)
                .id(UUID.randomUUID())
                .name(UUID.randomUUID().toString())
                .build();
        fileWriter.writeReport(build.getId().toString(), build);
        List<String> strings = fileWriter.listFiles();
        System.out.println(strings);
        Report report = fileWriter.readReport(strings.get(0));
        System.out.println(report);
        fileWriter.deleteFile(build.getId().toString());
        List<String> strings1 = fileWriter.listFiles();
        System.out.println(strings1);
    }*/
}
