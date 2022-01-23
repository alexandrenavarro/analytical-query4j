package com.github.alexandrenavarro.analyticalquery4j;

import com.github.alexandrenavarro.analyticalquery4j.core.AnalyticalQuery;
import com.github.alexandrenavarro.analyticalquery4j.description.CubeDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.DimensionDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.FactTableDescription;
import com.github.alexandrenavarro.analyticalquery4j.description.MeasureDescription;
import com.github.alexandrenavarro.analyticalquery4j.sql.SqlAnalyticalQueryMapper;
import com.github.alexandrenavarro.analyticalquery4j.sql.SqlQuery;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@AllArgsConstructor
@Slf4j
public class AnalyticalQuerySampleApp implements CommandLineRunner {

  private final CubeDescription cubeDescription = CubeDescription.builder()
    .measureDescriptions(List.of(
      MeasureDescription.builder()
        .name("amount")
        .columnWithFunction("amount")
        .aggregable(false)
        .build(),
      MeasureDescription.builder()
        .name("sumOfAmount")
        .columnWithFunction("SUM(amount)")
        .aggregable(true)
        .build(),
      MeasureDescription.builder()
        .name("countOfSales")
        .columnWithFunction("COUNT(*)")
        .aggregable(true)
        .build()
    ))
    .dimensionDescriptions(List.of(
        DimensionDescription.builder()
          .name("model")
          .columnWithFunction("model")
          .build(),
      DimensionDescription.builder()
        .name("color")
        .columnWithFunction("color")
        .build(),
      DimensionDescription.builder()
        .name("salesDate")
        .columnWithFunction("salesDate")
        .build()))
    .factTableDescription(FactTableDescription.builder()
      .factTable("sales")
      .build())
    .build();

  private final SqlAnalyticalQueryMapper sqlAnalyticalQueryMapper = new SqlAnalyticalQueryMapper(cubeDescription);

  private final JdbcTemplate jdbcTemplate;

  @Override
  public void run(String... args) throws Exception {
    jdbcTemplate.execute("CREATE TABLE sales (amount NUMERIC, model VARCHAR(64), color VARCHAR(64), salesDate TIMESTAMP );");
    jdbcTemplate.update(
      "INSERT INTO sales VALUES (?, ?, ?, ?)", 30000, "Model1", "Blue", LocalDate.now());
    jdbcTemplate.update(
      "INSERT INTO sales VALUES (?, ?, ?, ?)", 30000, "Model1", "Red", LocalDate.now().minusDays(1));
    jdbcTemplate.update(
      "INSERT INTO sales VALUES (?, ?, ?, ?)", 20000, "Model2", "Green", LocalDate.now().minusMonths(1));
  }

  @PostMapping("/sales/search")
  public List<Sales> search(@RequestBody @Valid final AnalyticalQuery analyticalQuery) {
    final SqlQuery sqlQuery = sqlAnalyticalQueryMapper.mapToSqlQuery(analyticalQuery);
    final List<Sales> sales =
      jdbcTemplate.query(sqlQuery.getSqlRequest(), new BeanPropertyRowMapper<>(Sales.class), sqlQuery.getParameters().toArray());
    return sales;
  }

  @AllArgsConstructor
  @Getter
  @Setter // Needed for BeanPropertyRowMapper
  @EqualsAndHashCode
  @ToString
  @Builder
  @Jacksonized
  @NoArgsConstructor // Needed for BeanPropertyRowMapper
  static class Sales {
    private BigDecimal sumOfAmount;
    private BigDecimal countOfSales;
    private BigDecimal amount;
    private String model;
    private String color;
    private LocalDate salesDate;
  }

  public static void main(String[] args) {
    SpringApplication.run(AnalyticalQuerySampleApp.class, args);
  }
}



