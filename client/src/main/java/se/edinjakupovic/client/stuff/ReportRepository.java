package se.edinjakupovic.client.stuff;

import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import java.util.Map;

public class ReportRepository {
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public ReportRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void saveReport(Report report) {
        SqlParameterSource sqlParameterSource = new BeanPropertySqlParameterSource(report);
        int update = namedParameterJdbcTemplate.update(
                "INSERT INTO reports (id, name, age, a, b, c) VALUES (:id, :name, :age, :a, :b, :c)",
                Map.of(
                        "id", report.getId(),
                        "name", report.getName(),
                        "age", report.getAge(),
                        "a", report.getA(),
                        "b", report.getB(),
                        "c", report.getC()
                )
        );
    }
}
